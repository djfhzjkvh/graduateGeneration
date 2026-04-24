package com.graduation.crm.modules.follow.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowRecordVO {

    private Long id;
    private Long customerId;
    private String followType;
    private String followResult;
    private String content;
    private String summary;
    private LocalDateTime nextFollowTime;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
}

