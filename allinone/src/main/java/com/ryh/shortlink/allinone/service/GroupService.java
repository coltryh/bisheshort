package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dao.entity.GroupDO;

import java.util.List;

public interface GroupService {

    List<GroupDO> listByUsername(String username);

    List<GroupDO> listAll();

    GroupDO getByGid(String gid);

    boolean save(GroupDO group);

    boolean update(GroupDO group);

    boolean delete(String gid, String username);
}
