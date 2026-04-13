package com.ryh.shortlink.allinone.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.dao.entity.LinkBlacklistDO;
import com.ryh.shortlink.allinone.dao.mapper.LinkBlacklistMapper;
import com.ryh.shortlink.allinone.service.LinkBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkBlacklistServiceImpl implements LinkBlacklistService {

    private final LinkBlacklistMapper linkBlacklistMapper;

    @Override
    public boolean isBlacklisted(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        try {
            // 提取域名
            String domain = extractDomain(url);
            if (domain == null) {
                return false;
            }

            // 查询黑名单
            LambdaQueryWrapper<LinkBlacklistDO> queryWrapper = Wrappers.lambdaQuery(LinkBlacklistDO.class)
                    .eq(LinkBlacklistDO::getStatus, 0); // 只查询有效的

            List<LinkBlacklistDO> blacklist = linkBlacklistMapper.selectList(queryWrapper);

            for (LinkBlacklistDO item : blacklist) {
                switch (item.getType()) {
                    case "domain":
                        // 精确匹配域名
                        if (domain.equals(item.getDomain()) || domain.endsWith("." + item.getDomain())) {
                            log.warn("域名在黑名单中: {}, 原因: {}", domain, item.getReason());
                            return true;
                        }
                        break;
                    case "url":
                        // 完整URL匹配
                        if (url.contains(item.getDomain())) {
                            log.warn("URL在黑名单中: {}, 原因: {}", url, item.getReason());
                            return true;
                        }
                        break;
                    case "keyword":
                        // 关键词匹配
                        if (url.toLowerCase().contains(item.getDomain().toLowerCase())) {
                            log.warn("URL包含敏感关键词: {}, 原因: {}", item.getDomain(), item.getReason());
                            return true;
                        }
                        break;
                }
            }
        } catch (Exception e) {
            log.error("检查黑名单失败: {}", url, e);
        }

        return false;
    }

    @Override
    public List<LinkBlacklistDO> listAllActive() {
        LambdaQueryWrapper<LinkBlacklistDO> queryWrapper = Wrappers.lambdaQuery(LinkBlacklistDO.class)
                .eq(LinkBlacklistDO::getStatus, 0)
                .orderByDesc(LinkBlacklistDO::getCreateTime);
        return linkBlacklistMapper.selectList(queryWrapper);
    }

    @Override
    public boolean addToBlacklist(String domain, String type, String reason) {
        try {
            LinkBlacklistDO blacklist = LinkBlacklistDO.builder()
                    .domain(domain)
                    .type(type)
                    .reason(reason)
                    .status(0)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            linkBlacklistMapper.insert(blacklist);
            log.info("添加黑名单成功: {} (类型: {})", domain, type);
            return true;
        } catch (Exception e) {
            log.error("添加黑名单失败: {}", domain, e);
            return false;
        }
    }

    @Override
    public boolean removeFromBlacklist(Long id) {
        try {
            linkBlacklistMapper.deleteById(id);
            log.info("删除黑名单记录: {}", id);
            return true;
        } catch (Exception e) {
            log.error("删除黑名单失败: {}", id, e);
            return false;
        }
    }

    @Override
    public boolean unblock(Long id) {
        try {
            LinkBlacklistDO blacklist = linkBlacklistMapper.selectById(id);
            if (blacklist != null) {
                blacklist.setStatus(1);
                blacklist.setUpdateTime(new Date());
                linkBlacklistMapper.updateById(blacklist);
                log.info("解封黑名单: {} - {}", id, blacklist.getDomain());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("解封失败: {}", id, e);
            return false;
        }
    }

    /**
     * 从URL中提取域名
     */
    private String extractDomain(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            URL netUrl = new URL(url);
            return netUrl.getHost();
        } catch (Exception e) {
            log.error("提取域名失败: {}", url, e);
            return null;
        }
    }
}
