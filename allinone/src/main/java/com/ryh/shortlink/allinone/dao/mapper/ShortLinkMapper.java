package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 短链接 Mapper
 */
@Mapper
public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {

    /**
     * 根据完整短链接查询
     */
    @Select("SELECT * FROM t_link WHERE full_short_url = #{fullShortUrl} AND del_flag = 0")
    ShortLinkDO selectByFullShortUrl(@Param("fullShortUrl") String fullShortUrl);

    /**
     * 统计回收站短链接数量
     */
    @Select("SELECT COUNT(*) FROM t_link WHERE gid = #{gid} AND del_flag = 1 AND del_time > 0")
    Long countRecycleBinByGid(@Param("gid") String gid);
}
