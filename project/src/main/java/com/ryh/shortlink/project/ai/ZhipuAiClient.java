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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智谱AI调用工具类
 * 文档: https://open.bigmodel.cn/dev/api
 */
@Slf4j
@Component
public class ZhipuAiClient {

    /**
     * 智谱AI API Key（需要去 https://open.bigmodel.cn/ 注册获取）
     * TODO: 替换成你的API Key
     */
    @Value("${ai.zhipu.api-key:your-api-key-here}")
    private String apiKey;

    /**
     * 模型名称
     */
    @Value("${ai.zhipu.chat-model:glm-4-flash}")
    private String model;

    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    /**
     * 调用AI进行对话
     *
     * @param prompt 用户问题
     * @return AI回复内容
     */
    public String chat(String prompt) {
        if (StrUtil.isBlank(apiKey) || apiKey.equals("your-api-key-here")) {
            return "AI服务未配置API Key，请联系管理员配置智谱AI API Key";
        }

        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个短链接管理系统的AI助手，擅长分析短链接的访问统计数据， " +
                    "回答用户关于短链接效果分析的问题。你需要用简洁易懂的语言解释数据含义， " +
                    "并给出合理的运营建议。");
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            // 发送请求
            HttpResponse response = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(requestBody))
                    .execute();

            // 解析响应
            if (response.isOk()) {
                JSONObject result = JSON.parseObject(response.body());
                if (result.containsKey("choices")) {
                    JSONArray choices = result.getJSONArray("choices");
                    if (choices != null && !choices.isEmpty()) {
                        JSONObject firstChoice = choices.getJSONObject(0);
                        JSONObject message = firstChoice.getJSONObject("message");
                        return message.getString("content");
                    }
                }
                // 返回错误信息
                return "AI响应格式异常: " + result.toJSONString();
            } else {
                log.error("智谱AI调用失败: {}", response.body());
                return "AI服务调用失败: " + response.getStatus();
            }
        } catch (Exception e) {
            log.error("智谱AI调用异常", e);
            return "AI服务调用异常: " + e.getMessage();
        }
    }

    /**
     * 分析短链接访问统计数据
     *
     * @param statsData 统计数据（JSON格式）
     * @return 分析结果
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
     *
     * @param originUrl 原始链接
     * @param statsData 访问数据（可选）
     * @return 建议
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
     *
     * @param question 用户问题
     * @param context 上下文信息（如短链接信息、统计数据等）
     * @return 回答
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
