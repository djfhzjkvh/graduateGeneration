package com.graduation.crm.modules.heat.dto;

import lombok.Data;

@Data
public class HeatChangeLogQueryDTO {
    private Long customerId;
    private String changeType;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
