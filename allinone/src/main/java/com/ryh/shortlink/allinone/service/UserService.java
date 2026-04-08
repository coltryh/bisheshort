package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dao.entity.UserDO;

import java.util.List;

public interface UserService {

    UserDO login(String username, String password);

    UserDO getUserByUsername(String username);

    List<UserDO> listAllUsers();

    boolean register(String username, String password, String realName);

    boolean updateUser(UserDO user);

    boolean deleteUser(Long userId);

    boolean hasUsername(String username);
}
