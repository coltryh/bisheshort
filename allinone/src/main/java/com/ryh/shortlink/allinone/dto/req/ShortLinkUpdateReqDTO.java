package com.ryh.shortlink.allinone.dto.req;

import lombok.Data;

import java.util.Date;

/**
 * 短链接更新请求DTO
 */
@Data
public class ShortLinkUpdateReqDTO {

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原分组标识
     */
    private String originGid;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 有效期类型：0=永久有效，1=自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    private String describe;
}
