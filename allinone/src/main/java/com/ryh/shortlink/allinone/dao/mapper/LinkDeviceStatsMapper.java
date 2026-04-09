package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.LinkDeviceStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 设备统计 Mapper
 */
@Mapper
public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {

    /**
     * 根据短链接查询设备统计
     */
    @Select("SELECT * FROM t_link_device_stats WHERE full_short_url = #{fullShortUrl} AND date = #{date}")
    List<LinkDeviceStatsDO> selectByFullShortUrlAndDate(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("date") Date date
    );

    /**
     * 根据短链接查询所有设备统计
     */
    @Select("SELECT * FROM t_link_device_stats WHERE full_short_url = #{fullShortUrl} ORDER BY date DESC, cnt DESC")
    List<LinkDeviceStatsDO> selectByFullShortUrl(@Param("fullShortUrl") String fullShortUrl);
}
