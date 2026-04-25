package com.graduation.crm.modules.prediction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("report_deal_prediction_snapshot")
public class DealPredictionSnapshot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate statDate;
    private Long customerId;
    private Long advisorId;
    private Long managerId;
    private Integer predictScore;
    private BigDecimal dealProbability;
    private BigDecimal expectedAmount;
    private String reasonSummary;
    private BigDecimal discountRate;
    private LocalDateTime createdAt;
}
