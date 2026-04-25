package com.graduation.crm.modules.ai.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScriptSendVO {
    private Long id;
    private Long customerId;
    private String channelType;
    private String sendStatus;
    private String sendResult;
    private LocalDateTime sentAt;
}
