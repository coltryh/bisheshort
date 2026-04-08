package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dao.entity.PermissionDO;
import com.ryh.shortlink.allinone.service.PermissionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping("/list")
    public Result<List<PermissionDO>> list(HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限");
        }
        return Result.success(permissionService.listAllPermissions());
    }

    @GetMapping("/user/{userId}")
    public Result<List<PermissionDO>> getUserPermissions(@PathVariable Long userId, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限");
        }
        return Result.success(permissionService.getUserPermissions(userId));
    }

    @PostMapping("/assign")
    public Result<?> assignPermissions(@RequestBody AssignRequest request, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限");
        }
        boolean success = permissionService.assignPermissions(request.getUserId(), request.getPermissionIds());
        return success ? Result.success() : Result.error("分配失败");
    }

    @GetMapping("/check/{permissionCode}")
    public Result<Boolean> checkPermission(@PathVariable String permissionCode, HttpSession session) {
        Long userId = SessionUtils.getUserId(session);
        if (userId == null) {
            return Result.error("未登录");
        }
        boolean has = permissionService.hasPermission(userId, permissionCode);
        return Result.success(has);
    }

    public static class AssignRequest {
        private Long userId;
        private List<Long> permissionIds;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public List<Long> getPermissionIds() {
            return permissionIds;
        }

        public void setPermissionIds(List<Long> permissionIds) {
            this.permissionIds = permissionIds;
        }
    }
}
