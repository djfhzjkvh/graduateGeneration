package com.graduation.crm.modules.heat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("crm_customer_heat_signal")
public class CustomerHeatSignal {
    @TableId(type = IdType.AUTO)
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
