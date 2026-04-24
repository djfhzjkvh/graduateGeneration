package com.graduation.crm.modules.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiCallLogVO {

    private Long id;
    private String bizType;
    private Long bizId;
    private String modelName;
    private Integer tokenUsage;
    private String status;
    private String errorMsg;
    private LocalDateTime createdAt;
}

