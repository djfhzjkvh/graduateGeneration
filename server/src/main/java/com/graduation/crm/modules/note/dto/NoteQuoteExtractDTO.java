package com.graduation.crm.modules.note.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NoteQuoteExtractDTO {
    private Integer maxCount;

    @NotNull(message = "创建人不能为空")
    private Long createdBy;
}
