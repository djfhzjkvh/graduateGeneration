package com.graduation.crm.modules.task.vo;

import lombok.Data;

import java.util.List;

/**
 * 任务详情返回对象，聚合基础任务信息、提醒记录和转派记录。
 */
@Data
public class TaskDetailVO {

    private TaskVO task;
    private List<TaskRemindLogVO> remindLogs;
    private List<TaskTransferLogVO> transferLogs;
}
