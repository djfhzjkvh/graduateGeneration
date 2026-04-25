package com.graduation.crm.modules.ai.dto;

import lombok.Data;

@Data
public class ScriptGenerateV2DTO {
    private Long customerId;
    private String sceneType;
    private String lastChatContent;
    private String projectSellingPoints;
    private String personalityType;
    private String festivalName;
    private String weatherText;
    private String customPrompt;
    private Long createdBy;
}
