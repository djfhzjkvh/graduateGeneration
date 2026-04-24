package com.graduation.crm.modules.auth.service;

import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.modules.auth.dto.LoginDTO;
import com.graduation.crm.modules.auth.vo.LoginVO;
import com.graduation.crm.modules.system.entity.SysUser;
import com.graduation.crm.modules.system.service.UserService;
import com.graduation.crm.modules.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final TokenService tokenService;

    /**
     * 登录先使用演示数据中的明文密码校验，后续接入 BCrypt 时只需替换此处判断。
     */
    public LoginVO login(LoginDTO dto) {
        SysUser user = userService.findEnabledByUsername(dto.getUsername());
        if (user == null || !user.getPassword().equals(dto.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        UserVO userInfo = userService.findUserVOById(user.getId());
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(tokenService.createToken(user.getId()));
        loginVO.setUserInfo(userInfo);
        return loginVO;
    }

    public UserVO profile(String authorization) {
        String token = tokenService.extractToken(authorization);
        Long userId = tokenService.parseUserId(token);
        return userService.findUserVOById(userId);
    }

    public void logout(String authorization) {
        String token = tokenService.extractToken(authorization);
        tokenService.removeToken(token);
    }
}

