package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.service.PermissionService;
import com.ryh.shortlink.allinone.service.ShortLinkService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/link")
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;
    private final PermissionService permissionService;

    @GetMapping("/list")
    public Result<List<ShortLinkDO>> list(@RequestParam(required = false) String gid, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }

        List<ShortLinkDO> links;
        if (gid != null && !gid.isEmpty()) {
            links = shortLinkService.listByGid(gid);
        } else if (SessionUtils.isAdmin(session)) {
            links = shortLinkService.listByUsername(username);
        } else {
            links = shortLinkService.listByUsername(username);
        }
        return Result.success(links);
    }

    @GetMapping("/{id}")
    public Result<ShortLinkDO> getById(@PathVariable Long id, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }

        ShortLinkDO link = shortLinkService.getById(id);
        if (link == null) {
            return Result.error("短链接不存在");
        }
        return Result.success(link);
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody ShortLinkDO shortLink, HttpSession session) {
        Long userId = SessionUtils.getUserId(session);
        if (userId == null) {
            return Result.error("未登录");
        }

        if (!SessionUtils.isAdmin(session)) {
            boolean hasPermission = permissionService.hasPermission(userId, "LINK_CREATE");
            if (!hasPermission) {
                return Result.error("无权限创建短链接");
            }
        }

        boolean success = shortLinkService.save(shortLink);
        return success ? Result.success() : Result.error("创建失败");
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody ShortLinkDO shortLink, HttpSession session) {
        Long userId = SessionUtils.getUserId(session);
        if (userId == null) {
            return Result.error("未登录");
        }

        if (!SessionUtils.isAdmin(session)) {
            boolean hasPermission = permissionService.hasPermission(userId, "LINK_UPDATE");
            if (!hasPermission) {
                return Result.error("无权限修改短链接");
            }
        }

        boolean success = shortLinkService.update(shortLink);
        return success ? Result.success() : Result.error("更新失败");
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id, HttpSession session) {
        Long userId = SessionUtils.getUserId(session);
        if (userId == null) {
            return Result.error("未登录");
        }

        if (!SessionUtils.isAdmin(session)) {
            boolean hasPermission = permissionService.hasPermission(userId, "LINK_DELETE");
            if (!hasPermission) {
                return Result.error("无权限删除短链接");
            }
        }

        boolean success = shortLinkService.delete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
