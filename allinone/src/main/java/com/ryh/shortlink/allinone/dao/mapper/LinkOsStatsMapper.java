package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.LinkOsStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 操作系统统计 Mapper
 */
@Mapper
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {

    /**
     * 根据短链接查询操作系统统计
     */
    @Select("SELECT * FROM t_link_os_stats WHERE full_short_url = #{fullShortUrl} AND date = #{date}")
    List<LinkOsStatsDO> selectByFullShortUrlAndDate(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("date") Date date
    );

    /**
     * 根据短链接查询所有操作系统统计
     */
    @Select("SELECT * FROM t_link_os_stats WHERE full_short_url = #{fullShortUrl} ORDER BY date DESC, cnt DESC")
    List<LinkOsStatsDO> selectByFullShortUrl(@Param("fullShortUrl") String fullShortUrl);
}
