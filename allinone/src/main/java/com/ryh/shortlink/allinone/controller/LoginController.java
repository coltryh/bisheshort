package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dto.req.UserRegisterReqDTO;
import com.ryh.shortlink.allinone.dto.resp.UserLoginRespDTO;
import com.ryh.shortlink.allinone.dto.resp.UserRespDTO;
import com.ryh.shortlink.allinone.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 登录控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserLoginRespDTO> login(@RequestBody Map<String, String> params, HttpSession session) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            return Result.error("用户名或密码不能为空");
        }

        try {
            UserLoginRespDTO loginResult = userService.login(username, password);
            // 设置session
            SessionUtils.setUser(session, username, loginResult.getToken());
            return Result.success(loginResult);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody UserRegisterReqDTO requestParam, HttpSession session) {
        if (requestParam.getUsername() == null || requestParam.getPassword() == null) {
            return Result.error("用户名和密码不能为空");
        }

        try {
            userService.register(requestParam);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        try {
            String username = SessionUtils.getUsername(session);
            String token = SessionUtils.getToken(session);
            if (username != null && token != null) {
                userService.logout(username, token);
            }
            SessionUtils.removeUser(session);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/currentUser")
    public Result<UserRespDTO> getCurrentUser(HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }

        try {
            UserRespDTO user = userService.getUserByUsername(username);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
