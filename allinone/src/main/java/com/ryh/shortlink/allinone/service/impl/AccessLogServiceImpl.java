package com.ryh.shortlink.allinone.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.dao.entity.LinkAccessLogsDO;
import com.ryh.shortlink.allinone.dao.mapper.LinkAccessLogsMapper;
import com.ryh.shortlink.allinone.service.AccessLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 访问日志服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccessLogServiceImpl implements AccessLogService {

    private final LinkAccessLogsMapper linkAccessLogsMapper;

    @Override
    @Async
    public void recordAccessLog(String fullShortUrl, String uv, String ip, String country, String province,
                                 String city, String device, String os, String browser, String network) {
        try {
            LinkAccessLogsDO accessLog = LinkAccessLogsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .uv(uv)
                    .ip(ip)
                    .country(country)
                    .province(province)
                    .city(city)
                    .device(device)
                    .os(os)
                    .browser(browser)
                    .network(network)
                    .accessTime(new Date())
                    .createTime(new Date())
                    .build();

            linkAccessLogsMapper.insert(accessLog);
            log.debug("记录访问日志成功: {}", fullShortUrl);
        } catch (Exception e) {
            log.error("记录访问日志失败: {}", fullShortUrl, e);
        }
    }

    @Override
    public List<LinkAccessLogsDO> listAccessLogs(String fullShortUrl, Date startTime, Date endTime) {
        LambdaQueryWrapper<LinkAccessLogsDO> queryWrapper = Wrappers.lambdaQuery(LinkAccessLogsDO.class)
                .eq(LinkAccessLogsDO::getFullShortUrl, fullShortUrl)
                .ge(startTime != null, LinkAccessLogsDO::getAccessTime, startTime)
                .le(endTime != null, LinkAccessLogsDO::getAccessTime, endTime)
                .orderByDesc(LinkAccessLogsDO::getAccessTime);
        return linkAccessLogsMapper.selectList(queryWrapper);
    }

    @Override
    public Long getUvCount(String fullShortUrl, Date startTime, Date endTime) {
        return linkAccessLogsMapper.countUniqueVisitors(fullShortUrl, startTime, endTime);
    }

    @Override
    public Long getUipCount(String fullShortUrl, Date startTime, Date endTime) {
        return linkAccessLogsMapper.countUniqueIps(fullShortUrl, startTime, endTime);
    }

    @Override
    public Long getPvCount(String fullShortUrl, Date startTime, Date endTime) {
        LambdaQueryWrapper<LinkAccessLogsDO> queryWrapper = Wrappers.lambdaQuery(LinkAccessLogsDO.class)
                .eq(LinkAccessLogsDO::getFullShortUrl, fullShortUrl)
                .ge(startTime != null, LinkAccessLogsDO::getAccessTime, startTime)
                .le(endTime != null, LinkAccessLogsDO::getAccessTime, endTime);
        return linkAccessLogsMapper.selectCount(queryWrapper);
    }
}
