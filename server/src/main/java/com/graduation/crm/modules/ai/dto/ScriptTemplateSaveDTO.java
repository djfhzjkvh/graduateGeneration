package com.graduation.crm.modules.ai.dto;

import lombok.Data;

@Data
public class ScriptTemplateSaveDTO {
    private String templateName;
    private String sceneType;
    private String channelType;
    private String customerTag;
    private String personalityType;
    private String content;
    private Integer isDefault;
    private Integer status;
    private Long createdBy;
}
