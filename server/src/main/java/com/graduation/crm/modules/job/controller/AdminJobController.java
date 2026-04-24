package com.graduation.crm.modules.job.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.job.service.JobService;
import com.graduation.crm.modules.job.vo.JobRunResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端系统任务接口。
 *
 * 这些接口用于手动触发后台自动化任务，方便毕业设计演示和管理员排查数据生成情况。
 */
@Tag(name = "管理端系统任务接口")
@RestController
@RequestMapping("/api/admin/jobs")
@RequiredArgsConstructor
public class AdminJobController {

    private final JobService jobService;

    /**
     * 手动刷新逾期任务，将过期未完成任务统一标记为 OVERDUE。
     */
    @PostMapping("/refresh-overdue-tasks")
    @Operation(summary = "自动刷新逾期任务")
    public Result<JobRunResultVO> refreshOverdueTasks() {
        return Result.success(jobService.refreshOverdueTasks());
    }

    /**
     * 手动生成客户跟进提醒任务，便于前端立即验证提醒列表。
     */
    @PostMapping("/generate-follow-tasks")
    @Operation(summary = "自动生成跟进提醒任务")
    public Result<JobRunResultVO> generateFollowTasks() {
        return Result.success(jobService.generateFollowTasks());
    }

    /**
     * 手动扫描即将到期任务，并生成站内消息提醒。
     */
    @PostMapping("/remind-due-soon-tasks")
    @Operation(summary = "即将到期任务提醒")
    public Result<JobRunResultVO> remindDueSoonTasks() {
        return Result.success(jobService.remindDueSoonTasks());
    }

    /**
     * 手动生成每日统计报表，生成后可通过 /api/admin/reports/daily 查询。
     */
    @PostMapping("/generate-daily-report")
    @Operation(summary = "每日统计报表生成")
    public Result<JobRunResultVO> generateDailyReport() {
        return Result.success(jobService.generateDailyReport());
    }
}
