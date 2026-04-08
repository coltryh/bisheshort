package com.ryh.shortlink.allinone.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.dao.entity.GroupDO;
import com.ryh.shortlink.allinone.dao.mapper.GroupMapper;
import com.ryh.shortlink.allinone.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;

    @Override
    public List<GroupDO> listByUsername(String username) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0)
                .orderByAsc(GroupDO::getSortOrder);
        return groupMapper.selectList(queryWrapper);
    }

    @Override
    public List<GroupDO> listAll() {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .orderByDesc(GroupDO::getCreateTime);
        return groupMapper.selectList(queryWrapper);
    }

    @Override
    public GroupDO getByGid(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
        return groupMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean save(GroupDO group) {
        group.setDelFlag(0);
        group.setCreateTime(new Date());
        group.setUpdateTime(new Date());
        return groupMapper.insert(group) > 0;
    }

    @Override
    @Transactional
    public boolean update(GroupDO group) {
        group.setUpdateTime(new Date());
        return groupMapper.updateById(group) > 0;
    }

    @Override
    @Transactional
    public boolean delete(String gid, String username) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0);
        GroupDO group = groupMapper.selectOne(queryWrapper);
        if (group != null) {
            group.setDelFlag(1);
            return groupMapper.updateById(group) > 0;
        }
        return false;
    }
}
