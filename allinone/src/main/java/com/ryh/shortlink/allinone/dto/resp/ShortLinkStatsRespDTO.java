package com.ryh.shortlink.allinone.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接统计概览响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsRespDTO {

    /**
     * 总PV
     */
    private Integer totalPv;

    /**
     * 总UV
     */
    private Integer totalUv;

    /**
     * 总UIP
     */
    private Integer totalUip;

    /**
     * 今日PV
     */
    private Integer todayPv;

    /**
     * 今日UV
     */
    private Integer todayUv;

    /**
     * 今日UIP
     */
    private Integer todayUip;

    /**
     * 短链接总数
     */
    private Integer totalShortLinks;

    /**
     * 分组数量
     */
    private Integer totalGroups;
}
