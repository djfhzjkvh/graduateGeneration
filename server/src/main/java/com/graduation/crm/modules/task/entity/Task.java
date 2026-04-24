package com.graduation.crm.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("crm_task")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String taskType;
    private String title;
    private String content;
    private LocalDate taskDate;
    private LocalTime taskTime;
    private String status;
    private String priority;
    private Long ownerId;
    private String sourceType;
    private Long sourceId;
    private Integer delayCount;
    private LocalDateTime completeTime;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

