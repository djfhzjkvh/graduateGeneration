package com.graduation.crm.modules.prediction.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PredictionOverviewVO {
    private Integer customerCount;
    private Integer expectedDealCount;
    private BigDecimal expectedAmount;
    private BigDecimal avgProbability;
    private BigDecimal highProbabilityAmount;
}
