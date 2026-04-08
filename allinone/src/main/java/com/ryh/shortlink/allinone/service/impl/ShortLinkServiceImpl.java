package com.ryh.shortlink.allinone.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.dao.mapper.ShortLinkMapper;
import com.ryh.shortlink.allinone.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl implements ShortLinkService {

    private final ShortLinkMapper shortLinkMapper;

    @Override
    public List<ShortLinkDO> listByGid(String gid) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        return shortLinkMapper.selectList(queryWrapper);
    }

    @Override
    public List<ShortLinkDO> listByUsername(String username) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        return shortLinkMapper.selectList(queryWrapper);
    }

    @Override
    public ShortLinkDO getById(Long id) {
        return shortLinkMapper.selectById(id);
    }

    @Override
    public ShortLinkDO getByFullShortUrl(String fullShortUrl) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getDelFlag, 0);
        return shortLinkMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean save(ShortLinkDO shortLink) {
        shortLink.setDelFlag(0);
        shortLink.setCreateTime(new Date());
        shortLink.setUpdateTime(new Date());
        shortLink.setClickNum(0);
        shortLink.setTotalPv(0);
        shortLink.setTotalUv(0);
        shortLink.setTotalUip(0);
        return shortLinkMapper.insert(shortLink) > 0;
    }

    @Override
    @Transactional
    public boolean update(ShortLinkDO shortLink) {
        shortLink.setUpdateTime(new Date());
        return shortLinkMapper.updateById(shortLink) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        ShortLinkDO shortLink = shortLinkMapper.selectById(id);
        if (shortLink != null) {
            shortLink.setDelFlag(1);
            shortLink.setDelTime(System.currentTimeMillis());
            return shortLinkMapper.updateById(shortLink) > 0;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean incrementStats(Long id, int pv, int uv, int uip) {
        ShortLinkDO shortLink = shortLinkMapper.selectById(id);
        if (shortLink != null) {
            shortLink.setTotalPv(shortLink.getTotalPv() + pv);
            shortLink.setTotalUv(shortLink.getTotalUv() + uv);
            shortLink.setTotalUip(shortLink.getTotalUip() + uip);
            shortLink.setClickNum(shortLink.getClickNum() + 1);
            return shortLinkMapper.updateById(shortLink) > 0;
        }
        return false;
    }

    @Override
    public List<ShortLinkDO> listRecycleBin(String username) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getDelFlag, 1)
                .orderByDesc(ShortLinkDO::getDelTime);
        return shortLinkMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public boolean recover(Long id) {
        ShortLinkDO shortLink = shortLinkMapper.selectById(id);
        if (shortLink != null) {
            shortLink.setDelFlag(0);
            shortLink.setDelTime(null);
            return shortLinkMapper.updateById(shortLink) > 0;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean permanentDelete(Long id) {
        return shortLinkMapper.deleteById(id) > 0;
    }
}
