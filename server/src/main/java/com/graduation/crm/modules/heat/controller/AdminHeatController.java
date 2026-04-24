package com.graduation.crm.modules.heat.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.heat.service.HeatService;
import com.graduation.crm.modules.heat.vo.HighIntentCustomerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端热度评分接口。
 *
 * 面向经理和管理员，用于查看团队高意向客户并批量刷新热度分。
 */
@Tag(name = "管理端热度接口")
@RestController
@RequestMapping("/api/admin/heat")
@RequiredArgsConstructor
public class AdminHeatController {

    private final HeatService heatService;

    /**
     * 管理端高意向客户池，可按经理过滤团队客户。
     */
    @GetMapping("/high-intent")
    @Operation(summary = "管理端高意向客户池")
    public Result<List<HighIntentCustomerVO>> highIntent(@RequestParam(required = false) Long managerId) {
        return Result.success(heatService.highIntent(null, managerId));
    }

    /**
     * 批量刷新所有未删除客户热度，用于演示或后续定时任务。
     */
    @PostMapping("/batch-calculate")
    @Operation(summary = "批量计算客户热度")
    public Result<Integer> batchCalculate() {
        return Result.success(heatService.batchCalculate());
    }
}

