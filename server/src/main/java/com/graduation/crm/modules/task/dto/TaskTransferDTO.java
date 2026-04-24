package com.graduation.crm.modules.task.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 任务转派请求参数。
 */
@Data
public class TaskTransferDTO {

    @NotNull(message = "接收人不能为空")
    private Long toUserId;

    private String reason;

    private Long createdBy;
}
