package com.ryh.shortlink.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MiniMax AI 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "minimax")
public class MiniMaxConfig {

    /**
     * API Key
     */
    private String apiKey;

    /**
     * Group ID
     */
    private String groupId;

    /**
     * API 基础地址
     */
    private String baseUrl = "https://api.minimax.chat/v";

    /**
     * 聊天模型
     */
    private String chatModel = "abab6.5s-chat";

    /**
     * Embedding 模型
     */
    private String embeddingModel = "embo-01";
}
