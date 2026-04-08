package com.ryh.shortlink.allinone.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.dao.entity.PermissionDO;
import com.ryh.shortlink.allinone.dao.entity.UserPermissionDO;
import com.ryh.shortlink.allinone.dao.mapper.PermissionMapper;
import com.ryh.shortlink.allinone.dao.mapper.UserPermissionMapper;
import com.ryh.shortlink.allinone.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final UserPermissionMapper userPermissionMapper;

    @Override
    public List<PermissionDO> listAllPermissions() {
        return permissionMapper.selectList(null);
    }

    @Override
    @Transactional
    public boolean assignPermissions(Long userId, List<Long> permissionIds) {
        // 删除用户现有权限
        LambdaQueryWrapper<UserPermissionDO> deleteWrapper = Wrappers.lambdaQuery(UserPermissionDO.class)
                .eq(UserPermissionDO::getUserId, userId);
        userPermissionMapper.delete(deleteWrapper);

        // 添加新权限
        for (Long permissionId : permissionIds) {
            UserPermissionDO userPermission = new UserPermissionDO();
            userPermission.setUserId(userId);
            userPermission.setPermissionId(permissionId);
            userPermissionMapper.insert(userPermission);
        }
        return true;
    }

    @Override
    public List<PermissionDO> getUserPermissions(Long userId) {
        LambdaQueryWrapper<UserPermissionDO> queryWrapper = Wrappers.lambdaQuery(UserPermissionDO.class)
                .eq(UserPermissionDO::getUserId, userId);
        List<UserPermissionDO> userPermissions = userPermissionMapper.selectList(queryWrapper);

        if (userPermissions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> permissionIds = userPermissions.stream()
                .map(UserPermissionDO::getPermissionId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<PermissionDO> permissionWrapper = Wrappers.lambdaQuery(PermissionDO.class)
                .in(PermissionDO::getId, permissionIds);
        return permissionMapper.selectList(permissionWrapper);
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        List<PermissionDO> permissions = getUserPermissions(userId);
        return permissions.stream().anyMatch(p -> p.getCode().equals(permissionCode));
    }
}
