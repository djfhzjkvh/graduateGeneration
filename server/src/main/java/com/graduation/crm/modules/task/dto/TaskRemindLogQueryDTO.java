package com.graduation.crm.modules.task.dto;

import lombok.Data;

/**
 * 任务提醒日志查询参数。
 */
@Data
public class TaskRemindLogQueryDTO {

    private Long taskId;
    private String remindType;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
