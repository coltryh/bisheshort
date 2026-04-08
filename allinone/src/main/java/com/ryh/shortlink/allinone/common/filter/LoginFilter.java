package com.ryh.shortlink.allinone.common.filter;

import com.alibaba.fastjson2.JSON;
import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.JwtUtils;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dao.entity.UserDO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/api/*"})
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        if (uri.contains("/api/login") || uri.contains("/api/register")) {
            chain.doFilter(request, response);
            return;
        }

        // 支持 Token 和 Session 两种认证方式
        String token = request.getHeader("Token");
        HttpSession session = request.getSession(false);

        if (token != null && JwtUtils.validateToken(token)) {
            // Token 认证
            Long userId = JwtUtils.getUserIdFromToken(token);
            String username = JwtUtils.getUsernameFromToken(token);
            String role = JwtUtils.getRoleFromToken(token);

            UserDO user = new UserDO();
            user.setId(userId);
            user.setUsername(username);
            user.setRole(role);

            session = request.getSession(true);
            SessionUtils.setUser(session, user);
            chain.doFilter(request, response);
            return;
        }

        if (session != null && SessionUtils.getUser(session) != null) {
            // Session 认证
            chain.doFilter(request, response);
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(Result.error("未登录")));
    }
}
