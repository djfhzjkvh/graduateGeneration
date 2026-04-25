package com.graduation.crm.modules.job.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobRunLogVO {
    private Long id;
    private String jobName;
    private String triggerType;
    private String runStatus;
    private Integer affectedCount;
    private String message;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Long durationMs;
    private String errorMessage;
    private LocalDateTime createdAt;
}
