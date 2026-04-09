package com.ryh.shortlink.allinone.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控过滤器
 */
@Slf4j
@Component
public class PerformanceFilter implements Filter {

    /**
     * 请求计数器
     */
    private static final AtomicLong REQUEST_COUNT = new AtomicLong(0);

    /**
     * 错误计数器
     */
    private static final AtomicLong ERROR_COUNT = new AtomicLong(0);

    /**
     * 响应时间统计（URI -> 总响应时间）
     */
    private static final ConcurrentHashMap<String, AtomicLong> RESPONSE_TIME_MAP = new ConcurrentHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("性能监控过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 开始时间
        long startTime = System.currentTimeMillis();

        // 请求计数
        REQUEST_COUNT.incrementAndGet();

        // 请求URI
        String uri = httpRequest.getRequestURI();

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            ERROR_COUNT.incrementAndGet();
            throw e;
        } finally {
            // 计算响应时间
            long duration = System.currentTimeMillis() - startTime;

            // 更新响应时间统计
            RESPONSE_TIME_MAP.computeIfAbsent(uri, k -> new AtomicLong(0))
                    .addAndGet(duration);

            // 记录慢请求（超过1秒）
            if (duration > 1000) {
                log.warn("慢请求检测: URI={}, Method={}, Duration={}ms, IP={}",
                        uri, httpRequest.getMethod(), duration, getClientIp(httpRequest));
            }

            // 记录错误请求
            if (httpResponse.getStatus() >= 400) {
                log.warn("异常响应: URI={}, Status={}, Duration={}ms",
                        uri, httpResponse.getStatus(), duration);
            }
        }
    }

    @Override
    public void destroy() {
        log.info("性能监控过滤器销毁");
        log.info("总请求数: {}, 总错误数: {}", REQUEST_COUNT.get(), ERROR_COUNT.get());
        RESPONSE_TIME_MAP.forEach((uri, time) -> {
            log.info("URI={}, TotalTime={}ms", uri, time.get());
        });
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
            ip = request.getRemoteAddr();
        }
        // 多代理情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取请求总数
     */
    public static long getRequestCount() {
        return REQUEST_COUNT.get();
    }

    /**
     * 获取错误总数
     */
    public static long getErrorCount() {
        return ERROR_COUNT.get();
    }
}
