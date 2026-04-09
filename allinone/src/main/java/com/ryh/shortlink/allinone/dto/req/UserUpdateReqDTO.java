package com.ryh.shortlink.allinone.dto.req;

import lombok.Data;

/**
 * 用户更新请求DTO
 */
@Data
public class UserUpdateReqDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 角色
     */
    private String role;
}
