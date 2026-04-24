package com.graduation.crm.modules.competitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_customer_competitor_focus")
public class CustomerCompetitorFocus {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private Long competitorId;
    private String focusContent;
    private Long createdBy;
    private LocalDateTime createdAt;
}

