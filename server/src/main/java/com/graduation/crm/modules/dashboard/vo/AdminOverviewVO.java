package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminOverviewVO {

    private Integer totalCustomerCount;
    private Integer newCustomerCount;
    private Integer highIntentCustomerCount;
    private Integer pendingTaskCount;
    private Integer overdueTaskCount;
    private Integer dealCustomerCount;
    private List<DailyStatVO> dailyStats;
    private List<TaskStatusStatVO> taskStats;
}

