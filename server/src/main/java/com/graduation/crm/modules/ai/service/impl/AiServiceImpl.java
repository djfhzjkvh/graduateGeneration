package com.graduation.crm.modules.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.modules.ai.client.QwenAiClient;
import com.graduation.crm.modules.ai.dto.AiChatDTO;
import com.graduation.crm.modules.ai.dto.LeadConfirmDTO;
import com.graduation.crm.modules.ai.dto.LeadExtractDTO;
import com.graduation.crm.modules.ai.dto.ScriptGenerateDTO;
import com.graduation.crm.modules.ai.entity.LeadExtractRecord;
import com.graduation.crm.modules.ai.mapper.LeadExtractRecordMapper;
import com.graduation.crm.modules.ai.service.AiService;
import com.graduation.crm.modules.ai.vo.AiChatVO;
import com.graduation.crm.modules.ai.vo.LeadExtractVO;
import com.graduation.crm.modules.ai.vo.ScriptGenerateVO;
import com.graduation.crm.modules.customer.service.CustomerService;
import com.graduation.crm.modules.customer.vo.CustomerDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final QwenAiClient qwenAiClient;
    private final CustomerService customerService;
    private final LeadExtractRecordMapper leadExtractRecordMapper;
    private final ObjectMapper objectMapper;

    @Override
    public AiChatVO chat(AiChatDTO dto) {
        return qwenAiClient.chat(dto);
    }

    @Override
    public SseEmitter chatStream(AiChatDTO dto) {
        return qwenAiClient.chatStream(dto);
    }

    @Override
    public ScriptGenerateVO generateScript(ScriptGenerateDTO dto) {
        CustomerDetailVO customer = customerService.detail(dto.getCustomerId());
        AiChatDTO chatDTO = new AiChatDTO();
        chatDTO.setBizType("SCRIPT");
        chatDTO.setBizId(dto.getCustomerId());
        chatDTO.setPrompt(buildScriptPrompt(dto, customer));
        AiChatVO chatVO = qwenAiClient.chat(chatDTO);

        ScriptGenerateVO vo = new ScriptGenerateVO();
        vo.setScriptText(chatVO.getContent());
        vo.setModelName(chatVO.getModelName());
        vo.setTokenUsage(chatVO.getTokenUsage());
        return vo;
    }

    @Override
    public LeadExtractVO extractLead(LeadExtractDTO dto) {
        AiChatDTO chatDTO = new AiChatDTO();
        chatDTO.setBizType("LEAD_EXTRACT");
        chatDTO.setPrompt(buildLeadExtractPrompt(dto));
        chatDTO.setImageUrls(dto.getImageUrls());
        chatDTO.setAudios(dto.getAudios());
        AiChatVO chatVO = qwenAiClient.chat(chatDTO);

        LeadExtractVO vo = parseLeadExtract(chatVO.getContent());
        vo.setRawModelOutput(chatVO.getContent());

        LeadExtractRecord record = new LeadExtractRecord();
        record.setSourceType(dto.getSourceType());
        record.setSourceFileId(dto.getSourceFileId());
        record.setRawText(dto.getRawText());
        record.setCleanedText(dto.getRawText());
        record.setExtractJson(toJson(vo));
        record.setSuggestionJson(toJson(Collections.singletonMap("suggestion", vo.getSuggestion())));
        record.setConfirmStatus("PENDING");
        record.setModelName(chatVO.getModelName());
        record.setPromptVersion("v1");
        record.setCreatedBy(dto.getCreatedBy());
        leadExtractRecordMapper.insert(record);

        vo.setExtractId(record.getId());
        return vo;
    }

    @Override
    public Long confirmLead(LeadConfirmDTO dto) {
        LeadExtractRecord record = leadExtractRecordMapper.selectOne(new LambdaQueryWrapper<LeadExtractRecord>()
                .eq(LeadExtractRecord::getId, dto.getExtractId()));
        if (record == null) {
            throw new BusinessException("抽取记录不存在");
        }
        if ("CONFIRMED".equals(record.getConfirmStatus())) {
            throw new BusinessException("抽取记录已确认");
        }
        Long customerId = customerService.create(dto.getCustomerForm());
        record.setConfirmStatus("CONFIRMED");
        record.setCustomerId(customerId);
        leadExtractRecordMapper.updateById(record);
        return customerId;
    }

    private String buildScriptPrompt(ScriptGenerateDTO dto, CustomerDetailVO customer) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是房地产置业顾问的跟进话术助手。请根据客户信息生成专业、自然、可直接发送的话术。");
        prompt.append("要求：中文输出，语气真诚，不夸大承诺，不超过180字。");
        prompt.append("\n场景类型：").append(dto.getSceneType());
        prompt.append("\n渠道类型：").append(dto.getChannelType());
        prompt.append("\n客户姓名：").append(customer.getCustomerName());
        prompt.append("\n客户状态：").append(customer.getStatus());
        prompt.append("\n意向等级：").append(customer.getIntentLevel());
        prompt.append("\n预算：").append(customer.getBudgetMin()).append("-").append(customer.getBudgetMax());
        prompt.append("\n意向区域：").append(customer.getRegion());
        prompt.append("\n意向户型：").append(customer.getHouseType());
        prompt.append("\n购房目的：").append(customer.getPurpose());
        prompt.append("\n客户标签：").append(customer.getTagNames());
        if (dto.getCustomPrompt() != null && !dto.getCustomPrompt().trim().isEmpty()) {
            prompt.append("\n补充要求：").append(dto.getCustomPrompt());
        }
        return prompt.toString();
    }

    private String buildLeadExtractPrompt(LeadExtractDTO dto) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是房地产客户线索信息抽取助手。请从输入的聊天文本、图片或音频中抽取购房客户信息。");
        prompt.append("只返回 JSON，不要返回 Markdown，不要解释。字段固定为：");
        prompt.append("customerName,mobile,gender,source,intentLevel,budgetMin,budgetMax,region,houseType,purpose,visitTime,remark,suggestion。");
        prompt.append("无法确定的字段返回 null。intentLevel 只能是 LOW、MEDIUM、HIGH。预算单位统一为元。");
        prompt.append("\n来源类型：").append(dto.getSourceType());
        if (dto.getRawText() != null && !dto.getRawText().trim().isEmpty()) {
            prompt.append("\n待抽取文本：").append(dto.getRawText());
        } else {
            prompt.append("\n请根据随请求提供的图片或音频内容抽取字段。");
        }
        return prompt.toString();
    }

    private LeadExtractVO parseLeadExtract(String content) {
        try {
            String json = normalizeJson(content);
            JsonNode root = objectMapper.readTree(json);
            LeadExtractVO vo = new LeadExtractVO();
            vo.setCustomerName(textOrNull(root, "customerName"));
            vo.setMobile(textOrNull(root, "mobile"));
            vo.setGender(textOrNull(root, "gender"));
            vo.setSource(textOrNull(root, "source"));
            vo.setIntentLevel(textOrNull(root, "intentLevel"));
            vo.setBudgetMin(decimalOrNull(root, "budgetMin"));
            vo.setBudgetMax(decimalOrNull(root, "budgetMax"));
            vo.setRegion(textOrNull(root, "region"));
            vo.setHouseType(textOrNull(root, "houseType"));
            vo.setPurpose(textOrNull(root, "purpose"));
            vo.setVisitTime(textOrNull(root, "visitTime"));
            vo.setRemark(textOrNull(root, "remark"));
            vo.setSuggestion(textOrNull(root, "suggestion"));
            return vo;
        } catch (Exception e) {
            throw new BusinessException("AI抽取结果解析失败，请重试或改用文本录入");
        }
    }

    private String normalizeJson(String content) {
        String text = content == null ? "" : content.trim();
        if (text.startsWith("```")) {
            text = text.replaceFirst("^```json", "").replaceFirst("^```", "");
            if (text.endsWith("```")) {
                text = text.substring(0, text.length() - 3);
            }
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private String textOrNull(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }

    private java.math.BigDecimal decimalOrNull(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull() || "".equals(node.asText())) {
            return null;
        }
        return new java.math.BigDecimal(node.asText());
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "{}";
        }
    }
}
