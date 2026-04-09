package com.ryh.shortlink.allinone.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ryh.shortlink.allinone.dao.entity.GroupDO;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkGotoDO;
import com.ryh.shortlink.allinone.dao.mapper.GroupMapper;
import com.ryh.shortlink.allinone.dao.mapper.ShortLinkGotoMapper;
import com.ryh.shortlink.allinone.dao.mapper.ShortLinkMapper;
import com.ryh.shortlink.allinone.dto.req.ShortLinkCreateReqDTO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkPageReqDTO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkUpdateReqDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkBaseInfoRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkBatchCreateRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkCreateRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkPageRespDTO;
import com.ryh.shortlink.allinone.service.GroupService;
import com.ryh.shortlink.allinone.service.ShortLinkService;
import com.ryh.shortlink.allinone.toolkit.HashUtil;
import com.ryh.shortlink.allinone.toolkit.LinkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 短链接服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl implements ShortLinkService {

    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final GroupMapper groupMapper;
    private final GroupService groupService;

    /**
     * 短链接域名
     */
    @Value("${short-link.domain.default}")
    private String defaultDomain;

    /**
     * 跳转链接缓存（单机版，使用内存缓存）
     */
    private static final java.util.Map<String, String> GOTO_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * 已删除但缓存还存在（用于快速判断）
     */
    private static final java.util.Map<String, String> DELETED_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        // 生成短链接后缀
        String shortUri = generateShortUri(requestParam.getOriginUrl());
        String fullShortUrl = StrBuilder.create(defaultDomain).append("/").append(shortUri).toString();

        // 获取网站图标
        String favicon = getFavicon(requestParam.getOriginUrl());

        // 构建短链接实体
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(defaultDomain)
                .shortUri(shortUri)
                .fullShortUrl(fullShortUrl)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType() != null ? requestParam.getCreatedType() : 1)
                .validDateType(requestParam.getValidDateType() != null ? requestParam.getValidDateType() : 0)
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .favicon(favicon)
                .enableStatus(0)
                .totalPv(0)
                .totalUv(0)
                .totalUip(0)
                .delFlag(0)
                .delTime(0L)
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        // 构建跳转记录
        ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();

        try {
            shortLinkMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(linkGotoDO);
        } catch (DuplicateKeyException ex) {
            throw new RuntimeException("短链接生成重复：" + fullShortUrl);
        }

        // 缓存跳转链接
        long cacheValidTime = LinkUtil.getLinkCacheValidTime(requestParam.getValidDate());
        if (cacheValidTime > 0) {
            GOTO_CACHE.put(fullShortUrl, requestParam.getOriginUrl());
        }

        log.info("创建短链接成功: {}", fullShortUrl);

        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://" + fullShortUrl)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShortLinkBatchCreateRespDTO batchCreateShortLink(List<String> originUrls, List<String> describes, String gid, Integer createdType) {
        List<ShortLinkBaseInfoRespDTO> result = new ArrayList<>();
        for (int i = 0; i < originUrls.size(); i++) {
            try {
                ShortLinkCreateReqDTO reqDTO = ShortLinkCreateReqDTO.builder()
                        .gid(gid)
                        .originUrl(originUrls.get(i))
                        .describe(describes != null && i < describes.size() ? describes.get(i) : "")
                        .createdType(createdType != null ? createdType : 1)
                        .validDateType(0)
                        .build();
                ShortLinkCreateRespDTO shortLink = createShortLink(reqDTO);
                ShortLinkBaseInfoRespDTO baseInfo = ShortLinkBaseInfoRespDTO.builder()
                        .fullShortUrl(shortLink.getFullShortUrl())
                        .originUrl(shortLink.getOriginUrl())
                        .describe(describes != null && i < describes.size() ? describes.get(i) : "")
                        .build();
                result.add(baseInfo);
            } catch (Exception ex) {
                log.error("批量创建短链接失败: {}", originUrls.get(i), ex);
            }
        }
        log.info("批量创建短链接完成: 成功{}条", result.size());
        return ShortLinkBatchCreateRespDTO.builder()
                .total(result.size())
                .baseLinkInfos(result)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        // 查询原短链接
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLink = shortLinkMapper.selectOne(queryWrapper);
        if (hasShortLink == null) {
            throw new RuntimeException("短链接记录不存在");
        }

        // 获取网站图标
        String favicon = getFavicon(requestParam.getOriginUrl());

        // 更新短链接
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                .eq(ShortLinkDO::getDelFlag, 0);

        ShortLinkDO updateDO = ShortLinkDO.builder()
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .favicon(favicon)
                .updateTime(new Date())
                .build();

        // 如果是永久有效，清除有效期
        if (requestParam.getValidDateType() != null && requestParam.getValidDateType() == 0) {
            updateDO.setValidDate(null);
        }

        shortLinkMapper.update(updateDO, updateWrapper);

        // 如果分组变更，需要更新跳转表
        if (!Objects.equals(hasShortLink.getGid(), requestParam.getGid())) {
            LambdaUpdateWrapper<ShortLinkGotoDO> gotoUpdateWrapper = Wrappers.lambdaUpdate(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, requestParam.getFullShortUrl());
            ShortLinkGotoDO gotoDO = ShortLinkGotoDO.builder()
                    .gid(requestParam.getGid())
                    .build();
            shortLinkGotoMapper.update(gotoDO, gotoUpdateWrapper);
        }

        // 更新缓存
        GOTO_CACHE.remove(requestParam.getFullShortUrl());
        log.info("更新短链接成功: {}", requestParam.getFullShortUrl());
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);

        Page<ShortLinkDO> page = new Page<>(requestParam.getCurrent() != null ? requestParam.getCurrent() : 1,
                requestParam.getSize() != null ? requestParam.getSize() : 10);
        Page<ShortLinkDO> resultPage = shortLinkMapper.selectPage(page, queryWrapper);

        return resultPage.convert(each -> {
            ShortLinkPageRespDTO respDTO = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            respDTO.setDomain("http://" + respDTO.getDomain());
            return respDTO;
        });
    }

    @Override
    public List<ShortLinkDO> listByGid(String gid) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        return shortLinkMapper.selectList(queryWrapper);
    }

    @Override
    public ShortLinkDO getByFullShortUrl(String fullShortUrl) {
        return shortLinkMapper.selectByFullShortUrl(fullShortUrl);
    }

    @Override
    public ShortLinkDO getById(Long id) {
        return shortLinkMapper.selectById(id);
    }

    @Override
    public List<ShortLinkDO> listByUsername(String username) {
        // 获取用户的分组
        List<GroupDO> groups = groupService.listByUsername(username);
        if (groups.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> gidList = groups.stream().map(GroupDO::getGid).toList();
        // 查询该用户的所有短链接
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(ShortLinkDO::getGid, gidList)
                .eq(ShortLinkDO::getDelFlag, 0);
        return shortLinkMapper.selectList(queryWrapper);
    }

    @Override
    public boolean save(ShortLinkDO shortLink) {
        return shortLinkMapper.insert(shortLink) > 0;
    }

    @Override
    public boolean update(ShortLinkDO shortLink) {
        return shortLinkMapper.updateById(shortLink) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteToRecycleBin(String gid, String fullShortUrl, String username) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0);

        ShortLinkDO updateDO = ShortLinkDO.builder()
                .delFlag(1)
                .delTime(System.currentTimeMillis())
                .updateTime(new Date())
                .build();

        shortLinkMapper.update(updateDO, updateWrapper);

        // 清除缓存
        GOTO_CACHE.remove(fullShortUrl);
        DELETED_CACHE.put(fullShortUrl, "-");

        log.info("删除短链接到回收站: {}", fullShortUrl);
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageRecycleBinShortLink(List<String> gidList, Long current, Long size) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(gidList != null && !gidList.isEmpty(), ShortLinkDO::getGid, gidList)
                .eq(ShortLinkDO::getDelFlag, 1)
                .gt(ShortLinkDO::getDelTime, 0L)
                .orderByDesc(ShortLinkDO::getDelTime);

        Page<ShortLinkDO> page = new Page<>(current != null ? current : 1, size != null ? size : 10);
        Page<ShortLinkDO> resultPage = shortLinkMapper.selectPage(page, queryWrapper);

        return resultPage.convert(each -> {
            ShortLinkPageRespDTO respDTO = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            respDTO.setDomain("http://" + respDTO.getDomain());
            return respDTO;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverShortLink(String gid, String fullShortUrl, String username) {
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 1);

        ShortLinkDO updateDO = ShortLinkDO.builder()
                .delFlag(0)
                .delTime(0L)
                .updateTime(new Date())
                .build();

        shortLinkMapper.update(updateDO, updateWrapper);

        // 恢复缓存
        ShortLinkDO shortLink = shortLinkMapper.selectByFullShortUrl(fullShortUrl);
        if (shortLink != null && (shortLink.getValidDate() == null || shortLink.getValidDate().after(new Date()))) {
            GOTO_CACHE.put(fullShortUrl, shortLink.getOriginUrl());
        }
        DELETED_CACHE.remove(fullShortUrl);

        log.info("恢复短链接: {}", fullShortUrl);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverShortLinkById(Long id, String username) {
        ShortLinkDO shortLink = shortLinkMapper.selectById(id);
        if (shortLink == null) {
            throw new RuntimeException("短链接不存在");
        }

        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getId, id)
                .eq(ShortLinkDO::getDelFlag, 1);

        ShortLinkDO updateDO = ShortLinkDO.builder()
                .delFlag(0)
                .delTime(0L)
                .updateTime(new Date())
                .build();

        shortLinkMapper.update(updateDO, updateWrapper);

        // 恢复缓存
        if (shortLink.getValidDate() == null || shortLink.getValidDate().after(new Date())) {
            GOTO_CACHE.put(shortLink.getFullShortUrl(), shortLink.getOriginUrl());
        }
        DELETED_CACHE.remove(shortLink.getFullShortUrl());

        log.info("根据ID恢复短链接: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeShortLink(String gid, String fullShortUrl, String username) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 1);
        shortLinkMapper.delete(queryWrapper);

        // 删除跳转记录
        LambdaQueryWrapper<ShortLinkGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
        shortLinkGotoMapper.delete(gotoQueryWrapper);

        // 清除缓存
        GOTO_CACHE.remove(fullShortUrl);
        DELETED_CACHE.remove(fullShortUrl);

        log.info("永久删除短链接: {}", fullShortUrl);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeShortLinkById(Long id, String username) {
        ShortLinkDO shortLink = shortLinkMapper.selectById(id);
        if (shortLink == null) {
            throw new RuntimeException("短链接不存在");
        }

        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getId, id)
                .eq(ShortLinkDO::getDelFlag, 1);
        shortLinkMapper.delete(queryWrapper);

        // 删除跳转记录
        LambdaQueryWrapper<ShortLinkGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getFullShortUrl, shortLink.getFullShortUrl());
        shortLinkGotoMapper.delete(gotoQueryWrapper);

        // 清除缓存
        GOTO_CACHE.remove(shortLink.getFullShortUrl());
        DELETED_CACHE.remove(shortLink.getFullShortUrl());

        log.info("根据ID永久删除短链接: id={}", id);
    }

    @Override
    public List<ShortLinkDO> listGroupShortLinkCount(List<String> gidList) {
        if (gidList == null || gidList.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .in(ShortLinkDO::getGid, gidList)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        return shortLinkMapper.selectList(queryWrapper);
    }

    @Override
    public String restoreUrl(String shortUri) {
        String fullShortUrl = defaultDomain + "/" + shortUri;

        // 先查缓存
        String cachedUrl = GOTO_CACHE.get(fullShortUrl);
        if (StrUtil.isNotBlank(cachedUrl)) {
            return cachedUrl;
        }

        // 检查是否已删除
        if (DELETED_CACHE.containsKey(fullShortUrl)) {
            return null;
        }

        // 查询数据库
        ShortLinkGotoDO linkGoto = shortLinkGotoMapper.selectByFullShortUrl(fullShortUrl);
        if (linkGoto == null) {
            DELETED_CACHE.put(fullShortUrl, "-");
            return null;
        }

        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, linkGoto.getGid())
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO shortLink = shortLinkMapper.selectOne(queryWrapper);

        if (shortLink == null) {
            DELETED_CACHE.put(fullShortUrl, "-");
            return null;
        }

        // 检查有效期
        if (shortLink.getValidDate() != null && shortLink.getValidDate().before(new Date())) {
            return null;
        }

        // 缓存并返回
        GOTO_CACHE.put(fullShortUrl, shortLink.getOriginUrl());
        return shortLink.getOriginUrl();
    }

    /**
     * 生成短链接后缀
     */
    private String generateShortUri(String originUrl) {
        int retryCount = 0;
        int maxRetries = 10;
        while (retryCount < maxRetries) {
            String urlWithRandom = originUrl + UUID.randomUUID().toString();
            String shortUri = HashUtil.hashToBase62(urlWithRandom);
            String fullShortUrl = defaultDomain + "/" + shortUri;

            // 检查是否已存在
            if (!GOTO_CACHE.containsKey(fullShortUrl) && !DELETED_CACHE.containsKey(fullShortUrl)) {
                ShortLinkDO existing = shortLinkMapper.selectByFullShortUrl(fullShortUrl);
                if (existing == null) {
                    return shortUri;
                }
            }
            retryCount++;
        }
        throw new RuntimeException("短链接生成频繁，请稍后再试");
    }

    /**
     * 获取网站图标
     */
    private String getFavicon(String url) {
        try {
            URL targetUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.connect();
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                Document document = Jsoup.connect(url).timeout(5000).get();
                Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
                if (faviconLink != null) {
                    return faviconLink.attr("abs:href");
                }
            }
        } catch (Exception e) {
            log.debug("获取网站图标失败: {}, 错误: {}", url, e.getMessage());
        }
        return null;
    }
}
