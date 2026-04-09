package com.ryh.shortlink.allinone.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_link")
public class ShortLinkDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String domain;

    private String shortUri;

    private String fullShortUrl;

    private String originUrl;

    private Integer clickNum;

    private String gid;

    private String favicon;

    private Integer enableStatus;

    private Integer createdType;

    private Integer validDateType;

    private Date validDate;

    @TableField("`describe`")
    private String describe;

    private Integer totalPv;

    private Integer totalUv;

    private Integer totalUip;

    private Date createTime;

    private Date updateTime;

    private Long delTime;

    @TableLogic
    private Integer delFlag;
}
