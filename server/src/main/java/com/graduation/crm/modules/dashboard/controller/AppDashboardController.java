package com.graduation.crm.modules.dashboard.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.dashboard.service.DashboardService;
import com.graduation.crm.modules.dashboard.vo.AdvisorDashboardVO;
import com.graduation.crm.modules.dashboard.vo.AppWorkbenchVO;
import com.graduation.crm.modules.dashboard.vo.ManagerDashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序端工作台接口。
 *
 * 当前通过 userId 参数模拟登录用户，等全局鉴权拦截器接入后会改为从 token 中获取。
 */
@Tag(name = "移动端工作台接口")
@RestController
@RequestMapping("/api/app/dashboard")
@RequiredArgsConstructor
public class AppDashboardController {

    private final DashboardService dashboardService;

    /**
     * 小程序首页工作台，返回今日任务、高意向客户和关键统计卡片。
     */
    @GetMapping("/workbench")
    @Operation(summary = "移动端工作台")
    public Result<AppWorkbenchVO> workbench(@RequestParam Long userId) {
        return Result.success(dashboardService.appWorkbench(userId));
    }

    /**
     * 顾问个人看板，展示个人客户、成交、待办和高意向概况。
     */
    @GetMapping("/advisor")
    @Operation(summary = "顾问看板")
    public Result<AdvisorDashboardVO> advisor(@RequestParam Long userId) {
        return Result.success(dashboardService.advisorDashboard(userId));
    }

    /**
     * 经理团队看板，展示团队客户、任务和顾问排行。
     */
    @GetMapping("/manager")
    @Operation(summary = "经理看板")
    public Result<ManagerDashboardVO> manager(@RequestParam Long managerId) {
        return Result.success(dashboardService.managerDashboard(managerId));
    }
}

