package com.ryh.shortlink.allinone.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ryh.shortlink.allinone.service.IpLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 高德API实现的IP地理位置服务
 */
@Slf4j
@Service
public class IpLocationServiceImpl implements IpLocationService {

    @Value("${short-link.stats.locale.amap-key}")
    private String amapKey;

    private static final String AMAP_API_URL = "https://restapi.amap.com/v3/ip";

    /**
     * 缓存已查询过的IP，避免重复调用API
     */
    private final ConcurrentHashMap<String, String[]> locationCache = new ConcurrentHashMap<>();

    @Override
    public String[] getLocation(String ip) {
        // 先查缓存
        String[] cached = locationCache.get(ip);
        if (cached != null) {
            return cached;
        }

        try {
            String url = AMAP_API_URL + "?key=" + amapKey + "&ip=" + ip;
            HttpResponse response = HttpRequest.get(url)
                    .timeout(5000)
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSON.parseObject(response.body());
                String status = result.getString("status");
                if ("1".equals(status)) {
                    String province = result.getString("province");
                    String city = result.getString("city");
                    String country = result.getString("country");

                    // 处理可能为"[]"或"null"的情况
                    if (province == null || "[]".equals(province)) {
                        province = "未知";
                    }
                    if (city == null || "[]".equals(city)) {
                        city = "未知";
                    }
                    if (country == null || "[]".equals(country)) {
                        country = "中国";
                    }

                    String[] location = new String[]{country, province, city};
                    // 缓存结果，最多缓存10000条
                    if (locationCache.size() < 10000) {
                        locationCache.put(ip, location);
                    }
                    log.debug("IP定位成功: {} -> {}/{}/{}", ip, country, province, city);
                    return location;
                } else {
                    log.warn("高德API返回失败: status={}, info={}", status, result.getString("info"));
                }
            }
        } catch (Exception e) {
            log.error("IP定位异常: {}", ip, e);
        }

        // 失败时返回默认值
        return new String[]{"未知", "未知", "未知"};
    }
}
