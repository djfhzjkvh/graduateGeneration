package com.graduation.crm.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_call_log")
public class AiCallLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String bizType;
    private Long bizId;
    private String modelName;
    private String requestJson;
    private String responseJson;
    private Integer tokenUsage;
    private String status;
    private String errorMsg;
    private LocalDateTime createdAt;
}

