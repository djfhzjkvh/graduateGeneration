package com.graduation.crm.modules.note.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoteQuoteVO {
    private Long id;
    private Long noteId;
    private Long customerId;
    private String customerName;
    private String quoteText;
    private String sourceType;
    private Integer likeCount;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
}
