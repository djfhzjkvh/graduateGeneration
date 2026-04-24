package com.graduation.crm.modules.auth.service;

import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.modules.system.vo.UserVO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    /**
     * 轻量 Token 存储用于先打通前后端登录流程；正式环境可替换为 JWT 或 Redis。
     */
    public String createToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, userId);
        return token;
    }

    public Long parseUserId(String token) {
        Long userId = tokenStore.get(token);
        if (userId == null) {
            throw new BusinessException("登录状态已失效");
        }
        return userId;
    }

    public void removeToken(String token) {
        if (token != null) {
            tokenStore.remove(token);
        }
    }

    public String extractToken(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            throw new BusinessException("请先登录");
        }
        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}

