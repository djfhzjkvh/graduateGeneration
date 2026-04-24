package com.graduation.crm.modules.note.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NoteCreateDTO {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;
    private Long followId;
    private String noteType;
    private String title;
    private String summary;
    private String objectionTop3;
    private String nextTopic;
    private Long createdBy;
}

