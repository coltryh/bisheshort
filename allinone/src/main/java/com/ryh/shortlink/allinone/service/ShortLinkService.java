package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;

import java.util.List;

public interface ShortLinkService {

    List<ShortLinkDO> listByGid(String gid);

    List<ShortLinkDO> listByUsername(String username);

    ShortLinkDO getById(Long id);

    ShortLinkDO getByFullShortUrl(String fullShortUrl);

    boolean save(ShortLinkDO shortLink);

    boolean update(ShortLinkDO shortLink);

    boolean delete(Long id);

    boolean incrementStats(Long id, int pv, int uv, int uip);

    // 回收站相关
    List<ShortLinkDO> listRecycleBin(String username);

    boolean recover(Long id);

    boolean permanentDelete(Long id);
}
