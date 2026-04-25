package com.graduation.crm.modules.task.dto;

import lombok.Data;

@Data
public class TaskTeamPoolQueryDTO {
    private Long managerId;
    private Long ownerId;
    private String status;
    private String customerName;
    private Boolean overdueOnly;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
