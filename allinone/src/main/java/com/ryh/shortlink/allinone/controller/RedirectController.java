package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.service.ShortLinkService;
import com.ryh.shortlink.allinone.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 短链接跳转控制器
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class RedirectController {

    private final ShortLinkService shortLinkService;
    private final StatsService statsService;

    /**
     * 短链接跳转
     */
    @GetMapping("/{shortUri}")
    public void redirect(@PathVariable String shortUri, HttpServletRequest request, HttpServletResponse response) {
        try {
            String fullShortUrl = request.getRequestURL().toString();
            String originalUrl = shortLinkService.restoreUrl(shortUri);
            if (originalUrl != null) {
                // 记录访问统计
                String uv = getUv(request);
                String ip = getClientIp(request);
                String device = getDevice(request);
                String os = getOs(request);
                String browser = getBrowser(request);
                String network = "Unknown";
                String country = "Unknown";
                String province = "Unknown";
                String city = "Unknown";

                // 更新短链接基础统计
                statsService.recordAccess(fullShortUrl, uv, ip, device, os, browser, network, country, province, city);

                log.info("短链接跳转: {} -> {} (uv={}, ip={})", shortUri, originalUrl, uv, ip);
                response.sendRedirect(originalUrl);
            } else {
                // 短链接不存在或已过期
                log.warn("短链接不存在: {}", shortUri);
                response.sendRedirect("/page/notfound");
            }
        } catch (IOException e) {
            log.error("短链接跳转失败: {}", shortUri, e);
            try {
                response.sendRedirect("/page/error");
            } catch (IOException ex) {
                // ignore
            }
        }
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
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 生成UV (Unique Visitor)
     */
    private String getUv(HttpServletRequest request) {
        String uv = (String) request.getSession().getAttribute("uv");
        if (uv == null) {
            uv = UUID.randomUUID().toString();
            request.getSession().setAttribute("uv", uv);
        }
        return uv;
    }

    /**
     * 获取设备类型
     */
    private String getDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "Mobile";
        }
        return "Desktop";
    }

    /**
     * 获取操作系统
     */
    private String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows")) return "Windows";
        if (userAgent.contains("mac")) return "MacOS";
        if (userAgent.contains("linux")) return "Linux";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("iphone") || userAgent.contains("ipad")) return "iOS";
        return "Unknown";
    }

    /**
     * 获取浏览器
     */
    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("chrome")) return "Chrome";
        if (userAgent.contains("safari")) return "Safari";
        if (userAgent.contains("firefox")) return "Firefox";
        if (userAgent.contains("edge")) return "Edge";
        if (userAgent.contains("msie") || userAgent.contains("trident")) return "IE";
        return "Unknown";
    }
}
