package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDrilldownVO {
    private String metricCode;
    private String metricName;
    private Integer total;
    private List<SimpleCustomerVO> customers;
    private List<SimpleTaskVO> tasks;
}
