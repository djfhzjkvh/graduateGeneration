package com.graduation.crm.modules.task.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TaskCreateDTO {

    private Long customerId;

    @NotBlank(message = "任务类型不能为空")
    private String taskType;

    @NotBlank(message = "任务标题不能为空")
    private String title;

    private String content;

    @NotBlank(message = "任务日期不能为空")
    private String taskDate;

    private String taskTime;
    private String priority;

    @NotNull(message = "负责人不能为空")
    private Long ownerId;
}

