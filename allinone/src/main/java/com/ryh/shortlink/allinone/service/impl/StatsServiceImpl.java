package com.ryh.shortlink.allinone.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.dao.mapper.ShortLinkMapper;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkStatsRespDTO;
import com.ryh.shortlink.allinone.service.GroupService;
import com.ryh.shortlink.allinone.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final ShortLinkMapper shortLinkMapper;
    private final GroupService groupService;

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
                .todayPv(shortLink.getTodayPv() != null ? shortLink.getTodayPv() : 0)
                .todayUv(shortLink.getTodayUv() != null ? shortLink.getTodayUv() : 0)
                .todayUip(shortLink.getTodayUip() != null ? shortLink.getTodayUip() : 0)
                .build();
    }
}
