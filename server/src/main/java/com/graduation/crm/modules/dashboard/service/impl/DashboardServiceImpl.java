package com.graduation.crm.modules.dashboard.service.impl;

import com.graduation.crm.modules.admin.service.SystemConfigService;
import com.graduation.crm.modules.dashboard.mapper.DashboardMapper;
import com.graduation.crm.modules.dashboard.service.DashboardService;
import com.graduation.crm.modules.dashboard.vo.AdminOverviewVO;
import com.graduation.crm.modules.dashboard.vo.AdvisorDashboardVO;
import com.graduation.crm.modules.dashboard.vo.AppWorkbenchVO;
import com.graduation.crm.modules.dashboard.vo.ManagerDashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;
    private final SystemConfigService systemConfigService;

    @Override
    public AppWorkbenchVO appWorkbench(Long userId) {
        AppWorkbenchVO vo = new AppWorkbenchVO();
        Integer threshold = systemConfigService.getHighIntentThreshold();
        // 小程序工作台默认按当前顾问维度展示，经理可通过 managerDashboard 查看团队数据。
        vo.setTodayTaskCount(dashboardMapper.countTodayTasks(userId));
        vo.setOverdueTaskCount(dashboardMapper.countOverdueTasks(userId));
        vo.setHighIntentCustomerCount(dashboardMapper.countHighIntentCustomers(userId, null, threshold));
        vo.setNewCustomerCount(dashboardMapper.countNewCustomers(userId, null));
        vo.setTodayTasks(dashboardMapper.selectTodayTasks(userId, 5));
        vo.setHighIntentCustomers(dashboardMapper.selectHighIntentCustomers(userId, null, threshold, 5));
        return vo;
    }

    @Override
    public AdvisorDashboardVO advisorDashboard(Long userId) {
        AdvisorDashboardVO vo = new AdvisorDashboardVO();
        Integer threshold = systemConfigService.getHighIntentThreshold();
        // 顾问看板聚焦个人客户池和个人待办，用于小程序“我的看板”。
        vo.setCustomerCount(dashboardMapper.countCustomers(userId, null, null));
        vo.setFollowingCount(dashboardMapper.countCustomers(userId, null, "FOLLOWING"));
        vo.setDealCount(dashboardMapper.countCustomers(userId, null, "DEAL"));
        vo.setPendingTaskCount(dashboardMapper.countPendingTasks(userId, null));
        vo.setHighIntentCustomerCount(dashboardMapper.countHighIntentCustomers(userId, null, threshold));
        return vo;
    }

    @Override
    public ManagerDashboardVO managerDashboard(Long managerId) {
        ManagerDashboardVO vo = new ManagerDashboardVO();
        Integer threshold = systemConfigService.getHighIntentThreshold();
        // 经理看板按 manager_id 汇总团队数据，后续可替换为 dept_id 数据范围。
        vo.setTeamCustomerCount(dashboardMapper.countCustomers(null, managerId, null));
        vo.setTeamPendingTaskCount(dashboardMapper.countPendingTasks(null, managerId));
        vo.setTeamOverdueTaskCount(dashboardMapper.countOverdueTasks(null));
        vo.setTeamHighIntentCustomerCount(dashboardMapper.countHighIntentCustomers(null, managerId, threshold));
        vo.setAdvisorRankList(dashboardMapper.selectAdvisorRank(managerId, threshold));
        return vo;
    }

    @Override
    public AdminOverviewVO adminOverview() {
        AdminOverviewVO vo = new AdminOverviewVO();
        Integer threshold = systemConfigService.getHighIntentThreshold();
        // 管理端总览不限制数据范围，用于后台首页展示全局经营概况。
        vo.setTotalCustomerCount(dashboardMapper.countCustomers(null, null, null));
        vo.setNewCustomerCount(dashboardMapper.countNewCustomers(null, null));
        vo.setHighIntentCustomerCount(dashboardMapper.countHighIntentCustomers(null, null, threshold));
        vo.setPendingTaskCount(dashboardMapper.countPendingTasks(null, null));
        vo.setOverdueTaskCount(dashboardMapper.countOverdueTasks(null));
        vo.setDealCustomerCount(dashboardMapper.countCustomers(null, null, "DEAL"));
        vo.setDailyStats(dashboardMapper.selectDailyStats(7));
        vo.setTaskStats(dashboardMapper.selectTaskStatusStats());
        return vo;
    }
}
