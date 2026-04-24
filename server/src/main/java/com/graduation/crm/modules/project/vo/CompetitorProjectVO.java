package com.graduation.crm.modules.project.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CompetitorProjectVO {

    private Long id;
    private String projectName;
    private String region;
    private BigDecimal avgPrice;
    private String discountInfo;
    private String houseTypes;
    private LocalDate handoverDate;
    private String highlights;
    private String weakness;
    private Integer status;
}

