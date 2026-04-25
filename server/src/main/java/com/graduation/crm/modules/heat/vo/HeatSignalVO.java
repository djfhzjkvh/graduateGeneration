package com.graduation.crm.modules.heat.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HeatSignalVO {
    private Long id;
    private Long customerId;
    private Long followId;
    private String signalType;
    private BigDecimal scoreDelta;
    private String reason;
    private String rawText;
    private LocalDateTime signalTime;
    private LocalDateTime createdAt;
}
