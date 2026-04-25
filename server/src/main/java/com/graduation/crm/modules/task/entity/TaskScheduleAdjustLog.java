package com.graduation.crm.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("task_schedule_adjust_log")
public class TaskScheduleAdjustLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private LocalDate oldTaskDate;
    private LocalTime oldTaskTime;
    private LocalDate newTaskDate;
    private LocalTime newTaskTime;
    private Integer adjustDays;
    private String reason;
    private Long createdBy;
    private LocalDateTime createdAt;
}
