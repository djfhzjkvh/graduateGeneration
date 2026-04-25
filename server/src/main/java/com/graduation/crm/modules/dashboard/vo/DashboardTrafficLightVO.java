package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardTrafficLightVO {
    private String metricCode;
    private String metricName;
    private BigDecimal metricValue;
    private String unit;
    private String lightStatus;
    private String riskLevel;
    private String description;
}
