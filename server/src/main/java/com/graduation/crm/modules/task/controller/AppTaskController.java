package com.graduation.crm.modules.task.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.task.dto.TaskCreateDTO;
import com.graduation.crm.modules.task.dto.TaskQueryDTO;
import com.graduation.crm.modules.task.service.TaskService;
import com.graduation.crm.modules.task.vo.TaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 小程序端任务接口。
 *
 * 面向顾问的移动待办场景，支持查看任务、手动创建任务和完成任务。
 * 逾期刷新、自动提醒等后台任务后续由定时任务模块补充。
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
}
