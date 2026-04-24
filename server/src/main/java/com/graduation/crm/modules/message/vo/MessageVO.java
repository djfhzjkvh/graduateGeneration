package com.graduation.crm.modules.message.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {

    private Long id;
    private Long userId;
    private String msgType;
    private String title;
    private String content;
    private String bizType;
    private Long bizId;
    private Integer readStatus;
    private LocalDateTime createdAt;
}

