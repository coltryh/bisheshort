package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dao.entity.GroupDO;
import com.ryh.shortlink.allinone.service.GroupService;
import com.ryh.shortlink.allinone.service.PermissionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final PermissionService permissionService;

    @GetMapping("/list")
    public Result<List<GroupDO>> list(HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }

        List<GroupDO> groups;
        if (SessionUtils.isAdmin(session)) {
            groups = groupService.listAll();
        } else {
            groups = groupService.listByUsername(username);
        }
        return Result.success(groups);
    }

    @GetMapping("/{gid}")
    public Result<GroupDO> getByGid(@PathVariable String gid, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }

        GroupDO group = groupService.getByGid(gid);
        if (group == null) {
            return Result.error("分组不存在");
        }

        if (!SessionUtils.isAdmin(session) && !username.equals(group.getUsername())) {
            return Result.error("无权限");
        }

        return Result.success(group);
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody GroupDO group, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }

        if (!SessionUtils.isAdmin(session)) {
            boolean hasPermission = permissionService.hasPermission(SessionUtils.getUserId(session), "LINK_CREATE");
            if (!hasPermission) {
                return Result.error("无权限创建分组");
            }
        }

        group.setGid(UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        group.setUsername(username);
        boolean success = groupService.save(group);
        return success ? Result.success() : Result.error("创建失败");
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody GroupDO group, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }

        GroupDO existing = groupService.getByGid(group.getGid());
        if (existing == null) {
            return Result.error("分组不存在");
        }

        if (!SessionUtils.isAdmin(session) && !username.equals(existing.getUsername())) {
            return Result.error("无权限");
        }

        boolean success = groupService.update(group);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/delete/{gid}")
    public Result<?> delete(@PathVariable String gid, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }

        GroupDO existing = groupService.getByGid(gid);
        if (existing == null) {
            return Result.error("分组不存在");
        }

        if (!SessionUtils.isAdmin(session)) {
            boolean hasPermission = permissionService.hasPermission(SessionUtils.getUserId(session), "LINK_DELETE");
            if (!hasPermission) {
                return Result.error("无权限删除分组");
            }
        }

        boolean success = groupService.delete(gid, username);
        return success ? Result.success() : Result.error("删除失败");
    }
}
