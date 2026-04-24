package com.graduation.crm.modules.task.vo;

import lombok.Data;

/**
 * 管理端任务统计返回对象。
 */
@Data
public class TaskStatVO {

    private Long pendingCount;
    private Long overdueCount;
    private Long doneCount;
    private Long delayedCount;
}
