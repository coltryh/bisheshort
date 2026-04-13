package com.ryh.shortlink.allinone.service.impl;

import com.ryh.shortlink.allinone.service.RateLimiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 防刷服务实现（基于滑动窗口算法）
 */
@Slf4j
@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    /**
     * 访问记录缓存: key=ip:action, value=[时间戳列表]
     */
    private final Map<String, RingBuffer> accessRecords = new ConcurrentHashMap<>();

    /**
     * 限制配置 (action -> [窗口秒数, 最大次数])
     */
    private static final Map<String, int[]> LIMITS = Map.of(
            "create_link", new int[]{60, 10},      // 60秒内最多创建10个短链接
            "access_link", new int[]{60, 60},     // 60秒内最多访问60次
            "login", new int[]{300, 5},            // 5分钟内最多登录5次
            "register", new int[]{3600, 3}        // 1小时内最多注册3次
    );

    @Override
    public boolean isAllowed(HttpServletRequest request, String action) {
        String clientIp = getClientIp(request);
        String key = clientIp + ":" + action;

        int[] limit = LIMITS.getOrDefault(action, new int[]{60, 30});
        int windowSeconds = limit[0];
        int maxCount = limit[1];

        RingBuffer buffer = accessRecords.computeIfAbsent(key, k -> new RingBuffer(windowSeconds));

        boolean allowed = buffer.tryAdd(maxCount);

        if (!allowed) {
            log.warn("防刷拦截: IP={}, action={}, 超过限制 {}/{}s", clientIp, action, maxCount, windowSeconds);
        }

        return allowed;
    }

    @Override
    public String getLimitMessage() {
        return "请求过于频繁，请稍后再试";
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 环形缓冲区实现滑动窗口
     */
    private static class RingBuffer {
        private final long[] timestamps;
        private final int windowSeconds;
        private int head = 0;

        RingBuffer(int windowSeconds) {
            this.windowSeconds = windowSeconds;
            this.timestamps = new long[100]; // 最多记录100个
        }

        synchronized boolean tryAdd(int maxCount) {
            long now = System.currentTimeMillis() / 1000;
            long windowStart = now - windowSeconds;

            // 清理过期记录
            int validCount = 0;
            for (int i = 0; i < timestamps.length; i++) {
                if (timestamps[i] > windowStart) {
                    validCount++;
                }
            }

            if (validCount >= maxCount) {
                return false;
            }

            // 添加新记录
            timestamps[head] = now;
            head = (head + 1) % timestamps.length;
            return true;
        }
    }
}
