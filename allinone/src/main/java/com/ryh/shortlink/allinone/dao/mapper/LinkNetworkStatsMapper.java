package com.ryh.shortlink.allinone.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.allinone.dao.entity.LinkNetworkStatsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 网络类型统计 Mapper
 */
@Mapper
public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {

    /**
     * 根据短链接查询网络类型统计
     */
    @Select("SELECT * FROM t_link_network_stats WHERE full_short_url = #{fullShortUrl} AND date = #{date}")
    List<LinkNetworkStatsDO> selectByFullShortUrlAndDate(
            @Param("fullShortUrl") String fullShortUrl,
            @Param("date") Date date
    );

    /**
     * 根据短链接查询所有网络类型统计
