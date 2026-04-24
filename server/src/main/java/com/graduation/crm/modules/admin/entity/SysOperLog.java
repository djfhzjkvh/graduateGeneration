package com.graduation.crm.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_oper_log")
public class SysOperLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String bizType;
    private Long bizId;
    private String action;
    private String content;
    private String ip;
    private LocalDateTime createdAt;
}

