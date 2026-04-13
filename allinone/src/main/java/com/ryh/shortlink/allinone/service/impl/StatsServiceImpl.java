package com.ryh.shortlink.allinone.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.dao.entity.*;
import com.ryh.shortlink.allinone.dao.mapper.*;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsRespDTO;
import com.ryh.shortlink.allinone.service.GroupService;
import com.ryh.shortlink.allinone.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final ShortLinkMapper shortLinkMapper;
    private final GroupService groupService;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;

    @Override
    public ShortLinkStatsRespDTO getOverview(String username) {
        // 获取用户的所有分组
        var groups = groupService.listByUsername(username);
        List<String> gidList = groups.stream()
                .map(g -> g.getGid())
                .toList();

        // 查询该用户的所有短链接
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(!gidList.isEmpty(), ShortLinkDO::getGid, gidList)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);

        List<ShortLinkDO> shortLinks = shortLinkMapper.selectList(queryWrapper);

        // 统计
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
                .totalGroups(groups.size())
                .todayPv(0)  // 简化版，不计算今日数据
                .todayUv(0)
                .todayUip(0)
                .build();
    }

    @Override
    public List<?> getShortLinkDailyStats(String fullShortUrl, int days) {
        // 简化版实现，返回空列表
        // 实际项目中需要查询 t_link_access_stats 表
        return new ArrayList<>();
    }

    @Override
    public ShortLinkStatsRespDTO getShortLinkStats(String fullShortUrl) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO shortLink = shortLinkMapper.selectOne(queryWrapper);

        if (shortLink == null) {
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

    @Override
    @Transactional
    public void recordAccess(String fullShortUrl, String uv, String ip, String device, String os, String browser, String network, String country, String province, String city) {
        try {
            // 标准化 fullShortUrl，去掉 http:// 前缀
            if (fullShortUrl.startsWith("http://")) {
                fullShortUrl = fullShortUrl.substring(7);
            } else if (fullShortUrl.startsWith("https://")) {
                fullShortUrl = fullShortUrl.substring(8);
            }

            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int weekday = cal.get(Calendar.DAY_OF_WEEK);
            // 转换星期：Calendar.DAY_OF_Sunday=1, 我们需要1=周一,7=周日
            // Calendar: 1=周日, 2=周一, ..., 7=周六
            // 我们需要: 1=周一, 2=周二, ..., 7=周日
            if (weekday == 1) {
                weekday = 7; // 周日
            } else {
                weekday = weekday - 1; // 周一=1, 周二=2, ...
            }

            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();

            // 1. 更新短链接基础统计 (totalPv +1, totalUv/UIP需要去重)
            ShortLinkDO shortLink = shortLinkMapper.selectByFullShortUrl(fullShortUrl);
            if (shortLink != null) {
                Integer oldPv = shortLink.getTotalPv() != null ? shortLink.getTotalPv() : 0;
                shortLink.setTotalPv(oldPv + 1);
                // 这里简化处理，实际UV/UIP需要去重
                Integer oldUv = shortLink.getTotalUv() != null ? shortLink.getTotalUv() : 0;
                shortLink.setTotalUv(oldUv + 1);
                Integer oldUip = shortLink.getTotalUip() != null ? shortLink.getTotalUip() : 0;
                shortLink.setTotalUip(oldUip + 1);
                shortLinkMapper.updateById(shortLink);
            }

            // 2. 更新每日统计 t_link_access_stats
            updateDailyStats(fullShortUrl, today, hour, weekday, uv, ip);

            // 3. 更新今日统计 t_link_stats_today
            updateTodayStats(fullShortUrl, today);

            // 4. 更新设备统计 t_link_device_stats
            updateDeviceStats(fullShortUrl, device, today);

            // 5. 更新浏览器统计 t_link_browser_stats
            updateBrowserStats(fullShortUrl, browser, today);

            // 6. 更新操作系统统计 t_link_os_stats
            updateOsStats(fullShortUrl, os, today);

            // 7. 更新地区统计 t_link_locale_stats
            updateLocaleStats(fullShortUrl, country, province, city, today);

            // 8. 更新网络统计 t_link_network_stats
            updateNetworkStats(fullShortUrl, network, today);

            log.debug("记录访问统计成功: {}", fullShortUrl);
        } catch (Exception e) {
            log.error("记录访问统计失败: {}", fullShortUrl, e);
        }
    }

    private void updateDailyStats(String fullShortUrl, Date date, int hour, int weekday, String uv, String ip) {
        LambdaQueryWrapper<LinkAccessStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkAccessStatsDO.class)
                .eq(LinkAccessStatsDO::getFullShortUrl, fullShortUrl)
                .eq(LinkAccessStatsDO::getDate, date);
        LinkAccessStatsDO stats = linkAccessStatsMapper.selectOne(queryWrapper);

        if (stats == null) {
            stats = LinkAccessStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .pv(1)
                    .uv(1)
                    .uip(1)
                    .hour(hour)
                    .weekday(weekday)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            linkAccessStatsMapper.insert(stats);
        } else {
            stats.setPv(stats.getPv() != null ? stats.getPv() + 1 : 1);
            stats.setUv(stats.getUv() != null ? stats.getUv() + 1 : 1);
            stats.setUip(stats.getUip() != null ? stats.getUip() + 1 : 1);
            stats.setHour(hour);
            stats.setWeekday(weekday);
            stats.setUpdateTime(new Date());
            linkAccessStatsMapper.updateById(stats);
        }
    }

    private void updateTodayStats(String fullShortUrl, Date date) {
        LambdaQueryWrapper<LinkStatsTodayDO> queryWrapper = Wrappers.lambdaQuery(LinkStatsTodayDO.class)
                .eq(LinkStatsTodayDO::getFullShortUrl, fullShortUrl)
                .eq(LinkStatsTodayDO::getDate, date);
        LinkStatsTodayDO stats = linkStatsTodayMapper.selectOne(queryWrapper);

        if (stats == null) {
            stats = LinkStatsTodayDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .todayPv(1)
                    .todayUv(1)
                    .todayUip(1)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            linkStatsTodayMapper.insert(stats);
        } else {
            stats.setTodayPv(stats.getTodayPv() != null ? stats.getTodayPv() + 1 : 1);
            stats.setTodayUv(stats.getTodayUv() != null ? stats.getTodayUv() + 1 : 1);
            stats.setTodayUip(stats.getTodayUip() != null ? stats.getTodayUip() + 1 : 1);
            stats.setUpdateTime(new Date());
            linkStatsTodayMapper.updateById(stats);
        }
    }

    private void updateDeviceStats(String fullShortUrl, String device, Date date) {
        LambdaQueryWrapper<LinkDeviceStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkDeviceStatsDO.class)
                .eq(LinkDeviceStatsDO::getFullShortUrl, fullShortUrl)
                .eq(LinkDeviceStatsDO::getDate, date)
                .eq(LinkDeviceStatsDO::getDevice, device);
        LinkDeviceStatsDO stats = linkDeviceStatsMapper.selectOne(queryWrapper);

        if (stats == null) {
            stats = LinkDeviceStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .device(device)
                    .cnt(1)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            linkDeviceStatsMapper.insert(stats);
        } else {
            stats.setCnt(stats.getCnt() != null ? stats.getCnt() + 1 : 1);
            stats.setUpdateTime(new Date());
            linkDeviceStatsMapper.updateById(stats);
        }
    }

    private void updateBrowserStats(String fullShortUrl, String browser, Date date) {
        LambdaQueryWrapper<LinkBrowserStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkBrowserStatsDO.class)
                .eq(LinkBrowserStatsDO::getFullShortUrl, fullShortUrl)
                .eq(LinkBrowserStatsDO::getDate, date)
                .eq(LinkBrowserStatsDO::getBrowser, browser);
        LinkBrowserStatsDO stats = linkBrowserStatsMapper.selectOne(queryWrapper);

        if (stats == null) {
            stats = LinkBrowserStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .browser(browser)
                    .cnt(1)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            linkBrowserStatsMapper.insert(stats);
        } else {
            stats.setCnt(stats.getCnt() != null ? stats.getCnt() + 1 : 1);
            stats.setUpdateTime(new Date());
            linkBrowserStatsMapper.updateById(stats);
        }
    }

    private void updateOsStats(String fullShortUrl, String os, Date date) {
        LambdaQueryWrapper<LinkOsStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkOsStatsDO.class)
                .eq(LinkOsStatsDO::getFullShortUrl, fullShortUrl)
                .eq(LinkOsStatsDO::getDate, date)
                .eq(LinkOsStatsDO::getOs, os);
        LinkOsStatsDO stats = linkOsStatsMapper.selectOne(queryWrapper);

        if (stats == null) {
            stats = LinkOsStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .os(os)
                    .cnt(1)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            linkOsStatsMapper.insert(stats);
        } else {
            stats.setCnt(stats.getCnt() != null ? stats.getCnt() + 1 : 1);
            stats.setUpdateTime(new Date());
            linkOsStatsMapper.updateById(stats);
        }
    }

    private void updateLocaleStats(String fullShortUrl, String country, String province, String city, Date date) {
        LambdaQueryWrapper<LinkLocaleStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkLocaleStatsDO.class)
                .eq(LinkLocaleStatsDO::getFullShortUrl, fullShortUrl)
                .eq(LinkLocaleStatsDO::getDate, date)
                .eq(LinkLocaleStatsDO::getCountry, country)
                .eq(LinkLocaleStatsDO::getProvince, province)
                .eq(LinkLocaleStatsDO::getCity, city);
        LinkLocaleStatsDO stats = linkLocaleStatsMapper.selectOne(queryWrapper);

        if (stats == null) {
            stats = LinkLocaleStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .country(country)
                    .province(province)
                    .city(city)
                    .cnt(1)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            linkLocaleStatsMapper.insert(stats);
        } else {
            stats.setCnt(stats.getCnt() != null ? stats.getCnt() + 1 : 1);
            stats.setUpdateTime(new Date());
            linkLocaleStatsMapper.updateById(stats);
        }
    }

    private void updateNetworkStats(String fullShortUrl, String network, Date date) {
        LambdaQueryWrapper<LinkNetworkStatsDO> queryWrapper = Wrappers.lambdaQuery(LinkNetworkStatsDO.class)
                .eq(LinkNetworkStatsDO::getFullShortUrl, fullShortUrl)
                .eq(LinkNetworkStatsDO::getDate, date)
                .eq(LinkNetworkStatsDO::getNetwork, network);
        LinkNetworkStatsDO stats = linkNetworkStatsMapper.selectOne(queryWrapper);

        if (stats == null) {
            stats = LinkNetworkStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .network(network)
                    .cnt(1)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            linkNetworkStatsMapper.insert(stats);
        } else {
            stats.setCnt(stats.getCnt() != null ? stats.getCnt() + 1 : 1);
            stats.setUpdateTime(new Date());
            linkNetworkStatsMapper.updateById(stats);
        }
    }
}
