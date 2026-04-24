package com.graduation.crm.modules.admin.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.admin.dto.AiLogQueryDTO;
import com.graduation.crm.modules.admin.dto.OperLogQueryDTO;
import com.graduation.crm.modules.admin.service.AdminConfigService;
import com.graduation.crm.modules.admin.vo.AiCallLogVO;
import com.graduation.crm.modules.admin.vo.OperLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端日志接口。
 *
 * 用于追踪 AI 调用情况和系统关键操作，支撑问题排查和论文中的可追溯设计。
 */
@Tag(name = "管理端日志接口")
@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class AdminLogController {

    private final AdminConfigService adminConfigService;

    /**
     * 分页查询 AI 调用日志，支持业务类型、状态和日期筛选。
     */
    @GetMapping("/ai")
    @Operation(summary = "AI调用日志")
    public Result<PageResult<AiCallLogVO>> aiLogs(AiLogQueryDTO queryDTO) {
        return Result.success(adminConfigService.pageAiLogs(queryDTO));
    }

    /**
     * 分页查询操作日志，支持用户、业务类型、操作类型和日期筛选。
     */
    @GetMapping("/operation")
    @Operation(summary = "操作日志")
    public Result<PageResult<OperLogVO>> operationLogs(OperLogQueryDTO queryDTO) {
        return Result.success(adminConfigService.pageOperLogs(queryDTO));
    }
}

