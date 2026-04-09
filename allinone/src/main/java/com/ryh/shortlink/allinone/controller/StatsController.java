package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsRespDTO;
import com.ryh.shortlink.allinone.service.StatsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * 获取统计概览
     */
    @GetMapping("/overview")
    public Result<ShortLinkStatsRespDTO> getOverview(HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            ShortLinkStatsRespDTO overview = statsService.getOverview(username);
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取指定短链接的日访问统计
     */
    @GetMapping("/daily")
    public Result<List<?>> getDailyStats(
            @RequestParam String fullShortUrl,
            @RequestParam(defaultValue = "7") int days,
            HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            List<?> stats = statsService.getShortLinkDailyStats(fullShortUrl, days);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取短链接统计
     */
    @GetMapping("/link")
    public Result<ShortLinkStatsRespDTO> getLinkStats(
            @RequestParam String fullShortUrl,
            HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            ShortLinkStatsRespDTO stats = statsService.getShortLinkStats(fullShortUrl);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
