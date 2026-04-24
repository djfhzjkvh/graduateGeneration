package com.graduation.crm.modules.note.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteVO {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long followId;
    private String noteType;
    private String title;
    private String summary;
    private String objectionTop3;
    private String nextTopic;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

