package com.graduation.crm.modules.system.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.system.service.RoleService;
import com.graduation.crm.modules.system.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端角色接口。
 *
 * 当前用于用户管理表单的角色下拉选择，细粒度权限绑定后续扩展。
 */
@Tag(name = "管理端角色接口")
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;

    /**
     * 查询启用角色列表。
     */
    @GetMapping
    @Operation(summary = "查询角色列表")
    public Result<List<RoleVO>> list() {
        return Result.success(roleService.listEnabled());
    }
}

