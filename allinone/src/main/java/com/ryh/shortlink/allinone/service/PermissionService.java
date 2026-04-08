package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dao.entity.PermissionDO;

import java.util.List;

public interface PermissionService {

    List<PermissionDO> listAllPermissions();

    boolean assignPermissions(Long userId, List<Long> permissionIds);

    List<PermissionDO> getUserPermissions(Long userId);

    boolean hasPermission(Long userId, String permissionCode);
}
