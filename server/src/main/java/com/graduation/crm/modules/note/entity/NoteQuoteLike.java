package com.graduation.crm.modules.note.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_note_quote_like")
public class NoteQuoteLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long quoteId;
    private Long userId;
    private LocalDateTime createdAt;
}
