package com.graduation.crm.modules.system.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.system.service.DeptService;
import com.graduation.crm.modules.system.vo.DeptVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端部门接口。
 *
 * 用于后台组织架构展示、用户归属选择和经理团队数据范围判断。
 */
@Tag(name = "管理端部门接口")
@RestController
@RequestMapping("/api/admin/depts")
@RequiredArgsConstructor
public class AdminDeptController {

    private final DeptService deptService;

    /**
     * 查询部门树，返回层级结构供管理端树形控件使用。
     */
    @GetMapping("/tree")
    @Operation(summary = "查询部门树")
    public Result<List<DeptVO>> tree() {
        return Result.success(deptService.tree());
    }
}

