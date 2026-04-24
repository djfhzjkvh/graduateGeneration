package com.graduation.crm.modules.task.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.task.dto.TaskCreateDTO;
import com.graduation.crm.modules.task.dto.TaskDelayDTO;
import com.graduation.crm.modules.task.dto.TaskQueryDTO;
import com.graduation.crm.modules.task.dto.TaskRemindLogQueryDTO;
import com.graduation.crm.modules.task.service.TaskService;
import com.graduation.crm.modules.task.vo.TaskRemindLogVO;
import com.graduation.crm.modules.task.vo.TaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 小程序端任务接口。
 *
 * 面向顾问的移动待办场景，支持查看任务、创建任务、完成任务、延期任务和查看提醒记录。
 */
@Tag(name = "移动端任务接口")
@RestController
@RequestMapping("/api/app/tasks")
@RequiredArgsConstructor
public class AppTaskController {

    private final TaskService taskService;

    /**
     * 查询任务列表，支持按负责人、客户、状态和日期过滤。
     */
    @GetMapping
    @Operation(summary = "分页查询任务")
    public Result<PageResult<TaskVO>> page(TaskQueryDTO queryDTO) {
        return Result.success(taskService.page(queryDTO));
    }

    /**
     * 手动创建跟进任务，自动任务后续由跟进规则或定时任务生成。
     */
    @PostMapping
    @Operation(summary = "新增任务")
    public Result<Void> create(@RequestBody @Valid TaskCreateDTO dto) {
        taskService.create(dto);
        return Result.success();
    }

    /**
     * 将任务标记为已完成，并记录完成时间，便于后续统计任务完成率。
     */
    @PutMapping("/{id}/complete")
    @Operation(summary = "完成任务")
    public Result<Void> complete(@PathVariable Long id) {
        taskService.complete(id);
        return Result.success();
    }

    /**
     * 延期任务，更新新的处理日期/时间并累计延期次数，便于顾问临时调整跟进计划。
     */
    @PutMapping("/{id}/delay")
    @Operation(summary = "延期任务")
    public Result<Void> delay(@PathVariable Long id, @RequestBody @Valid TaskDelayDTO dto) {
        taskService.delay(id, dto);
        return Result.success();
    }

    /**
     * 查询单个任务的提醒日志，顾问可确认系统是否已经提醒过该任务。
     */
    @GetMapping("/{taskId}/remind-logs")
    @Operation(summary = "任务提醒日志")
    public Result<PageResult<TaskRemindLogVO>> remindLogs(@PathVariable Long taskId, TaskRemindLogQueryDTO queryDTO) {
        return Result.success(taskService.remindLogsByTask(taskId, queryDTO));
    }
}
