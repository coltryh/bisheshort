package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.LinkAccessStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

/**
 * 访问统计 Mapper
 */
@Mapper
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    /**
     * 根据短链接查询日统计
     */
    @Select("SELECT * FROM t_link_access_stats WHERE full_short_url = #{fullShortUrl} AND date BETWEEN #{startDate} AND #{endDate} ORDER BY date DESC")
    List<LinkAccessStatsDO> selectDailyStatsByFullShortUrl(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    /**
     * 查询或创建今日统计记录
     */
    @Select("SELECT * FROM t_link_access_stats WHERE full_short_url = #{fullShortUrl} AND date = #{date} LIMIT 1")
    LinkAccessStatsDO selectByFullShortUrlAndDate(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("date") Date date
    );

    /**
     * 增加PV
     */
    @Update("UPDATE t_link_access_stats SET pv = pv + 1, update_time = NOW() WHERE full_short_url = #{fullShortUrl} AND date = #{date}")
    int incrementPv(@Param("fullShortUrl") String fullShortUrl, @Param("date") Date date);

    /**
     * 增加UV
     */
    @Update("UPDATE t_link_access_stats SET uv = uv + 1, update_time = NOW() WHERE full_short_url = #{fullShortUrl} AND date = #{date}")
    int incrementUv(@Param("fullShortUrl") String fullShortUrl, @Param("date") Date date);

    /**
     * 增加UIP
     */
    @Update("UPDATE t_link_access_stats SET uip = uip + 1, update_time = NOW() WHERE full_short_url = #{fullShortUrl} AND date = #{date}")
    int incrementUip(@Param("fullShortUrl") String fullShortUrl, @Param("date") Date date);

    /**
     * 根据短链接查询小时统计
     */
    @Select("SELECT hour, SUM(pv) as pv FROM t_link_access_stats WHERE full_short_url = #{fullShortUrl} AND date BETWEEN #{startDate} AND #{endDate} AND hour IS NOT NULL GROUP BY hour")
    List<LinkAccessStatsDO> selectHourStatsByFullShortUrl(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    /**
     * 根据短链接查询星期统计
     */
    @Select("SELECT weekday, SUM(pv) as pv FROM t_link_access_stats WHERE full_short_url = #{fullShortUrl} AND date BETWEEN #{startDate} AND #{endDate} AND weekday IS NOT NULL GROUP BY weekday")
    List<LinkAccessStatsDO> selectWeekdayStatsByFullShortUrl(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );
}
