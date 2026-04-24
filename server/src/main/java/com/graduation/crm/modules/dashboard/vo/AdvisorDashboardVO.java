package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

@Data
public class AdvisorDashboardVO {

    private Integer customerCount;
    private Integer followingCount;
    private Integer dealCount;
    private Integer pendingTaskCount;
    private Integer highIntentCustomerCount;
}

