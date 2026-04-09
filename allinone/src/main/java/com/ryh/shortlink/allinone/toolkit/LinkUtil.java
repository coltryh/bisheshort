package com.ryh.shortlink.allinone.toolkit;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * 链接工具类
 */
public class LinkUtil {

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    /**
     * 获取实际IP地址
     */
    public static String getActualIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况,第一个IP为真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取操作系统信息
     */
    public static String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        if (userAgent.contains("Windows")) {
            if (userAgent.contains("Windows NT 10.0")) {
                return "Windows 10";
            } else if (userAgent.contains("Windows NT 6.3")) {
                return "Windows 8.1";
            } else if (userAgent.contains("Windows NT 6.2")) {
                return "Windows 8";
            } else if (userAgent.contains("Windows NT 6.1")) {
                return "Windows 7";
            }
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "Mac";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Unix")) {
            return "Unix";
        } else if (userAgent.contains("iPhone")) {
            return "iOS";
        } else if (userAgent.contains("Android")) {
            return "Android";
        }
        return "Unknown";
    }

    /**
     * 获取浏览器信息
     */
    public static String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "IE";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Opera") || userAgent.contains("OPR")) {
            return "Opera";
        }
        return "Unknown";
    }

    /**
     * 获取设备类型
     */
    public static String getDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        if (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone")) {
            return "Mobile";
        }
        return "PC";
    }

    /**
     * 获取网络类型
     */
    public static String getNetwork(HttpServletRequest request) {
        // 简化实现，实际项目中可能需要根据IP库判断
        return "Unknown";
    }

    /**
     * 从URL提取域名
     */
    public static String extractDomain(String url) {
        if (StrUtil.isBlank(url)) {
            return null;
        }
        try {
            String domain;
            if (url.startsWith(HTTP_PREFIX)) {
                domain = url.substring(HTTP_PREFIX.length());
            } else if (url.startsWith(HTTPS_PREFIX)) {
                domain = url.substring(HTTPS_PREFIX.length());
            } else {
                domain = url;
            }
            int slashIndex = domain.indexOf('/');
            if (slashIndex > 0) {
                domain = domain.substring(0, slashIndex);
            }
            return domain;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取链接缓存有效时间
     */
    public static long getLinkCacheValidTime(java.util.Date validDate) {
        if (validDate == null) {
            // 永久有效，缓存30天
            return TimeUnit.DAYS.toMillis(30);
        }
        long diff = validDate.getTime() - System.currentTimeMillis();
        if (diff <= 0) {
            // 已过期，返回1分钟
            return TimeUnit.MINUTES.toMillis(1);
        }
        // 最多缓存30天
        return Math.min(diff, TimeUnit.DAYS.toMillis(30));
    }
}
