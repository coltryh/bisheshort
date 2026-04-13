package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dao.entity.LinkBlacklistDO;

import java.util.List;

public interface LinkBlacklistService {

    /**
     * 检查URL是否在黑名单中
     * @param url 要检查的URL
     * @return true=在黑名单中, false=不在黑名单中
     */
    boolean isBlacklisted(String url);

    /**
     * 获取所有有效的黑名单记录
     */
    List<LinkBlacklistDO> listAllActive();

    /**
     * 添加到黑名单
     */
    boolean addToBlacklist(String domain, String type, String reason);

    /**
     * 从黑名单移除
     */
    boolean removeFromBlacklist(Long id);

    /**
     * 解封域名/URL
     */
    boolean unblock(Long id);
}
