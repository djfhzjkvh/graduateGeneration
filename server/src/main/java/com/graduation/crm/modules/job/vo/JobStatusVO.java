package com.graduation.crm.modules.job.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobStatusVO {
    private String jobName;
    private Integer enabled;
    private String scheduleDesc;
    private LocalDateTime lastRunAt;
    private Integer lastAffectedCount;
    private String lastMessage;
    private String lastStatus;
}
