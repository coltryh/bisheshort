package com.ryh.shortlink.allinone.common.config;

import com.ryh.shortlink.allinone.common.filter.CorsFilter;
import com.ryh.shortlink.allinone.common.filter.PerformanceFilter;
import com.ryh.shortlink.allinone.common.interceptor.RoleInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 */
@Configuration
@EnableAsync
public class WebConfig implements WebMvcConfigurer {

    private final RoleInterceptor roleInterceptor;

    public WebConfig(RoleInterceptor roleInterceptor) {
        this.roleInterceptor = roleInterceptor;
    }

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/api/**");
    }
}
