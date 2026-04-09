package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.LinkLocaleStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 地区统计 Mapper
 */
@Mapper
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {

    /**
     * 根据短链接查询地区统计
     */
    @Select("SELECT * FROM t_link_locale_stats WHERE full_short_url = #{fullShortUrl} AND date = #{date}")
    List<LinkLocaleStatsDO> selectByFullShortUrlAndDate(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("date") Date date
    );

    /**
     * 根据短链接查询所有地区统计
     */
    @Select("SELECT * FROM t_link_locale_stats WHERE full_short_url = #{fullShortUrl} ORDER BY date DESC, cnt DESC")
    List<LinkLocaleStatsDO> selectByFullShortUrl(@Param("fullShortUrl") String fullShortUrl);
}
