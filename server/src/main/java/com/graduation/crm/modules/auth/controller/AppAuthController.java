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
 * 小程序端认证接口。
 *
 * 面向顾问和经理登录移动端，返回的 token 供后续接口放入 Authorization 请求头。
 */
@Tag(name = "移动端认证接口")
@RestController
@RequestMapping("/api/app/auth")
@RequiredArgsConstructor
public class AppAuthController {

    private final AuthService authService;

    /**
     * 小程序端账号密码登录，现阶段复用后台用户表。
     */
    @PostMapping("/login")
    @Operation(summary = "移动端登录")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    /**
     * 根据请求头 token 获取当前登录用户，用于前端刷新页面后恢复身份。
     */
    @GetMapping("/profile")
    @Operation(summary = "获取当前用户信息")
    public Result<UserVO> profile(@RequestHeader("Authorization") String authorization) {
        return Result.success(authService.profile(authorization));
    }

    /**
     * 退出登录会移除服务端保存的临时 token。
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization) {
        authService.logout(authorization);
        return Result.success();
    }
}

