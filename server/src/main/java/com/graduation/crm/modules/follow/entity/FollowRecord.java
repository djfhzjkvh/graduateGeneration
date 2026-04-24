package com.graduation.crm.modules.follow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("crm_follow_record")
public class FollowRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String followType;
    private String followResult;
    private String content;
    private String summary;
    private LocalDateTime nextFollowTime;
    private Long userId;
    private Integer callDuration;
    private BigDecimal sentimentScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

