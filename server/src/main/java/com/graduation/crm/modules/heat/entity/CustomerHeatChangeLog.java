package com.graduation.crm.modules.heat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_customer_heat_change_log")
public class CustomerHeatChangeLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private Integer oldScore;
    private Integer newScore;
    private String changeType;
    private String reason;
    private LocalDateTime createdAt;
}
