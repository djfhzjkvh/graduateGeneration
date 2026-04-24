package com.graduation.crm.modules.job.service;

import com.graduation.crm.modules.job.vo.JobRunResultVO;

/**
 * 系统自动化任务服务。
 */
public interface JobService {

    JobRunResultVO refreshOverdueTasks();

    JobRunResultVO generateFollowTasks();

    JobRunResultVO remindDueSoonTasks();

    JobRunResultVO generateDailyReport();
}
