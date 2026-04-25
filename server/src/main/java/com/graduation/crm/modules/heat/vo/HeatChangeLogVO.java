package com.graduation.crm.modules.heat.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HeatChangeLogVO {
    private Long id;
    private Long customerId;
    private String customerName;
    private Integer oldScore;
    private Integer newScore;
    private Integer changeValue;
    private String changeType;
    private String reason;
    private LocalDateTime createdAt;
}
