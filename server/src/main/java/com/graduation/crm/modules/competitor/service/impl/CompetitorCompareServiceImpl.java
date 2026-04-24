package com.graduation.crm.modules.competitor.service.impl;

import com.graduation.crm.modules.ai.client.QwenAiClient;
import com.graduation.crm.modules.ai.dto.AiChatDTO;
import com.graduation.crm.modules.ai.vo.AiChatVO;
import com.graduation.crm.modules.competitor.dto.CompetitorCompareDTO;
import com.graduation.crm.modules.competitor.entity.CustomerCompetitorFocus;
import com.graduation.crm.modules.competitor.mapper.CustomerCompetitorFocusMapper;
import com.graduation.crm.modules.competitor.service.CompetitorCompareService;
import com.graduation.crm.modules.competitor.vo.CompetitorAiResponseVO;
import com.graduation.crm.modules.competitor.vo.CompetitorCompareVO;
import com.graduation.crm.modules.competitor.vo.CustomerCompetitorFocusVO;
import com.graduation.crm.modules.customer.service.CustomerService;
import com.graduation.crm.modules.customer.vo.CustomerDetailVO;
import com.graduation.crm.modules.project.service.ProjectService;
import com.graduation.crm.modules.project.vo.CompetitorProjectVO;
import com.graduation.crm.modules.project.vo.ProjectVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitorCompareServiceImpl implements CompetitorCompareService {

    private final CustomerService customerService;
    private final ProjectService projectService;
    private final QwenAiClient qwenAiClient;
    private final CustomerCompetitorFocusMapper focusMapper;

    @Override
    public CompetitorCompareVO compare(CompetitorCompareDTO dto) {
        CustomerDetailVO customer = customerService.detail(dto.getCustomerId());
        ProjectVO project = projectService.projectDetail(dto.getProjectId());
        CompetitorProjectVO competitor = projectService.competitorDetail(dto.getCompetitorId());
        return buildCompareVO(dto, customer, project, competitor);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompetitorAiResponseVO aiResponse(CompetitorCompareDTO dto) {
        CompetitorCompareVO compare = compare(dto);
        saveFocus(dto);

        AiChatDTO chatDTO = new AiChatDTO();
        chatDTO.setBizType("COMPETITOR_COMPARE");
        chatDTO.setBizId(dto.getCustomerId());
        chatDTO.setPrompt(buildPrompt(compare));
        AiChatVO chatVO = qwenAiClient.chat(chatDTO);

        CompetitorAiResponseVO vo = new CompetitorAiResponseVO();
        vo.setResponseText(chatVO.getContent());
        vo.setModelName(chatVO.getModelName());
        vo.setTokenUsage(chatVO.getTokenUsage());
        return vo;
    }

    @Override
    public List<CustomerCompetitorFocusVO> history(Long customerId) {
        return focusMapper.selectByCustomerId(customerId);
    }

    private CompetitorCompareVO buildCompareVO(CompetitorCompareDTO dto, CustomerDetailVO customer,
                                               ProjectVO project, CompetitorProjectVO competitor) {
        CompetitorCompareVO vo = new CompetitorCompareVO();
        vo.setCustomerId(customer.getId());
        vo.setCustomerName(customer.getCustomerName());
        vo.setProjectId(project.getId());
        vo.setProjectName(project.getProjectName());
        vo.setProjectAvgPrice(project.getAvgPrice());
        vo.setProjectHighlights(project.getHighlights());
        vo.setProjectDiscountInfo(project.getDiscountInfo());
        vo.setProjectHandoverDate(project.getHandoverDate());
        vo.setCompetitorId(competitor.getId());
        vo.setCompetitorName(competitor.getProjectName());
        vo.setCompetitorAvgPrice(competitor.getAvgPrice());
        vo.setCompetitorHighlights(competitor.getHighlights());
        vo.setCompetitorWeakness(competitor.getWeakness());
        vo.setCompetitorDiscountInfo(competitor.getDiscountInfo());
        vo.setCompetitorHandoverDate(competitor.getHandoverDate());
        vo.setCustomerConcern(dto.getCustomerConcern());
        vo.setCompareSummary(buildSummary(project, competitor));
        return vo;
    }

    private String buildSummary(ProjectVO project, CompetitorProjectVO competitor) {
        StringBuilder summary = new StringBuilder();
        if (project.getAvgPrice() != null && competitor.getAvgPrice() != null) {
            int diff = project.getAvgPrice().subtract(competitor.getAvgPrice()).intValue();
            if (diff > 0) {
                summary.append("本楼盘均价高于竞品约").append(diff).append("元/㎡，需要突出交付、配套或品质差异。");
            } else if (diff < 0) {
                summary.append("本楼盘均价低于竞品约").append(Math.abs(diff)).append("元/㎡，可强调性价比。");
            } else {
                summary.append("本楼盘与竞品均价接近，可重点比较交付时间和产品卖点。");
            }
        }
        return summary.toString();
    }

    private String buildPrompt(CompetitorCompareVO compare) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是房地产销售竞品应对话术助手。请基于以下信息生成一句话应对模板。");
        prompt.append("要求：中文输出，专业自然，不攻击竞品，不夸大承诺，80字以内，可直接发给顾问参考。");
        prompt.append("\n客户：").append(compare.getCustomerName());
        prompt.append("\n客户关注点：").append(compare.getCustomerConcern());
        prompt.append("\n本楼盘：").append(compare.getProjectName());
        prompt.append("\n本楼盘均价：").append(compare.getProjectAvgPrice());
        prompt.append("\n本楼盘卖点：").append(compare.getProjectHighlights());
        prompt.append("\n本楼盘优惠：").append(compare.getProjectDiscountInfo());
        prompt.append("\n本楼盘交付时间：").append(compare.getProjectHandoverDate());
        prompt.append("\n竞品楼盘：").append(compare.getCompetitorName());
        prompt.append("\n竞品均价：").append(compare.getCompetitorAvgPrice());
        prompt.append("\n竞品卖点：").append(compare.getCompetitorHighlights());
        prompt.append("\n竞品弱点：").append(compare.getCompetitorWeakness());
        prompt.append("\n竞品优惠：").append(compare.getCompetitorDiscountInfo());
        prompt.append("\n竞品交付时间：").append(compare.getCompetitorHandoverDate());
        prompt.append("\n对比摘要：").append(compare.getCompareSummary());
        return prompt.toString();
    }

    private void saveFocus(CompetitorCompareDTO dto) {
        CustomerCompetitorFocus focus = new CustomerCompetitorFocus();
        focus.setCustomerId(dto.getCustomerId());
        focus.setCompetitorId(dto.getCompetitorId());
        focus.setFocusContent(dto.getCustomerConcern());
        focus.setCreatedBy(dto.getCreatedBy());
        focusMapper.insert(focus);
    }
}

