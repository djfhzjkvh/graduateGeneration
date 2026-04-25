package com.graduation.crm.modules.ai.dto;

import lombok.Data;

@Data
public class ScriptTemplateQueryDTO {
    private String sceneType;
    private String channelType;
    private String customerTag;
    private String personalityType;
    private Long createdBy;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
