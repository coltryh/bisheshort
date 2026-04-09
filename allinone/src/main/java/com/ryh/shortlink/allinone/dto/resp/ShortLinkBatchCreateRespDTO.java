package com.ryh.shortlink.allinone.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 短链接批量创建响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkBatchCreateRespDTO {

    /**
     * 成功创建数量
     */
    private Integer total;

    /**
     * 基础链接信息列表
     */
    private List<ShortLinkBaseInfoRespDTO> baseLinkInfos;
}
