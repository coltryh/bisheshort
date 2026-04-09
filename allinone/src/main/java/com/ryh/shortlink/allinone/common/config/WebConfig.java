package com.ryh.shortlink.allinone.common.config;

import com.ryh.shortlink.allinone.common.filter.CorsFilter;
import com.ryh.shortlink.allinone.common.filter.PerformanceFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Web配置
 */
@Configuration
@EnableAsync
public class WebConfig {

    /**
     * 注册CORS过滤器
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CorsFilter());
        registration.addUrlPatterns("/*");
        registration.setName("corsFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 注册性能监控过滤器
     */
    @Bean
    public FilterRegistrationBean<PerformanceFilter> performanceFilterRegistration() {
        FilterRegistrationBean<PerformanceFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PerformanceFilter());
        registration.addUrlPatterns("/*");
        registration.setName("performanceFilter");
        registration.setOrder(2);
        return registration;
    }
}
