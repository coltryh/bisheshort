package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.service.PermissionService;
import com.ryh.shortlink.allinone.service.ShortLinkService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final ShortLinkService shortLinkService;
    private final PermissionService permissionService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview(HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }

        Long userId = SessionUtils.getUserId(session);
        if (!SessionUtils.isAdmin(session)) {
            boolean hasPermission = permissionService.hasPermission(userId, "STATS_VIEW");
            if (!hasPermission) {
                return Result.error("无权限查看统计");
            }
        }

        List<ShortLinkDO> links = shortLinkService.listByUsername(username);

        int totalPv = 0;
        int totalUv = 0;
        int totalUip = 0;

        for (ShortLinkDO link : links) {
            totalPv += link.getTotalPv() != null ? link.getTotalPv() : 0;
            totalUv += link.getTotalUv() != null ? link.getTotalUv() : 0;
            totalUip += link.getTotalUip() != null ? link.getTotalUip() : 0;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalPv", totalPv);
        result.put("totalUv", totalUv);
        result.put("totalUip", totalUip);
        result.put("linkCount", links.size());

        return Result.success(result);
    }

    @GetMapping("/link/{id}")
    public Result<ShortLinkDO> linkStats(@PathVariable Long id, HttpSession session) {
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
}
