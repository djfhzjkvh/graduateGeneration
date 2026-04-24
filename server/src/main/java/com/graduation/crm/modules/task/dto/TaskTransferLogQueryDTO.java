package com.graduation.crm.modules.task.dto;

import lombok.Data;

/**
 * 任务转派日志查询参数。
 */
@Data
public class TaskTransferLogQueryDTO {

    private Long taskId;
    private Long fromUserId;
    private Long toUserId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
