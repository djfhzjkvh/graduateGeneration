package com.graduation.crm.modules.task.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 任务延期请求参数。
 */
@Data
public class TaskDelayDTO {

    @NotBlank(message = "新的任务日期不能为空")
    private String newTaskDate;

    private String newTaskTime;

    private String reason;
}
