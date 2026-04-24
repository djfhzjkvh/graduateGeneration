package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

import java.util.List;

@Data
public class AppWorkbenchVO {

    private Integer todayTaskCount;
    private Integer overdueTaskCount;
    private Integer highIntentCustomerCount;
    private Integer newCustomerCount;
    private List<SimpleTaskVO> todayTasks;
    private List<SimpleCustomerVO> highIntentCustomers;
}

