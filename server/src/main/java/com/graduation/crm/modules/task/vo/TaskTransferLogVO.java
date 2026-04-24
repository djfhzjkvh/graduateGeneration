package com.graduation.crm.modules.task.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务转派日志返回对象。
 */
@Data
public class TaskTransferLogVO {

    private Long id;
    private Long taskId;
    private Long fromUserId;
    private Long toUserId;
    private String reason;
    private Long createdBy;
    private LocalDateTime createdAt;
}
