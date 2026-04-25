package com.graduation.crm.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_script_send_log")
public class AiScriptSendLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private Long scriptLogId;
    private String channelType;
    private String title;
    private String content;
    private String receiverName;
    private String receiverMobile;
    private String sendStatus;
    private String sendResult;
    private Long sentBy;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
