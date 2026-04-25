package com.graduation.crm.modules.task.vo;

import lombok.Data;

@Data
public class TaskScheduleAdjustVO {
    private Integer affectedCount;
    private Integer adjustDays;
    private String startDate;
    private String endDate;
    private Long ownerId;
    private Long managerId;
    private String message;
}
