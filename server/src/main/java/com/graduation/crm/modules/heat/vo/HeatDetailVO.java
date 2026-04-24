package com.graduation.crm.modules.heat.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HeatDetailVO {

    private Long customerId;
    private String customerName;
    private Integer score;
    private String level;
    private LocalDate scoreDate;
    private BigDecimal replySpeedScore;
    private BigDecimal askDepthScore;
    private BigDecimal bargainScore;
    private BigDecimal visitScore;
    private BigDecimal sentimentScore;
    private String reason;
    private String suggestion;
}

