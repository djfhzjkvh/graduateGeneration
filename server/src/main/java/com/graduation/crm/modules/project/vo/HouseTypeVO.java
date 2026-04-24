package com.graduation.crm.modules.project.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HouseTypeVO {

    private Long id;
    private Long projectId;
    private String projectName;
    private String typeName;
    private BigDecimal area;
    private String rooms;
    private BigDecimal totalPriceMin;
    private BigDecimal totalPriceMax;
    private String sellingPoints;
    private Integer status;
}

