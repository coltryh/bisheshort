package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dao.entity.UserDO;
import com.ryh.shortlink.allinone.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public Result<List<UserDO>> list(HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限");
        }
        return Result.success(userService.listAllUsers());
    }

    @GetMapping("/{id}")
    public Result<UserDO> getById(@PathVariable Long id, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限");
        }
        UserDO user = userService.getUserByUsername(null);
        if (user != null && user.getId().equals(id)) {
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody UserDO user, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限");
        }
        boolean success = userService.updateUser(user);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限");
        }
        boolean success = userService.deleteUser(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
