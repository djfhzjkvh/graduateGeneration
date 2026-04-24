package com.graduation.crm.modules.dashboard.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.dashboard.service.DashboardService;
import com.graduation.crm.modules.dashboard.vo.AdminOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端首页看板接口。
 *
 * 面向管理员和经理后台首页，提供全局经营概况、任务状态和近日报表数据。
 */
@Tag(name = "管理端首页看板接口")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    /**
     * 管理端总览数据，用于后台首页指标卡片和图表展示。
     */
    @GetMapping("/overview")
    @Operation(summary = "管理端首页总览")
    public Result<AdminOverviewVO> overview() {
        return Result.success(dashboardService.adminOverview());
    }
}

