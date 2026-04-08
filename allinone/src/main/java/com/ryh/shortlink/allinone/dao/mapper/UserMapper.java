package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
