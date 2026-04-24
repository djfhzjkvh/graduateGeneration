package com.graduation.crm.modules.report.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SourceReportVO {

    private String source;
    private Integer customerCount;
    private Integer dealCustomerCount;
    private BigDecimal dealRate;
}

