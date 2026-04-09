package com.ryh.shortlink.allinone.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 短链接详细统计响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsDetailRespDTO {

    // 基础统计（扁平化到顶层）
    private Integer totalPv;
    private Integer totalUv;
    private Integer totalUip;

    // 每日访问统计
    private List<ShortLinkStatsAccessDailyRespDTO> daily;

    // 24小时分布
    private List<Integer> hourStats;

    // 一周分布
    private List<Integer> weekdayStats;

    // 高频访问IP
    private List<StatsIpRespDTO> topIpStats;

    // 设备统计
    private List<StatsDeviceRespDTO> deviceStats;

    // 网络类型统计
    private List<StatsDeviceRespDTO> networkStats;

    // 操作系统统计
    private List<StatsOsRespDTO> osStats;

    // 浏览器统计
    private List<StatsBrowserRespDTO> browserStats;

    // 中国地图地区统计
    private List<StatsLocaleRespDTO> localeCnStats;

    // 世界地图地区统计
    private List<StatsLocaleRespDTO> localeWorldStats;

    // 访客类型统计
    private List<StatsUvTypeRespDTO> uvTypeStats;
}