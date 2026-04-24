package com.graduation.crm.modules.system.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.system.dto.UserCreateDTO;
import com.graduation.crm.modules.system.dto.UserQueryDTO;
import com.graduation.crm.modules.system.dto.UserUpdateDTO;
import com.graduation.crm.modules.system.service.UserService;
import com.graduation.crm.modules.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 管理端用户接口。
 *
 * 用于管理员维护顾问、经理和管理员账号，是角色权限和客户归属的基础。
 */
@Tag(name = "管理端用户接口")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * 分页查询用户，支持按关键字、角色、部门和状态筛选。
     */
    @GetMapping
    @Operation(summary = "分页查询用户")
    public Result<PageResult<UserVO>> page(UserQueryDTO queryDTO) {
        return Result.success(userService.page(queryDTO));
    }

    /**
     * 查询单个用户详情，用于管理端编辑弹窗回显和用户资料页展示。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情")
    public Result<UserVO> detail(@PathVariable Long id) {
        return Result.success(userService.detail(id));
    }

    /**
     * 新增系统用户，当前密码按演示数据明文存储，正式环境需要替换为 BCrypt。
     */
    @PostMapping
    @Operation(summary = "新增用户")
    public Result<Void> create(@RequestBody @Valid UserCreateDTO dto) {
        userService.create(dto);
        return Result.success();
    }

    /**
     * 更新用户基础信息、角色、部门和启停状态。
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        userService.update(id, dto);
        return Result.success();
    }

    /**
     * 删除用户采用逻辑删除，避免破坏历史客户和任务的负责人引用。
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
