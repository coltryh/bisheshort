package com.ryh.shortlink.allinone.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_user")
public class UserDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String mail;

    private String role;

    private Long deletionTime;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer delFlag;
}
