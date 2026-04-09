package com.ryh.shortlink.allinone.common.interceptor;

import com.alibaba.fastjson2.JSON;
import com.ryh.shortlink.allinone.common.annotation.RequireRole;
import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.JwtUtils;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 角色权限拦截器
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requireRole = handlerMethod.getMethod().getAnnotation(RequireRole.class);

        // 如果没有 @RequireRole 注解，放行
        if (requireRole == null) {
            return true;
        }

        // 获取当前用户角色
        String role = null;

        // 优先从 Token 获取
        String token = request.getHeader("Token");
        if (token != null && JwtUtils.validateToken(token)) {
            role = JwtUtils.getRoleFromToken(token);
        } else {
            // 从 Session 获取
            HttpSession session = request.getSession(false);
            if (session != null) {
                var user = SessionUtils.getUser(session);
                if (user != null) {
                    role = user.getRole();
                }
            }
        }

        // 检查角色
        if (role == null) {
            sendError(response, "未登录或登录已过期");
            return false;
        }

        for (String allowedRole : requireRole.value()) {
            if (allowedRole.equals(role)) {
                return true;
            }
        }

        sendError(response, "无权限访问该功能");
        return false;
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(403);
        response.getWriter().write(JSON.toJSONString(Result.error(message)));
    }
}
