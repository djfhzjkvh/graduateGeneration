package com.graduation.crm.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务转派日志，对应 crm_task_transfer_log 表。
 */
@Data
@TableName("crm_task_transfer_log")
public class TaskTransferLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private Long fromUserId;
    private Long toUserId;
    private String reason;
    private Long createdBy;
    private LocalDateTime createdAt;
}
