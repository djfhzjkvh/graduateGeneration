package com.graduation.crm.modules.task.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TaskBatchAdjustDTO {
    private Long ownerId;
    private Long managerId;
    private String startDate;
    private String endDate;
    private List<String> statuses;
    @NotNull(message = "调整天数不能为空")
    private Integer adjustDays;
    private String reason;
    private Long createdBy;
}
