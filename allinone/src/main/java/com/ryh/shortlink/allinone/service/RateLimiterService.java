package com.ryh.shortlink.allinone.service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 防刷服务接口
 */
public interface RateLimiterService {

    /**
     * 检查请求是否超过限制
     * @param request HTTP请求
     * @param action 操作类型 (如: create_link, access_link, login等)
     * @return true=通过, false=被限制
     */
    boolean isAllowed(HttpServletRequest request, String action);

    /**
     * 获取限制描述
     * @return 限制信息
     */
    String getLimitMessage();
}
