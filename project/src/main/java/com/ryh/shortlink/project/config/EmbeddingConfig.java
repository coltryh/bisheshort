package com.ryh.shortlink.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Embedding 服务配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "embedding")
public class EmbeddingConfig {

    /**
     * Embedding 服务地址
     */
    private String baseUrl = "http://localhost:5000";

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 30000;

    /**
     * 检索结果数量
     */
    private int searchTopK = 5;
}
