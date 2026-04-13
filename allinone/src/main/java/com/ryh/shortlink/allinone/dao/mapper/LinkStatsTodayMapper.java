package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.LinkStatsTodayDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 今日统计 Mapper
 */
@Mapper
public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsTodayDO> {

    /**
     * 根据短链接查询今日统计
     */
    @Select("SELECT * FROM t_link_stats_today WHERE full_short_url = #{fullShortUrl} AND date = #{date} LIMIT 1")
    LinkStatsTodayDO selectByFullShortUrlAndDate(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("date") Date date
    );

    /**
     * 根据短链接列表查询今日统计
     */
    @Select("<script>" +
            "SELECT * FROM t_link_stats_today WHERE full_short_url IN " +
            "<foreach collection='fullShortUrls' item='url' open='(' separator=',' close=')'>" +
            "#{url}" +
            "</foreach>" +
            " AND date = #{date}" +
            "</script>")
    List<LinkStatsTodayDO> selectByFullShortUrlsAndDate(
            @Param("fullShortUrls") List<String> fullShortUrls,
            @Param("date") Date date
    );
}