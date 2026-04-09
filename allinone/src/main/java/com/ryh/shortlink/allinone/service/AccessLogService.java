package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dao.entity.LinkAccessLogsDO;

import java.util.Date;
import java.util.List;

/**
 * 访问日志服务接口
 */
public interface AccessLogService {

    /**
     * 记录访问日志
     */
    void recordAccessLog(String fullShortUrl, String uv, String ip, String country, String province,
                        String city, String device, String os, String browser, String network);

    /**
     * 查询访问日志列表
     */
    List<LinkAccessLogsDO> listAccessLogs(String fullShortUrl, Date startTime, Date endTime);

    /**
     * 获取UV统计
     */
    Long getUvCount(String fullShortUrl, Date startTime, Date endTime);

    /**
     * 获取IP统计
     */
    Long getUipCount(String fullShortUrl, Date startTime, Date endTime);

    /**
     * 获取PV统计
     */
    Long getPvCount(String fullShortUrl, Date startTime, Date endTime);
}
