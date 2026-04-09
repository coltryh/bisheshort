package com.ryh.shortlink.allinone.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接日访问统计响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsAccessDailyRespDTO {

    /**
     * 日期
     */
    private Date date;

    /**
     * PV
     */
    private Integer pv;

    /**
     * UV
     */
    private Integer uv;

    /**
     * UIP
     */
    private Integer uip;
}
