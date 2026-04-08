package com.ryh.shortlink.allinone;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 短链接管理系统 - 单机版
 * 简化版，去掉微服务架构
 */
@SpringBootApplication
@MapperScan("com.ryh.shortlink.allinone.dao.mapper")
@ServletComponentScan
public class ShortLinkAllInOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAllInOneApplication.class, args);
    }
}
