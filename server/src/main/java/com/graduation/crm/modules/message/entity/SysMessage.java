package com.graduation.crm.modules.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_message")
public class SysMessage {

    @TableId(type = IdType.AUTO)
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

