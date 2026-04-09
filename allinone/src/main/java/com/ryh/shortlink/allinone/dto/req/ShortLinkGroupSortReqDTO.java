package com.ryh.shortlink.allinone.dto.req;

import lombok.Data;

/**
 * 分组排序请求DTO
 */
@Data
public class ShortLinkGroupSortReqDTO {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 排序值
     */
    private Integer sortOrder;
}
