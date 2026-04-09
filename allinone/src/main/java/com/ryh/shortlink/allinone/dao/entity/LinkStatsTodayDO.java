package com.ryh.shortlink.allinone.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 今日统计实体
 */
@Data
@TableName("t_link_stats_today")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkStatsTodayDO {

    /**
     * ID
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

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
     * 日期
     */
    private Date date;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
