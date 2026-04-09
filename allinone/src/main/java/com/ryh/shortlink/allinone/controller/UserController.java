package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.annotation.RequireRole;
import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.dto.req.UserUpdateReqDTO;
import com.ryh.shortlink.allinone.dto.resp.UserRespDTO;
import com.ryh.shortlink.allinone.service.UserService;
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
    @RequireRole("admin")
    public Result<List<UserRespDTO>> list() {
        List<UserRespDTO> users = userService.listAllUsers();
        return Result.success(users);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    @RequireRole("admin")
    public Result<UserRespDTO> getById(@PathVariable Long id) {
        try {
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
    @RequireRole("admin")
    public Result<?> update(@RequestBody UserUpdateReqDTO requestParam) {
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
    @RequireRole("admin")
    public Result<?> delete(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
