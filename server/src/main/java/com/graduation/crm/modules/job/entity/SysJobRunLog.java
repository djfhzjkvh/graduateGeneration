package com.graduation.crm.modules.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_job_run_log")
public class SysJobRunLog {
    @TableId(type = IdType.AUTO)
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
