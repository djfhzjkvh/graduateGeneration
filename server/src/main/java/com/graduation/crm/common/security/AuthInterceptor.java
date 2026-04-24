package com.graduation.crm.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graduation.crm.common.config.SecurityProperties;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.auth.service.TokenService;
import com.graduation.crm.modules.system.service.UserService;
import com.graduation.crm.modules.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 轻量 Token 权限拦截器。
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final Set<String> ADMIN_ROLES = new HashSet<>(Arrays.asList("ADMIN", "MANAGER"));
    private static final Set<String> APP_ROLES = new HashSet<>(Arrays.asList("ADMIN", "MANAGER", "ADVISOR"));

    private final SecurityProperties securityProperties;
    private final TokenService tokenService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!Boolean.TRUE.equals(securityProperties.getEnabled()) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        if (!uri.startsWith("/api/")) {
            return true;
        }

        try {
            String token = tokenService.extractToken(request.getHeader("Authorization"));
            Long userId = tokenService.parseUserId(token);
            UserVO user = userService.findUserVOById(userId);
            if (user == null) {
                writeError(response, 401, "登录状态已失效");
                return false;
            }
            if (uri.startsWith("/api/admin/") && !ADMIN_ROLES.contains(user.getRoleCode())) {
                writeError(response, 403, "无管理端访问权限");
                return false;
            }
            if (uri.startsWith("/api/app/") && !APP_ROLES.contains(user.getRoleCode())) {
                writeError(response, 403, "无移动端访问权限");
                return false;
            }
            AuthContext.setCurrentUser(user);
            return true;
        } catch (Exception e) {
            writeError(response, 401, e.getMessage());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private void writeError(HttpServletResponse response, Integer code, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(code, message)));
    }
}
