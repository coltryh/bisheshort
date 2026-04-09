package com.ryh.shortlink.allinone.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkCreateReqDTO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkPageReqDTO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkUpdateReqDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkBatchCreateRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkCreateRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

/**
 * 短链接服务接口
 */
public interface ShortLinkService {

    /**
     * 创建短链接
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 批量创建短链接
     */
    ShortLinkBatchCreateRespDTO batchCreateShortLink(List<String> originUrls, List<String> describes, String gid, Integer createdType);

    /**
     * 修改短链接
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 分页查询短链接
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 根据分组查询短链接列表
     */
    List<ShortLinkDO> listByGid(String gid);

    /**
     * 根据完整短链接查询
     */
    ShortLinkDO getByFullShortUrl(String fullShortUrl);

    /**
     * 根据ID查询
     */
    ShortLinkDO getById(Long id);

    /**
     * 根据用户名查询短链接列表
     */
    List<ShortLinkDO> listByUsername(String username);

    /**
     * 保存短链接
     */
    boolean save(ShortLinkDO shortLink);

    /**
     * 更新短链接
     */
    boolean update(ShortLinkDO shortLink);

    /**
     * 删除短链接到回收站
     */
    void deleteToRecycleBin(String gid, String fullShortUrl, String username);

    /**
     * 查询用户的回收站短链接
     */
    IPage<ShortLinkPageRespDTO> pageRecycleBinShortLink(List<String> gidList, Long current, Long size);

    /**
     * 恢复短链接
     */
    void recoverShortLink(String gid, String fullShortUrl, String username);

    /**
     * 根据ID恢复短链接
     */
    void recoverShortLinkById(Long id, String username);

    /**
     * 永久删除短链接
     */
    void removeShortLink(String gid, String fullShortUrl, String username);

    /**
     * 根据ID永久删除短链接
     */
    void removeShortLinkById(Long id, String username);

    /**
     * 根据用户名查询分组短链接数量
     */
    List<ShortLinkDO> listGroupShortLinkCount(List<String> gidList);

    /**
     * 跳转短链接
     */
    String restoreUrl(String shortUri);
}
