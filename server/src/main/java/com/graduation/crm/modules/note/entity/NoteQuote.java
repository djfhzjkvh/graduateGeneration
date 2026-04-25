package com.graduation.crm.modules.note.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_note_quote")
public class NoteQuote {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long noteId;
    private Long customerId;
    private String quoteText;
    private String sourceType;
    private Integer likeCount;
    private Long createdBy;
    private LocalDateTime createdAt;
}
