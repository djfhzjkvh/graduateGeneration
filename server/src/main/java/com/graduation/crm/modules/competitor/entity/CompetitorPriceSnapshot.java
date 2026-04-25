package com.graduation.crm.modules.competitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("crm_competitor_price_snapshot")
public class CompetitorPriceSnapshot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long competitorId;
    private BigDecimal avgPrice;
    private String discountInfo;
    private String houseTypes;
    private LocalDate snapshotDate;
    private String dataSource;
    private Long createdBy;
    private LocalDateTime createdAt;
}
