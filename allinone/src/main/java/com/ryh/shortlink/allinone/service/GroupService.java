package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dao.entity.GroupDO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkGroupSortReqDTO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkGroupUpdateReqDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * 分组服务接口
 */
public interface GroupService {

    /**
     * 保存分组
     */
    void saveGroup(String username, String groupName);

    /**
     * 查询用户的所有分组（带短链接数量）
     */
    List<ShortLinkGroupRespDTO> listGroup(String username);

    /**
     * 更新分组
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);

    /**
     * 删除分组
     */
    void deleteGroup(String gid, String username);

    /**
     * 排序分组
     */
    void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam, String username);

    /**
     * 根据用户名查询分组列表
     */
    List<GroupDO> listByUsername(String username);

    /**
     * 根据gid查询分组
     */
    GroupDO getByGid(String gid);
}
