package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dto.req.UserUpdateReqDTO;
import com.ryh.shortlink.allinone.dto.resp.UserRespDTO;
import com.ryh.shortlink.allinone.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 查询所有用户列表
     */
    @GetMapping("/list")
    public Result<List<UserRespDTO>> list(HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限访问");
        }
        List<UserRespDTO> users = userService.listAllUsers();
        return Result.success(users);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public Result<UserRespDTO> getById(@PathVariable Long id, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限访问");
        }
        try {
            // 这里通过遍历查找，因为现有接口是基于username
            List<UserRespDTO> users = userService.listAllUsers();
            UserRespDTO user = users.stream()
                    .filter(u -> u.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户失败");
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody UserUpdateReqDTO requestParam, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限访问");
        }
        try {
            userService.updateUser(requestParam);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限访问");
        }
        try {
            userService.deleteUser(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
