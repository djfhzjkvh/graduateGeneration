package com.graduation.crm.modules.competitor.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CompetitorCompareVO {

    private Long customerId;
    private String customerName;
    private Long projectId;
    private String projectName;
    private BigDecimal projectAvgPrice;
    private String projectHighlights;
    private String projectDiscountInfo;
    private LocalDate projectHandoverDate;
    private Long competitorId;
    private String competitorName;
    private BigDecimal competitorAvgPrice;
    private String competitorHighlights;
    private String competitorWeakness;
    private String competitorDiscountInfo;
    private LocalDate competitorHandoverDate;
    private String customerConcern;
    private String compareSummary;
}

