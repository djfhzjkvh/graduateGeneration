package com.graduation.crm.modules.task.dto;

import lombok.Data;

@Data
public class TaskQueryDTO {

    private Long customerId;
    private Long ownerId;
    private String status;
    private String date;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

