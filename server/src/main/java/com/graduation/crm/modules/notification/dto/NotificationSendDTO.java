package com.graduation.crm.modules.notification.dto;

import lombok.Data;

@Data
public class NotificationSendDTO {
    private String channelCode;
    private Long receiverUserId;
    private String receiverName;
    private String receiverAddress;
    private String msgType;
    private String title;
    private String content;
    private String bizType;
    private Long bizId;
}
