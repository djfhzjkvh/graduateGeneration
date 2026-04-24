package com.graduation.crm.modules.task.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务提醒日志返回对象。
 */
@Data
public class TaskRemindLogVO {

    private Long id;
    private Long taskId;
    private String remindType;
    private LocalDateTime remindTime;
    private String status;
    private String resultMsg;
    private LocalDateTime createdAt;
}
