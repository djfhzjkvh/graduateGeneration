package com.graduation.crm.modules.competitor.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompetitorSnapshotSaveDTO {
    private BigDecimal avgPrice;
    private String discountInfo;
    private String houseTypes;
    private String snapshotDate;
    private String dataSource;
    private Long createdBy;
}
