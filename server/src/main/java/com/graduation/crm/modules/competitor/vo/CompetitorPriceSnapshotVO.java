package com.graduation.crm.modules.competitor.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CompetitorPriceSnapshotVO {
    private Long id;
    private Long competitorId;
    private String competitorName;
    private BigDecimal avgPrice;
    private String discountInfo;
    private String houseTypes;
    private LocalDate snapshotDate;
    private String dataSource;
    private Long createdBy;
    private LocalDateTime createdAt;
}
