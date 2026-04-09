package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsDetailRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsRespDTO;
import com.ryh.shortlink.allinone.service.DetailedStatsService;
import com.ryh.shortlink.allinone.service.StatsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final DetailedStatsService detailedStatsService;

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

    /**
     * 获取短链接详细统计（兼容前端smallLinkPage.queryLinkStats）
     */
    @GetMapping
    public Result<ShortLinkStatsDetailRespDTO> getLinkStatsDetail(
            @RequestParam(required = false) String gid,
            @RequestParam String fullShortUrl,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            // 默认7天
            int days = 7;
            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                // 计算日期范围天数
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);
                long diff = end.getTime() - start.getTime();
                days = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
            }
            ShortLinkStatsDetailRespDTO detailStats = detailedStatsService.getDetailedStats(fullShortUrl, days);
            return Result.success(detailStats);
        } catch (Exception e) {
            log.error("获取详细统计失败", e);
            return Result.error(e.getMessage());
        }
    }
}
