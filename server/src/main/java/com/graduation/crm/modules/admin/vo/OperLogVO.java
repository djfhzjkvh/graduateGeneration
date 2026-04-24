package com.graduation.crm.modules.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperLogVO {

    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String bizType;
    private Long bizId;
    private String action;
    private String content;
    private String ip;
    private LocalDateTime createdAt;
}

