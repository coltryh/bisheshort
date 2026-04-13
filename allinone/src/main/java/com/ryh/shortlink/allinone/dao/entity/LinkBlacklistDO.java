package com.ryh.shortlink.allinone.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@TableName("t_link_blacklist")
public class LinkBlacklistDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 被封禁的域名或URL
     */
    private String domain;

    /**
     * 封禁类型: domain=域名, url=完整URL, keyword=关键词
     */
    private String type;

    /**
     * 封禁原因
     */
    private String reason;

    /**
     * 封禁状态: 0=有效, 1=已解封
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
