package com.graduation.crm.modules.ai.dto;

import lombok.Data;

@Data
public class ScriptSendDTO {
    private Long customerId;
    private Long scriptLogId;
    private String channelType;
    private String title;
    private String content;
    private Long sentBy;
}
