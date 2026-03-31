package com.ryh.shortlink.project.ai;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ryh.shortlink.project.config.MiniMaxConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MiniMax AI 客户端
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MiniMaxAiClient {

    private final MiniMaxConfig config;

    private static final String CHAT_API_PATH = "/text/chatcompletion_v2";
    private static final String EMBEDDING_API_PATH = "/text/embeddings";

    /**
     * 对话接口
     *
     * @param prompt       用户问题
     * @param systemPrompt 系统提示词（可选）
     * @return AI 回复
     */
    public String chat(String prompt, String systemPrompt) {
        if (StrUtil.isBlank(config.getApiKey())) {
            return "AI服务未配置API Key，请联系管理员配置MiniMax API Key";
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getChatModel());
            requestBody.put("stream", false);

            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();

            // 系统提示词
            if (StrUtil.isNotBlank(systemPrompt)) {
                Map<String, String> systemMessage = new HashMap<>();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
                messages.add(systemMessage);
            }

            // 用户消息
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            String url = config.getBaseUrl() + CHAT_API_PATH + "?GroupId=" + config.getGroupId();

            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(requestBody))
                    .timeout(config.getChatModel().length() * 100)
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSON.parseObject(response.body());
                JSONArray choices = result.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("messages");
                    return message.getString("content");
                }
                return "AI响应格式异常: " + result.toJSONString();
            } else {
                log.error("MiniMax AI调用失败: {}", response.body());
                return "AI服务调用失败: " + response.getStatus();
            }
        } catch (Exception e) {
            log.error("MiniMax AI调用异常", e);
            return "AI服务调用异常: " + e.getMessage();
        }
    }

    /**
     * 对话接口（默认系统提示词）
     */
    public String chat(String prompt) {
        return chat(prompt, getDefaultSystemPrompt());
    }

    /**
     * 获取 Embedding 向量
     *
     * @param text 文本
     * @return 向量列表
     */
    @SuppressWarnings("unchecked")
    public List<Double> getEmbedding(String text) {
        if (StrUtil.isBlank(config.getApiKey())) {
            // 返回 mock 数据
            return generateMockEmbedding();
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getEmbeddingModel());
            requestBody.put("texts", new String[]{text});

            HttpResponse response = HttpRequest.post(config.getBaseUrl() + EMBEDDING_API_PATH)
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(requestBody))
                    .timeout(30000)
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSON.parseObject(response.body());
                JSONArray data = result.getJSONArray("data");
                if (data != null && !data.isEmpty()) {
                    return data.getJSONObject(0).getList("embedding", Double.class);
                }
            }
            log.error("MiniMax Embedding调用失败: {}", response.body());
        } catch (Exception e) {
            log.error("MiniMax Embedding调用异常", e);
        }

        return generateMockEmbedding();
    }

    /**
     * 生成 Mock 向量（用于开发测试）
     */
    private List<Double> generateMockEmbedding() {
        List<Double> mock = new ArrayList<>();
        for (int i = 0; i < 768; i++) {
            mock.add(Math.random() * 2 - 1);
        }
        return mock;
    }

    /**
     * 默认系统提示词
     */
    private String getDefaultSystemPrompt() {
        return "你是一个短链接管理系统的AI助手，擅长回答用户关于平台使用的问题。" +
                "你的知识库包含了平台的使用指南、常见问题解答等功能文档。" +
                "请用简洁易懂的语言回答用户的问题。";
    }

    /**
     * 构建 RAG 问答的系统提示词
     */
    public String buildRagSystemPrompt(List<String> retrievedDocs) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个短链接管理系统的AI助手，擅长回答用户关于平台使用的问题。\n\n");
        prompt.append("参考知识库内容回答用户问题：\n");

        for (int i = 0; i < retrievedDocs.size(); i++) {
            prompt.append(String.format("[文档%d] %s\n\n", i + 1, retrievedDocs.get(i)));
        }

        prompt.append("请根据以上知识库内容，用专业但易懂的方式回答用户的问题。" +
                "如果知识库中没有相关信息，请说明并建议用户查看官方文档或联系客服。");
        return prompt.toString();
    }
}
