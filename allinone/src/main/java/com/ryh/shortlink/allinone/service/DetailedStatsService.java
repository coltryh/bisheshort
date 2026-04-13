package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsDetailRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsRespDTO;

import java.util.List;

/**
 * 详细统计服务接口
 */
public interface DetailedStatsService {

    /**
     * 获取短链接详细统计
     */
    ShortLinkStatsDetailRespDTO getDetailedStats(String fullShortUrl, int days);

    /**
     * 获取分组统计概览
     */
    ShortLinkStatsRespDTO getGroupStats(List<String> gidList);

    /**
     * 获取用户所有短链接的汇总统计
     */
    ShortLinkStatsRespDTO getUserTotalStats(String username);

    /**
     * 获取TOP N 短链接统计
     */
    List<ShortLinkStatsRespDTO> getTopShortLinks(String username, int topN);

    /**
     * 获取访问趋势（按小时）
     */
    List<?> getHourlyTrend(String fullShortUrl, String date);

    /**
     * 获取设备分布
     */
    List<?> getDeviceDistribution(String fullShortUrl, int days);

    /**
     * 获取浏览器分布
     */
    List<?> getBrowserDistribution(String fullShortUrl, int days);

    /**
     * 获取操作系统分布
     */
    List<?> getOsDistribution(String fullShortUrl, int days);

    /**
     * 获取网络类型分布
     */
    List<?> getNetworkDistribution(String fullShortUrl, int days);
}
