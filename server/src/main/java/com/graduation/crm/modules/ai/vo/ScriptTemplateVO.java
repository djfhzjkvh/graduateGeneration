package com.graduation.crm.modules.ai.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScriptTemplateVO {
    private Long id;
    private String templateName;
    private String sceneType;
    private String channelType;
    private String customerTag;
    private String personalityType;
    private String content;
    private Integer isDefault;
    private Integer status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
