package com.graduation.crm.modules.report.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConversionReportVO {

    private Integer totalCustomerCount;
    private Integer visitedCustomerCount;
    private Integer dealCustomerCount;
    private Integer lostCustomerCount;
    private BigDecimal visitRate;
    private BigDecimal dealRate;
    private BigDecimal lostRate;
}

