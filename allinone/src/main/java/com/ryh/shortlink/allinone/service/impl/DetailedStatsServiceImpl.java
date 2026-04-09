package com.ryh.shortlink.allinone.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.dao.entity.*;
import com.ryh.shortlink.allinone.dao.mapper.*;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsAccessDailyRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsDetailRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsRespDTO;
import com.ryh.shortlink.allinone.service.DetailedStatsService;
import com.ryh.shortlink.allinone.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 详细统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DetailedStatsServiceImpl implements DetailedStatsService {

    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final ShortLinkMapper shortLinkMapper;
    private final GroupService groupService;

    @Override
    public ShortLinkStatsDetailRespDTO getDetailedStats(String fullShortUrl, int days) {
        ShortLinkStatsDetailRespDTO detailStats = ShortLinkStatsDetailRespDTO.builder()
                .baseStats(getShortLinkStats(fullShortUrl))
                .dailyStats(getShortLinkDailyStats(fullShortUrl, days))
                .deviceStats(getDeviceDistribution(fullShortUrl, days))
                .browserStats(getBrowserDistribution(fullShortUrl, days))
                .osStats(getOsDistribution(fullShortUrl, days))
                .localeStats(getLocaleDistribution(fullShortUrl, days))
                .networkStats(getNetworkDistribution(fullShortUrl, days))
                .hourlyStats(getHourlyTrend(fullShortUrl, null))
                .trendStats(getShortLinkDailyStats(fullShortUrl, 30))
                .build();

        return detailStats;
    }

    @Override
    public ShortLinkStatsRespDTO getGroupStats(List<String> gidList) {
        if (gidList == null || gidList.isEmpty()) {
            return buildEmptyStats();
        }

        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(ShortLinkDO::getGid, gidList)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);

        List<ShortLinkDO> shortLinks = shortLinkMapper.selectList(queryWrapper);

        return aggregateStats(shortLinks);
    }

    @Override
    public ShortLinkStatsRespDTO getUserTotalStats(String username) {
        var groups = groupService.listByUsername(username);
        List<String> gidList = groups.stream()
                .map(GroupDO::getGid)
                .collect(Collectors.toList());

        return getGroupStats(gidList);
    }

    @Override
    public List<ShortLinkStatsRespDTO> getTopShortLinks(String username, int topN) {
        var groups = groupService.listByUsername(username);
        List<String> gidList = groups.stream()
                .map(GroupDO::getGid)
                .collect(Collectors.toList());

        if (gidList.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(ShortLinkDO::getGid, gidList)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .orderByDesc(ShortLinkDO::getTotalPv)
                .last("LIMIT " + topN);

        List<ShortLinkDO> shortLinks = shortLinkMapper.selectList(queryWrapper);

        return shortLinks.stream().map(link -> ShortLinkStatsRespDTO.builder()
                .totalPv(link.getTotalPv() != null ? link.getTotalPv() : 0)
                .totalUv(link.getTotalUv() != null ? link.getTotalUv() : 0)
                .totalUip(link.getTotalUip() != null ? link.getTotalUip() : 0)
                .totalShortLinks(1)
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<?> getHourlyTrend(String fullShortUrl, String date) {
        // 简化实现，返回空列表
        // 实际需要查询 t_link_access_logs 按小时聚合
        return new ArrayList<>();
    }

    @Override
    public List<?> getDeviceDistribution(String fullShortUrl, int days) {
        Date startDate = getStartDate(days);
        Date endDate = new Date();

        LambdaQueryWrapper<LinkDeviceStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkDeviceStatsDO.class)
                .eq(LinkDeviceStatsDO::getFullShortUrl, fullShortUrl)
                .ge(LinkDeviceStatsDO::getDate, startDate)
                .le(LinkDeviceStatsDO::getDate, endDate)
                .orderByDesc(LinkDeviceStatsDO::getCnt);

        List<LinkDeviceStatsDO> deviceStats = linkDeviceStatsMapper.selectList(queryWrapper);

        // 按设备类型聚合
        Map<String, Integer> deviceMap = new HashMap<>();
        for (LinkDeviceStatsDO stat : deviceStats) {
            String device = stat.getDevice() != null ? stat.getDevice() : "Unknown";
            deviceMap.merge(device, stat.getCnt(), Integer::sum);
        }

        return deviceMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", e.getKey());
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<?> getBrowserDistribution(String fullShortUrl, int days) {
        Date startDate = getStartDate(days);
        Date endDate = new Date();

        LambdaQueryWrapper<LinkBrowserStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkBrowserStatsDO.class)
                .eq(LinkBrowserStatsDO::getFullShortUrl, fullShortUrl)
                .ge(LinkBrowserStatsDO::getDate, startDate)
                .le(LinkBrowserStatsDO::getDate, endDate)
                .orderByDesc(LinkBrowserStatsDO::getCnt);

        List<LinkBrowserStatsDO> browserStats = linkBrowserStatsMapper.selectList(queryWrapper);

        // 按浏览器聚合
        Map<String, Integer> browserMap = new HashMap<>();
        for (LinkBrowserStatsDO stat : browserStats) {
            String browser = stat.getBrowser() != null ? stat.getBrowser() : "Unknown";
            browserMap.merge(browser, stat.getCnt(), Integer::sum);
        }

        return browserMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", e.getKey());
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<?> getOsDistribution(String fullShortUrl, int days) {
        Date startDate = getStartDate(days);
        Date endDate = new Date();

        LambdaQueryWrapper<LinkOsStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkOsStatsDO.class)
                .eq(LinkOsStatsDO::getFullShortUrl, fullShortUrl)
                .ge(LinkOsStatsDO::getDate, startDate)
                .le(LinkOsStatsDO::getDate, endDate)
                .orderByDesc(LinkOsStatsDO::getCnt);

        List<LinkOsStatsDO> osStats = linkOsStatsMapper.selectList(queryWrapper);

        // 按操作系统聚合
        Map<String, Integer> osMap = new HashMap<>();
        for (LinkOsStatsDO stat : osStats) {
            String os = stat.getOs() != null ? stat.getOs() : "Unknown";
            osMap.merge(os, stat.getCnt(), Integer::sum);
        }

        return osMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", e.getKey());
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<?> getLocaleDistribution(String fullShortUrl, int days) {
        Date startDate = getStartDate(days);
        Date endDate = new Date();

        LambdaQueryWrapper<LinkLocaleStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkLocaleStatsDO.class)
                .eq(LinkLocaleStatsDO::getFullShortUrl, fullShortUrl)
                .ge(LinkLocaleStatsDO::getDate, startDate)
                .le(LinkLocaleStatsDO::getDate, endDate)
                .orderByDesc(LinkLocaleStatsDO::getCnt);

        List<LinkLocaleStatsDO> localeStats = linkLocaleStatsMapper.selectList(queryWrapper);

        // 按地区聚合
        Map<String, Integer> localeMap = new HashMap<>();
        for (LinkLocaleStatsDO stat : localeStats) {
            String locale = (stat.getProvince() != null ? stat.getProvince() : "Unknown");
            localeMap.merge(locale, stat.getCnt(), Integer::sum);
        }

        return localeMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", e.getKey());
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<?> getNetworkDistribution(String fullShortUrl, int days) {
        Date startDate = getStartDate(days);
        Date endDate = new Date();

        LambdaQueryWrapper<LinkNetworkStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkNetworkStatsDO.class)
                .eq(LinkNetworkStatsDO::getFullShortUrl, fullShortUrl)
                .ge(LinkNetworkStatsDO::getDate, startDate)
                .le(LinkNetworkStatsDO::getDate, endDate)
                .orderByDesc(LinkNetworkStatsDO::getCnt);

        List<LinkNetworkStatsDO> networkStats = linkNetworkStatsMapper.selectList(queryWrapper);

        // 按网络类型聚合
        Map<String, Integer> networkMap = new HashMap<>();
        for (LinkNetworkStatsDO stat : networkStats) {
            String network = stat.getNetwork() != null ? stat.getNetwork() : "Unknown";
            networkMap.merge(network, stat.getCnt(), Integer::sum);
        }

        return networkMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", e.getKey());
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ShortLinkStatsAccessDailyRespDTO> getShortLinkDailyStats(String fullShortUrl, int days) {
        Date startDate = getStartDate(days);
        Date endDate = new Date();

        LambdaQueryWrapper<LinkAccessStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkAccessStatsDO.class)
                .eq(LinkAccessStatsDO::getFullShortUrl, fullShortUrl)
                .ge(LinkAccessStatsDO::getDate, startDate)
                .le(LinkAccessStatsDO::getDate, endDate)
                .orderByDesc(LinkAccessStatsDO::getDate);

        List<LinkAccessStatsDO> statsList = linkAccessStatsMapper.selectList(queryWrapper);

        return statsList.stream().map(stat -> ShortLinkStatsAccessDailyRespDTO.builder()
                .date(stat.getDate())
                .pv(stat.getPv())
                .uv(stat.getUv())
                .uip(stat.getUip())
                .build()).collect(Collectors.toList());
    }

    @Override
    public ShortLinkStatsRespDTO getShortLinkStats(String fullShortUrl) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO shortLink = shortLinkMapper.selectOne(queryWrapper);

        if (shortLink == null) {
            return buildEmptyStats();
        }

        return ShortLinkStatsRespDTO.builder()
                .totalPv(shortLink.getTotalPv() != null ? shortLink.getTotalPv() : 0)
                .totalUv(shortLink.getTotalUv() != null ? shortLink.getTotalUv() : 0)
                .totalUip(shortLink.getTotalUip() != null ? shortLink.getTotalUip() : 0)
                .totalShortLinks(1)
                .totalGroups(0)
                .todayPv(0)
                .todayUv(0)
                .todayUip(0)
                .build();
    }

    /**
     * 获取指定天数前的日期
     */
    private Date getStartDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }

    /**
     * 构建空统计数据
     */
    private ShortLinkStatsRespDTO buildEmptyStats() {
        return ShortLinkStatsRespDTO.builder()
                .totalPv(0)
                .totalUv(0)
                .totalUip(0)
                .totalShortLinks(0)
                .totalGroups(0)
                .todayPv(0)
                .todayUv(0)
                .todayUip(0)
                .build();
    }

    /**
     * 聚合统计
     */
    private ShortLinkStatsRespDTO aggregateStats(List<ShortLinkDO> shortLinks) {
        int totalPv = 0;
        int totalUv = 0;
        int totalUip = 0;

        for (ShortLinkDO link : shortLinks) {
            totalPv += link.getTotalPv() != null ? link.getTotalPv() : 0;
            totalUv += link.getTotalUv() != null ? link.getTotalUv() : 0;
            totalUip += link.getTotalUip() != null ? link.getTotalUip() : 0;
        }

        return ShortLinkStatsRespDTO.builder()
                .totalPv(totalPv)
                .totalUv(totalUv)
                .totalUip(totalUip)
                .totalShortLinks(shortLinks.size())
                .totalGroups(0)
                .todayPv(0)
                .todayUv(0)
                .todayUip(0)
                .build();
    }
}
