package com.graduation.crm.modules.job.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定时任务执行结果，统一返回给管理端，方便前端展示本次处理数量。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRunResultVO {

    private String jobName;
    private Integer affectedCount;
    private String message;
}
