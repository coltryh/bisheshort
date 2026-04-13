package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dao.entity.LinkBlacklistDO;
import com.ryh.shortlink.allinone.service.LinkBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 黑名单管理控制器
 */
@RestController
@RequestMapping("/api/blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    private final LinkBlacklistService linkBlacklistService;

    /**
     * 检查URL是否在黑名单中
     */
    @GetMapping("/check")
    public Result<Boolean> checkUrl(@RequestParam String url) {
        boolean blacklisted = linkBlacklistService.isBlacklisted(url);
        return Result.success(blacklisted);
    }

    /**
     * 获取所有黑名单列表
     */
    @GetMapping("/list")
    public Result<List<LinkBlacklistDO>> listBlacklist(HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        if (username == null) {
            return Result.error("请先登录");
        }
        List<LinkBlacklistDO> list = linkBlacklistService.listAllActive();
        return Result.success(list);
    }

    /**
     * 添加到黑名单
     */
    @PostMapping("/add")
    public Result<Void> addBlacklist(
            @RequestParam String domain,
            @RequestParam(defaultValue = "domain") String type,
            @RequestParam(required = false) String reason,
            HttpServletRequest request) {

        String username = getUsernameFromRequest(request);
        if (username == null) {
            return Result.error("请先登录");
        }

        if (!"admin".equals(getRoleFromRequest(request))) {
            return Result.error("无权限操作");
        }

        if (reason == null || reason.isEmpty()) {
            reason = "管理员手动封禁";
        }

        boolean success = linkBlacklistService.addToBlacklist(domain, type, reason);
        return success ? Result.success() : Result.error("添加失败");
    }

    /**
     * 解封（软删除）
     */
    @PostMapping("/unblock")
    public Result<Void> unblock(@RequestParam Long id, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        if (username == null) {
            return Result.error("请先登录");
        }

        if (!"admin".equals(getRoleFromRequest(request))) {
            return Result.error("无权限操作");
        }

        boolean success = linkBlacklistService.unblock(id);
        return success ? Result.success() : Result.error("解封失败");
    }

    /**
     * 删除黑名单记录
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteBlacklist(@RequestParam Long id, HttpServletRequest request) {
        String username = getUsernameFromRequest(request);
        if (username == null) {
            return Result.error("请先登录");
        }

        if (!"admin".equals(getRoleFromRequest(request))) {
            return Result.error("无权限操作");
        }

        boolean success = linkBlacklistService.removeFromBlacklist(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    private String getUsernameFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (token != null && com.ryh.shortlink.allinone.common.utils.JwtUtils.validateToken(token)) {
            return com.ryh.shortlink.allinone.common.utils.JwtUtils.getUsernameFromToken(token);
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            return SessionUtils.getUsername(session);
        }
        return null;
    }

    private String getRoleFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (token != null && com.ryh.shortlink.allinone.common.utils.JwtUtils.validateToken(token)) {
            return com.ryh.shortlink.allinone.common.utils.JwtUtils.getRoleFromToken(token);
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            var user = SessionUtils.getUser(session);
            return user != null ? user.getRole() : null;
        }
        return null;
    }
}
