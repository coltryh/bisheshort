/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    /**
     * 对话接口
     *
     * @param prompt       用户问题
     * @param systemPrompt 系统提示词（可选）
     * @return AI 回复
     */
    public String chat(String prompt, String systemPrompt) {
        log.info("MiniMax配置 - apiKey: {}, groupId: {}, baseUrl: {}, chatModel: {}",
                config.getApiKey(), config.getGroupId(), config.getBaseUrl(), config.getChatModel());

        if (StrUtil.isBlank(config.getApiKey())) {
            log.warn("MiniMax API Key 未配置");
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
            log.info("调用MiniMax API URL: {}", url);
            log.info("请求体: {}", JSON.toJSONString(requestBody));

            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(requestBody))
                    .timeout(30000)
                    .setFollowRedirects(false)  // 不跟随重定向
                    .execute();

            log.info("MiniMax API响应状态: {}", response.getStatus());

            log.info("MiniMax API响应状态: {}, body: {}", response.getStatus(), response.body());

            if (response.isOk()) {
                JSONObject result = JSON.parseObject(response.body());
                log.info("MiniMax API响应内容: {}", result.toJSONString());

                // 检查是否有错误码
                if (result.containsKey("base_resp")) {
                    JSONObject baseResp = result.getJSONObject("base_resp");
                    Integer errCode = baseResp.getInteger("status_code");
                    if (errCode != null && errCode != 0) {
                        String errMsg = baseResp.getString("status_msg");
                        log.error("MiniMax API返回错误: errCode={}, errMsg={}", errCode, errMsg);
                        return "AI服务调用失败: " + errMsg;
                    }
                }

                JSONArray choices = result.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    log.info("firstChoice: {}", firstChoice.toJSONString());

                    // 尝试获取 messages（可能是 JSONArray）
                    JSONArray messagesArray = firstChoice.getJSONArray("messages");
                    if (messagesArray != null && !messagesArray.isEmpty()) {
                        JSONObject msgObj = messagesArray.getJSONObject(0);
                        String content = msgObj.getString("content");
                        log.info("AI回复内容: {}", content);
                        return content;
                    }

                    // 尝试获取 message（可能是单个对象）
                    JSONObject messageObj = firstChoice.getJSONObject("message");
                    if (messageObj != null) {
                        String content = messageObj.getString("content");
                        log.info("AI回复内容: {}", content);
                        return content;
                    }

                    log.warn("MiniMax API响应格式异常，未找到messages或message字段");
                    return "AI响应格式异常: " + firstChoice.toJSONString();
                }
                log.warn("MiniMax API响应格式异常，choices为空或不存在");
                return "AI响应格式异常: " + result.toJSONString();
            } else {
                log.error("MiniMax AI调用失败: HTTP状态={}, body={}", response.getStatus(), response.body());
                return "AI服务调用失败: HTTP " + response.getStatus() + ", " + response.body();
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
     * 默认系统提示词
     */
    private String getDefaultSystemPrompt() {
        return "你是一个短链接管理系统的AI助手，擅长分析短链接的访问统计数据，" +
                "回答用户关于短链接效果分析的问题。请用简洁易懂的语言解释数据含义，" +
                "并给出合理的运营建议。";
    }

    /**
     * 分析短链接访问统计数据
     */
    public String analyzeStats(String statsData) {
        String prompt = "请分析以下短链接的访问统计数据，给出专业的运营分析建议：\n\n" +
                "统计数据：" + statsData + "\n\n" +
                "请从以下几个角度进行分析：\n" +
                "1. 访问量整体情况\n" +
                "2. 用户活跃度\n" +
                "3. 可能的优化建议";

        return chat(prompt);
    }

    /**
     * 生成短链接描述和标题建议
     */
    public String suggestDescription(String originUrl, String statsData) {
        String prompt = "请为以下短链接生成一个简洁的描述和标题建议：\n\n" +
                "原始链接：" + originUrl + "\n";

        if (statsData != null && !statsData.isEmpty()) {
            prompt += "访问数据：" + statsData + "\n";
        }

        prompt += "\n请给出3-5个适合的描述选项，并说明推荐理由。";

        return chat(prompt);
    }

    /**
     * 回答用户关于短链接的任何问题
     */
    public String answer(String question, String context) {
        String prompt = "用户问题：" + question + "\n\n";

        if (context != null && !context.isEmpty()) {
            prompt += "相关上下文信息：\n" + context + "\n\n";
        }

        prompt += "请用专业但易懂的方式回答用户的问题。";

        return chat(prompt);
    }
}
