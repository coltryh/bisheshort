package com.ryh.shortlink.allinone.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 短链接日访问统计响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsAccessDailyRespDTO {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 日期
     */
    private String date;

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

    /**
     * 设置日期（接收Date对象并格式化为字符串）
     */
    public void setDate(Date date) {
        if (date != null) {
            this.date = DATE_FORMAT.format(date);
        } else {
            this.date = null;
        }
    }
}
