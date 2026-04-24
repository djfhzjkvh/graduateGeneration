package com.graduation.crm.modules.task.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.task.dto.TaskQueryDTO;
import com.graduation.crm.modules.task.service.TaskService;
import com.graduation.crm.modules.task.vo.TaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端任务接口。
 *
 * 面向经理和管理员，用于后台查看团队任务、逾期任务和后续任务转派。
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
}
