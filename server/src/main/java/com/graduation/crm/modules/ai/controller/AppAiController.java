package com.graduation.crm.modules.ai.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.ai.dto.AiChatDTO;
import com.graduation.crm.modules.ai.dto.LeadConfirmDTO;
import com.graduation.crm.modules.ai.dto.LeadExtractDTO;
import com.graduation.crm.modules.ai.dto.ScriptGenerateDTO;
import com.graduation.crm.modules.ai.service.AiService;
import com.graduation.crm.modules.ai.vo.AiChatVO;
import com.graduation.crm.modules.ai.vo.LeadExtractVO;
import com.graduation.crm.modules.ai.vo.ScriptGenerateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;

/**
 * 小程序端 AI 接口。
 */
@Tag(name = "移动端AI接口")
@RestController
@RequestMapping("/api/app/ai")
@RequiredArgsConstructor
public class AppAiController {

    private final AiService aiService;

    @PostMapping("/chat")
    @Operation(summary = "AI通用对话")
    public Result<AiChatVO> chat(@RequestBody @Valid AiChatDTO dto) {
        return Result.success(aiService.chat(dto));
    }

    /**
     * AI 流式对话接口，适合前端逐字展示模型输出。
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI流式对话")
    public SseEmitter chatStream(@RequestBody @Valid AiChatDTO dto) {
        return aiService.chatStream(dto);
    }

    @PostMapping("/scripts/generate")
    @Operation(summary = "生成跟进话术")
    public Result<ScriptGenerateVO> generateScript(@RequestBody @Valid ScriptGenerateDTO dto) {
        return Result.success(aiService.generateScript(dto));
    }

    /**
     * 智能线索录入，从聊天文本、图片 URL 或 base64 音频中抽取客户购房需求字段。
     */
    @PostMapping("/leads/extract")
    @Operation(summary = "智能线索抽取")
    public Result<LeadExtractVO> extractLead(@RequestBody @Valid LeadExtractDTO dto) {
        return Result.success(aiService.extractLead(dto));
    }

    /**
     * 确认抽取结果并创建客户，前端可在提交前允许顾问修正字段。
     */
    @PostMapping("/leads/confirm")
    @Operation(summary = "确认线索入库")
    public Result<Long> confirmLead(@RequestBody @Valid LeadConfirmDTO dto) {
        return Result.success(aiService.confirmLead(dto));
    }
}
