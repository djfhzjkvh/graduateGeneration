package com.graduation.crm.common.security;

import com.graduation.crm.modules.system.vo.UserVO;

/**
 * 当前请求登录用户上下文。
 */
public class AuthContext {

    private static final ThreadLocal<UserVO> CURRENT_USER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void setCurrentUser(UserVO user) {
        CURRENT_USER.set(user);
    }

    public static UserVO getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
