package com.graduation.crm.modules.ai.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.ai.dto.LeadExtractRecordQueryDTO;
import com.graduation.crm.modules.ai.service.AiService;
import com.graduation.crm.modules.ai.vo.LeadExtractRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 AI 业务接口。
 */
@Tag(name = "管理端AI接口")
@RestController
@RequestMapping("/api/admin/ai")
@RequiredArgsConstructor
public class AdminAiController {

    private final AiService aiService;

    /**
     * 查询 AI 线索抽取历史，供后台审计智能录入效果。
     */
    @GetMapping("/leads/extract-records")
    @Operation(summary = "AI线索抽取记录")
    public Result<PageResult<LeadExtractRecordVO>> extractRecords(LeadExtractRecordQueryDTO queryDTO) {
        return Result.success(aiService.pageLeadExtractRecords(queryDTO));
    }
}
