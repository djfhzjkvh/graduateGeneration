package com.graduation.crm.modules.note.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_note")
public class Note {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private Long followId;
    private String noteType;
    private String title;
    private String summary;
    private String objectionTop3;
    private String nextTopic;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

