package com.graduation.crm.modules.task.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.task.dto.TaskQueryDTO;
import com.graduation.crm.modules.task.dto.TaskRemindLogQueryDTO;
import com.graduation.crm.modules.task.dto.TaskTransferDTO;
import com.graduation.crm.modules.task.service.TaskService;
import com.graduation.crm.modules.task.vo.TaskRemindLogVO;
import com.graduation.crm.modules.task.vo.TaskStatVO;
import com.graduation.crm.modules.task.vo.TaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 管理端任务接口。
 *
 * 面向经理和管理员，用于后台查看团队任务、统计任务状态、追踪提醒记录和执行任务转派。
 */
@Tag(name = "管理端任务接口")
@RestController
@RequestMapping("/api/admin/tasks")
@RequiredArgsConstructor
public class AdminTaskController {

    private final TaskService taskService;

    /**
     * 管理端任务分页查询，主要用于团队任务总览和逾期任务排查。
     */
    @GetMapping
    @Operation(summary = "分页查询任务")
    public Result<PageResult<TaskVO>> page(TaskQueryDTO queryDTO) {
        return Result.success(taskService.page(queryDTO));
    }

    /**
     * 查询任务统计数量，供管理端任务看板展示。
     */
    @GetMapping("/stat")
    @Operation(summary = "任务统计")
    public Result<TaskStatVO> stat() {
        return Result.success(taskService.stat());
    }

    /**
     * 查询任务提醒日志，便于管理员确认提醒是否成功触达。
     */
    @GetMapping("/remind-logs")
    @Operation(summary = "任务提醒日志")
    public Result<PageResult<TaskRemindLogVO>> remindLogs(TaskRemindLogQueryDTO queryDTO) {
        return Result.success(taskService.remindLogPage(queryDTO));
    }

    /**
     * 任务转派，经理可将待处理任务调整给其他顾问。
     */
    @PutMapping("/{id}/transfer")
    @Operation(summary = "任务转派")
    public Result<Void> transfer(@PathVariable Long id, @RequestBody @Valid TaskTransferDTO dto) {
        taskService.transfer(id, dto);
        return Result.success();
    }
}
