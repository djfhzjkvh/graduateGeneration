package com.graduation.crm.modules.task.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class TaskVO {

    private Long id;
    private Long customerId;
    private String customerName;
    private String taskType;
    private String title;
    private String content;
    private LocalDate taskDate;
    private LocalTime taskTime;
    private String status;
    private String priority;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime completeTime;
}

