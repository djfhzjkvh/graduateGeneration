package com.graduation.crm.modules.dashboard.service;

import com.graduation.crm.modules.dashboard.vo.AdminOverviewVO;
import com.graduation.crm.modules.dashboard.vo.AdvisorDashboardVO;
import com.graduation.crm.modules.dashboard.vo.AppWorkbenchVO;
import com.graduation.crm.modules.dashboard.vo.ManagerDashboardVO;

public interface DashboardService {

    AppWorkbenchVO appWorkbench(Long userId);

    AdvisorDashboardVO advisorDashboard(Long userId);

    ManagerDashboardVO managerDashboard(Long managerId);

    AdminOverviewVO adminOverview();
}

