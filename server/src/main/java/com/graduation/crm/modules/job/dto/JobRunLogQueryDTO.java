package com.graduation.crm.modules.job.dto;

import lombok.Data;

@Data
public class JobRunLogQueryDTO {
    private String jobName;
    private String triggerType;
    private String runStatus;
    private String dateStart;
    private String dateEnd;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
