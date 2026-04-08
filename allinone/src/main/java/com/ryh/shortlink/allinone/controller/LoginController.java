package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.JwtUtils;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dao.entity.UserDO;
import com.ryh.shortlink.allinone.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> params, HttpSession session) {
        String username = params.get("username");
        String password = params.get("password");

        UserDO user = userService.login(username, password);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        SessionUtils.setUser(session, user);

        String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<?> logout(HttpSession session) {
        SessionUtils.removeUser(session);
        return Result.success();
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String realName = params.get("realName");

        if (username == null || password == null || realName == null) {
            return Result.error("参数不完整");
        }

        boolean success = userService.register(username, password, realName);
        if (!success) {
            return Result.error("用户名已存在");
        }

        return Result.success();
    }

    @GetMapping("/currentUser")
    public Result<?> currentUser(HttpSession session) {
        UserDO user = SessionUtils.getUser(session);
        if (user == null) {
            return Result.error("未登录");
        }
        return Result.success(user);
    }
}
