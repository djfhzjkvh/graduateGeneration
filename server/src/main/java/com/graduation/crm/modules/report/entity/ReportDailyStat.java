package com.graduation.crm.modules.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日统计报表落库实体，对应 report_daily_stat 表。
 */
@Data
@TableName("report_daily_stat")
public class ReportDailyStat {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate statDate;
    private Long userId;
    private Long deptId;
    private Integer newCustomerCount;
    private Integer highIntentCount;
    private Integer pendingTaskCount;
    private BigDecimal expectedAmount;
    private Integer forgetTaskCount;
    private LocalDateTime createdAt;
}
