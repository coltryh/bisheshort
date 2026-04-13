package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.LinkAccessLogsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 访问日志 Mapper
 */
@Mapper
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {

    /**
     * 根据短链接和时间范围查询日志
     */
    @Select("SELECT * FROM t_link_access_logs WHERE full_short_url = #{fullShortUrl} AND create_time BETWEEN #{startTime} AND #{endTime} ORDER BY create_time DESC")
    List<LinkAccessLogsDO> selectByFullShortUrlAndTimeRange(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );

    /**
     * 统计UV数量
     */
    @Select("SELECT COUNT(DISTINCT uv) FROM t_link_access_logs WHERE full_short_url = #{fullShortUrl} AND create_time BETWEEN #{startTime} AND #{endTime}")
    Long countUniqueVisitors(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );

    /**
     * 统计IP数量
     */
    @Select("SELECT COUNT(DISTINCT ip) FROM t_link_access_logs WHERE full_short_url = #{fullShortUrl} AND create_time BETWEEN #{startTime} AND #{endTime}")
    Long countUniqueIps(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );
}
