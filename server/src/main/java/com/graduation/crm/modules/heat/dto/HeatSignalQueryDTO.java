package com.graduation.crm.modules.heat.dto;

import lombok.Data;

@Data
public class HeatSignalQueryDTO {
    private Long customerId;
    private String signalType;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
