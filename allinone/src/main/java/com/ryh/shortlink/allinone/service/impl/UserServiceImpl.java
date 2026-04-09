package com.ryh.shortlink.allinone.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ryh.shortlink.allinone.common.utils.JwtUtils;
import com.ryh.shortlink.allinone.dao.entity.GroupDO;
import com.ryh.shortlink.allinone.dao.entity.UserDO;
import com.ryh.shortlink.allinone.dao.mapper.GroupMapper;
import com.ryh.shortlink.allinone.dao.mapper.UserMapper;
import com.ryh.shortlink.allinone.dto.req.UserRegisterReqDTO;
import com.ryh.shortlink.allinone.dto.req.UserUpdateReqDTO;
import com.ryh.shortlink.allinone.dto.resp.UserLoginRespDTO;
import com.ryh.shortlink.allinone.dto.resp.UserRespDTO;
import com.ryh.shortlink.allinone.service.GroupService;
import com.ryh.shortlink.allinone.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final GroupService groupService;

    /**
     * 用户登录会话缓存（单机版不使用Redis，用内存替代）
     * Key: username, Value: Map<token, UserDO>
     */
    private static final Map<String, Map<String, String>> USER_LOGIN_CACHE = new ConcurrentHashMap<>();

    /**
     * 用户名注册标记缓存
     */
    private static final Map<String, Boolean> USERNAME_CACHE = new ConcurrentHashMap<>();

    /**
     * 最大分组数量
     */
    private static final int MAX_GROUP_NUM = 100;

    @Override
    public UserLoginRespDTO login(String username, String password) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getPassword, password)
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = userMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查是否已登录
        Map<String, String> userSessions = USER_LOGIN_CACHE.get(username);
        if (userSessions != null && !userSessions.isEmpty()) {
            String existingToken = userSessions.keySet().stream().findFirst().orElse(null);
            if (existingToken != null) {
                return UserLoginRespDTO.builder()
                        .token(existingToken)
                        .username(username)
                        .build();
            }
        }

        // 生成新token
        String token = JwtUtils.generateToken(userDO.getId(), userDO.getUsername(), userDO.getRole());

        // 缓存登录信息
        Map<String, String> sessionMap = new ConcurrentHashMap<>();
        sessionMap.put(token, BeanUtil.toJsonStr(userDO));
        USER_LOGIN_CACHE.put(username, sessionMap);

        return UserLoginRespDTO.builder()
                .token(token)
                .username(username)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterReqDTO requestParam) {
        // 检查用户名是否存在
        if (hasUsername(requestParam.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户
        UserDO userDO = UserDO.builder()
                .username(requestParam.getUsername())
                .password(requestParam.getPassword())
                .realName(requestParam.getRealName())
                .phone(requestParam.getPhone())
                .mail(requestParam.getMail())
                .role("user")
                .delFlag(0)
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        userMapper.insert(userDO);

        // 创建默认分组
        groupService.saveGroup(requestParam.getUsername(), "默认分组");

        // 缓存用户名
        USERNAME_CACHE.put(requestParam.getUsername(), true);

        log.info("用户注册成功: {}", requestParam.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateReqDTO requestParam) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = userMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new RuntimeException("用户不存在");
        }

        userDO.setRealName(requestParam.getRealName());
        userDO.setPhone(requestParam.getPhone());
        userDO.setMail(requestParam.getMail());
        if (requestParam.getRole() != null) {
            userDO.setRole(requestParam.getRole());
        }
        userDO.setUpdateTime(new Date());

        userMapper.updateById(userDO);
        log.info("用户信息更新成功: {}", requestParam.getUsername());
    }

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = userMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new RuntimeException("用户不存在");
        }
        return BeanUtil.toBean(userDO, UserRespDTO.class);
    }

    @Override
    public List<UserRespDTO> listAllUsers() {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getDelFlag, 0)
                .orderByDesc(UserDO::getCreateTime);
        List<UserDO> userDOList = userMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(userDOList, UserRespDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        UserDO userDO = userMapper.selectById(userId);
        if (userDO == null) {
            throw new RuntimeException("用户不存在");
        }
        userDO.setDelFlag(1);
        userDO.setDeletionTime(System.currentTimeMillis());
        userDO.setUpdateTime(new Date());
        userMapper.updateById(userDO);

        // 清除缓存
        USERNAME_CACHE.remove(userDO.getUsername());
        USER_LOGIN_CACHE.remove(userDO.getUsername());

        log.info("用户删除成功: {}", userDO.getUsername());
    }

    @Override
    public Boolean hasUsername(String username) {
        // 先检查缓存
        if (USERNAME_CACHE.containsKey(username)) {
            return true;
        }
        // 查询数据库
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0);
        boolean exists = userMapper.selectCount(queryWrapper) > 0;
        if (exists) {
            USERNAME_CACHE.put(username, true);
        }
        return exists;
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        Map<String, String> userSessions = USER_LOGIN_CACHE.get(username);
        return userSessions != null && userSessions.containsKey(token);
    }

    @Override
    public void logout(String username, String token) {
        if (checkLogin(username, token)) {
            USER_LOGIN_CACHE.remove(username);
            log.info("用户登出成功: {}", username);
            return;
        }
        throw new RuntimeException("Token不存在或用户未登录");
    }
}
