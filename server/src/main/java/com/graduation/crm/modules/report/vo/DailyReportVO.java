package com.graduation.crm.modules.report.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyReportVO {

    private LocalDate statDate;
    private Integer newCustomerCount;
    private Integer highIntentCount;
    private Integer pendingTaskCount;
    private BigDecimal expectedAmount;
    private Integer forgetTaskCount;
}

