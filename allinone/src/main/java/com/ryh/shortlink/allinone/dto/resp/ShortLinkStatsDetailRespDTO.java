package com.ryh.shortlink.allinone.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 短链接详细统计响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsDetailRespDTO {

    /**
     * 基础统计
     */
    private ShortLinkStatsRespDTO baseStats;

    /**
     * 每日统计
     */
    private List<ShortLinkStatsAccessDailyRespDTO> dailyStats;

    /**
     * 设备统计
     */
    private List<Map<String, Object>> deviceStats;

    /**
     * 浏览器统计
     */
    private List<Map<String, Object>> browserStats;

    /**
     * 操作系统统计
     */
    private List<Map<String, Object>> osStats;

    /**
     * 地区统计
     */
    private List<Map<String, Object>> localeStats;

    /**
     * 网络类型统计
     */
    private List<Map<String, Object>> networkStats;

    /**
     * 小时统计
     */
    private List<Map<String, Object>> hourlyStats;

    /**
     * 访问趋势（最近30天）
     */
    private List<ShortLinkStatsAccessDailyRespDTO> trendStats;
}
