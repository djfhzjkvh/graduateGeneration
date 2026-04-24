package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyStatVO {

    private LocalDate statDate;
    private Integer newCustomerCount;
    private Integer highIntentCount;
    private Integer pendingTaskCount;
    private BigDecimal expectedAmount;
    private Integer forgetTaskCount;
}

