package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsRespDTO;

import java.util.List;

/**
 * 统计服务接口
 */
public interface StatsService {

    /**
     * 获取统计概览
     */
    ShortLinkStatsRespDTO getOverview(String username);

    /**
     * 获取指定短链接的日访问统计
     */
    List<?> getShortLinkDailyStats(String fullShortUrl, int days);

    /**
     * 获取短链接的UV/PV/UIP统计
     */
    ShortLinkStatsRespDTO getShortLinkStats(String fullShortUrl);

    /**
     * 记录短链接访问
     */
    void recordAccess(String fullShortUrl, String uv, String ip, String device, String os, String browser, String network, String country, String province, String city);
}
