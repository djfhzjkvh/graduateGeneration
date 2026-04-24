package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SimpleTaskVO {

    private Long id;
    private Long customerId;
    private String customerName;
    private String title;
    private String status;
    private String priority;
    private LocalDate taskDate;
    private LocalTime taskTime;
}

