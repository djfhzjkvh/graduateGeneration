package com.graduation.crm.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务提醒日志，对应 crm_task_remind_log 表。
 */
@Data
@TableName("crm_task_remind_log")
public class TaskRemindLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String remindType;
    private LocalDateTime remindTime;
    private String status;
    private String resultMsg;
    private LocalDateTime createdAt;
}
