package com.ryh.shortlink.allinone.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.dao.entity.GroupDO;
import com.ryh.shortlink.allinone.dao.entity.GroupUniqueDO;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.dao.mapper.GroupMapper;
import com.ryh.shortlink.allinone.dao.mapper.GroupUniqueMapper;
import com.ryh.shortlink.allinone.dao.mapper.ShortLinkMapper;
import com.ryh.shortlink.allinone.dto.req.ShortLinkGroupSortReqDTO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkGroupUpdateReqDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkGroupRespDTO;
import com.ryh.shortlink.allinone.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 分组服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;
    private final GroupUniqueMapper groupUniqueMapper;
    private final ShortLinkMapper shortLinkMapper;

    /**
     * 最大分组数量
     */
    @Value("${short-link.group.max-num:100}")
    private Integer groupMaxNum;

    /**
     * GID注册标记缓存（单机版）
     */
    private static final List<String> GID_CACHE = new ArrayList<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGroup(String username, String groupName) {
        // 检查分组数量限制
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> existingGroups = groupMapper.selectList(queryWrapper);
        if (CollUtil.isNotEmpty(existingGroups) && existingGroups.size() >= groupMaxNum) {
            throw new RuntimeException("已超出最大分组数：" + groupMaxNum);
        }

        // 生成唯一的gid
        String gid = generateUniqueGid();
        if (StrUtil.isEmpty(gid)) {
            throw new RuntimeException("生成分组标识频繁，请稍后再试");
        }

        // 保存分组
        GroupDO groupDO = GroupDO.builder()
                .gid(gid)
                .name(groupName)
                .username(username)
                .sortOrder(0)
                .delFlag(0)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        groupMapper.insert(groupDO);

        // 缓存gid
        GID_CACHE.add(gid);

        log.info("创建分组成功: gid={}, name={}, username={}", gid, groupName, username);
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup(String username) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, username)
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);

        // 查询每个分组的短链接数量
        List<ShortLinkGroupRespDTO> result = new ArrayList<>();
        for (GroupDO groupDO : groupDOList) {
            // 查询该分组的短链接数量（只统计未删除的）
            LambdaQueryWrapper<ShortLinkDO> linkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, groupDO.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            Long count = shortLinkMapper.selectCount(linkQueryWrapper);

            ShortLinkGroupRespDTO respDTO = ShortLinkGroupRespDTO.builder()
                    .gid(groupDO.getGid())
                    .name(groupDO.getName())
                    .sortOrder(groupDO.getSortOrder())
                    .shortLinkCount(count.intValue())
                    .build();
            result.add(respDTO);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0);

        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        groupDO.setUpdateTime(new Date());
        groupMapper.update(groupDO, updateWrapper);

        log.info("更新分组成功: gid={}, name={}", requestParam.getGid(), requestParam.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(String gid, String username) {
        // 检查该分组下是否有短链接
        LambdaQueryWrapper<ShortLinkDO> linkQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getDelFlag, 0);
        Long count = shortLinkMapper.selectCount(linkQueryWrapper);
        if (count > 0) {
            throw new RuntimeException("该分组下存在短链接，请先删除或移动短链接");
        }

        // 软删除分组
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        groupDO.setUpdateTime(new Date());
        groupMapper.update(groupDO, updateWrapper);

        log.info("删除分组成功: gid={}, username={}", gid, username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam, String username) {
        for (ShortLinkGroupSortReqDTO sortReq : requestParam) {
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getGid, sortReq.getGid())
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0);
            GroupDO groupDO = new GroupDO();
            groupDO.setSortOrder(sortReq.getSortOrder());
            groupDO.setUpdateTime(new Date());
            groupMapper.update(groupDO, updateWrapper);
        }
        log.info("分组排序成功: username={}", username);
    }

    @Override
    public List<GroupDO> listByUsername(String username) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0)
                .orderByAsc(GroupDO::getSortOrder);
        return groupMapper.selectList(queryWrapper);
    }

    @Override
    public GroupDO getByGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        return groupMapper.selectOne(queryWrapper);
    }

    /**
     * 生成唯一的分组标识
     */
    private String generateUniqueGid() {
        int maxRetries = 10;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            String gid = RandomUtil.randomString(6);
            if (!GID_CACHE.contains(gid)) {
                // 尝试插入唯一标识表
                GroupUniqueDO uniqueDO = GroupUniqueDO.builder().gid(gid).build();
                try {
                    groupUniqueMapper.insert(uniqueDO);
                    return gid;
                } catch (DuplicateKeyException e) {
                    // gid已存在，重试
                }
            }
            retryCount++;
        }
        return null;
    }
}
