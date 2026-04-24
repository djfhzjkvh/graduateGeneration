package com.graduation.crm.modules.job.schedule;

import com.graduation.crm.modules.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 系统定时任务调度器。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemJobScheduler {

    private final JobService jobService;

    /**
     * 每小时刷新一次逾期任务，避免用户当天打开小程序时看到过期状态滞后。
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void refreshOverdueTasks() {
        log.info("开始执行定时任务：自动刷新逾期任务");
        jobService.refreshOverdueTasks();
    }

    /**
     * 每天 08:05 生成当天应跟进客户的提醒任务。
     */
    @Scheduled(cron = "0 5 8 * * ?")
    public void generateFollowTasks() {
        log.info("开始执行定时任务：自动生成跟进提醒任务");
        jobService.generateFollowTasks();
    }

    /**
     * 每 30 分钟扫描一次未来 2 小时内即将到期的任务。
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void remindDueSoonTasks() {
        log.info("开始执行定时任务：即将到期任务提醒");
        jobService.remindDueSoonTasks();
    }

    /**
     * 每天 23:55 生成当日统计报表，供管理端报表页查询。
     */
    @Scheduled(cron = "0 55 23 * * ?")
    public void generateDailyReport() {
        log.info("开始执行定时任务：每日统计报表生成");
        jobService.generateDailyReport();
    }
}
