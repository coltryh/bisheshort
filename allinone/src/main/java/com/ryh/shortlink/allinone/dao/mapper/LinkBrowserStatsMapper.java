package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.LinkBrowserStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 浏览器统计 Mapper
 */
@Mapper
public interface LinkBrowserStatsMapper extends BaseMapper<LinkBrowserStatsDO> {

    /**
     * 根据短链接查询浏览器统计
     */
    @Select("SELECT * FROM t_link_browser_stats WHERE full_short_url = #{fullShortUrl} AND date = #{date}")
    List<LinkBrowserStatsDO> selectByFullShortUrlAndDate(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("date") Date date
    );

    /**
     * 根据短链接查询所有浏览器统计
     */
    @Select("SELECT * FROM t_link_browser_stats WHERE full_short_url = #{fullShortUrl} ORDER BY date DESC, cnt DESC")
    List<LinkBrowserStatsDO> selectByFullShortUrl(@Param("fullShortUrl") String fullShortUrl);
}
