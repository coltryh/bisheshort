package com.ryh.shortlink.allinone.dto.req;

import lombok.Data;

/**
 * 短链接分页查询请求DTO
 */
@Data
public class ShortLinkPageReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 排序字段
     */
    private String orderTag;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;
}
