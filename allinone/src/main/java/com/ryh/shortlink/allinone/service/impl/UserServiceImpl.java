package com.ryh.shortlink.allinone.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.dao.entity.UserDO;
import com.ryh.shortlink.allinone.dao.mapper.UserMapper;
import com.ryh.shortlink.allinone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public UserDO login(String username, String password) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getPassword, password)
                .eq(UserDO::getDelFlag, 0);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public UserDO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public List<UserDO> listAllUsers() {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getDelFlag, 0)
                .orderByDesc(UserDO::getCreateTime);
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public boolean register(String username, String password, String realName) {
        if (hasUsername(username)) {
            return false;
        }
        UserDO user = new UserDO();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setRole("user");
        user.setDelFlag(0);
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean updateUser(UserDO user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean deleteUser(Long userId) {
        UserDO user = new UserDO();
        user.setId(userId);
        user.setDelFlag(1);
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean hasUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0);
        return userMapper.selectCount(queryWrapper) > 0;
    }
}
