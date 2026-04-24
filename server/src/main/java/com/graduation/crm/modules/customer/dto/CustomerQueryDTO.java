package com.graduation.crm.modules.customer.dto;

import lombok.Data;

@Data
public class CustomerQueryDTO {

    private String keyword;
    private String status;
    private Long advisorId;
    private Long managerId;
    private Long deptId;
    private Integer minHeatScore;
    private Integer maxHeatScore;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

