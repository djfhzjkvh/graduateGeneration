package com.graduation.crm.modules.heat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("crm_customer_heat_log")
public class CustomerHeatLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private Integer score;
    private LocalDate scoreDate;
    private BigDecimal replySpeedScore;
    private BigDecimal askDepthScore;
    private BigDecimal bargainScore;
    private BigDecimal visitScore;
    private BigDecimal sentimentScore;
    private String totalReason;
    private LocalDateTime createdAt;
}

