package com.graduation.crm.modules.auth.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.auth.dto.LoginDTO;
import com.graduation.crm.modules.auth.service.AuthService;
import com.graduation.crm.modules.auth.vo.LoginVO;
import com.graduation.crm.modules.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理端认证接口。
 *
 * 面向管理员和经理登录 Web 管理端，当前与小程序端共用认证服务。
 */
@Tag(name = "管理端认证接口")
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthService authService;

    /**
     * 管理端账号密码登录，返回 token 和用户角色信息。
     */
    @PostMapping("/login")
    @Operation(summary = "管理端登录")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    /**
     * 管理端获取当前用户信息，用于刷新页面和权限菜单初始化。
     */
    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息")
    public Result<UserVO> profile(@RequestHeader("Authorization") String authorization) {
        return Result.success(authService.profile(authorization));
    }

    /**
     * 管理端退出登录，清理当前 token。
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization);
        return Result.success();
    }
}

