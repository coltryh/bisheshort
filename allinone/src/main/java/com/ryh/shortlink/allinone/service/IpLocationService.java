package com.ryh.shortlink.allinone.service;

/**
 * IP地理位置服务接口
 */
public interface IpLocationService {

    /**
     * 根据IP地址获取地理位置
     * @param ip IP地址
     * @return String[] {国家, 省份, 城市}
     */
    String[] getLocation(String ip);
}
