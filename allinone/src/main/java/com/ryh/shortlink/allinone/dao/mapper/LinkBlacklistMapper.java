package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.LinkBlacklistDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 链接黑名单 Mapper
 */
@Mapper
public interface LinkBlacklistMapper extends BaseMapper<LinkBlacklistDO> {
}
