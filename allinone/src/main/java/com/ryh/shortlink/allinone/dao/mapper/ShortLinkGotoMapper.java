package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkGotoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 短链接跳转 Mapper
 */
@Mapper
public interface ShortLinkGotoMapper extends BaseMapper<ShortLinkGotoDO> {

    /**
     * 根据完整短链接查询
     */
    @Select("SELECT * FROM t_link_goto WHERE full_short_url = #{fullShortUrl} LIMIT 1")
    ShortLinkGotoDO selectByFullShortUrl(@Param("fullShortUrl") String fullShortUrl);
}
