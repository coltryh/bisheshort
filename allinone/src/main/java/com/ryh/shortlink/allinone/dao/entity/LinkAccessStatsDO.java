package com.ryh.shortlink.allinone.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接访问统计实体
 */
@Data
@TableName("t_link_access_stats")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkAccessStatsDO {

    /**
     * 统计ID
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 统计日期
     */
    private Date date;

    /**
     * 页面访问量
     */
    private Integer pv;

    /**
     * 独立访客数
     */
    private Integer uv;

    /**
     * 独立IP数
     */
    private Integer uip;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
