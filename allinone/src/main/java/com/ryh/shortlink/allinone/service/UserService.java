package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dao.entity.UserDO;
import com.ryh.shortlink.allinone.dto.req.UserRegisterReqDTO;
import com.ryh.shortlink.allinone.dto.req.UserUpdateReqDTO;
import com.ryh.shortlink.allinone.dto.resp.UserLoginRespDTO;
import com.ryh.shortlink.allinone.dto.resp.UserRespDTO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    UserLoginRespDTO login(String username, String password);

    /**
     * 用户注册
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 修改用户信息
     */
    void updateUser(UserUpdateReqDTO requestParam);

    /**
     * 根据用户名查询用户
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询所有用户
     */
    List<UserRespDTO> listAllUsers();

    /**
     * 删除用户
     */
    void deleteUser(Long userId);

    /**
     * 检查用户名是否存在
     */
    Boolean hasUsername(String username);

    /**
     * 检查用户是否登录
     */
    Boolean checkLogin(String username, String token);

    /**
     * 用户登出
     */
    void logout(String username, String token);
}
