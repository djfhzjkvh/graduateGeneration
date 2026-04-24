package com.graduation.crm.modules.project.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectVO {

    private Long id;
    private String projectName;
    private String city;
    private String region;
    private String address;
    private BigDecimal avgPrice;
    private String highlights;
    private String discountInfo;
    private LocalDate handoverDate;
    private Integer status;
}

