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

package com.ryh.shortlink.admin.controller;

import com.ryh.shortlink.admin.common.convention.result.Result;
import com.ryh.shortlink.admin.common.convention.result.Results;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * AI智能助手控制器
 * 调用project服务的AI功能
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/v1/ai")
public class ShortLinkAiController {

    private final RestTemplate restTemplate;
    private final LoadBalancerClient loadBalancerClient;

    private static final String PROJECT_SERVICE_NAME = "short-link-project";

    /**
     * 获取目标服务地址
     */
    private String getProjectUrl() {
        ServiceInstance instance = loadBalancerClient.choose(PROJECT_SERVICE_NAME);
        if (instance == null) {
            throw new RuntimeException("无法找到服务: " + PROJECT_SERVICE_NAME);
        }
        return "http://" + instance.getHost() + ":" + instance.getPort();
    }

    /**
     * 分析短链接访问统计
     */
    @PostMapping("/analyze")
    public Result<String> analyzeStats(@RequestBody String statsData) {
        String url = getProjectUrl() + "/api/short-link/ai/analyze";
        try {
            String projectResponse = restTemplate.postForObject(url, statsData, String.class);
            com.alibaba.fastjson2.JSONObject jsonResult = com.alibaba.fastjson2.JSON.parseObject(projectResponse);
            String code = jsonResult.getString("code");
            String data = jsonResult.getString("data");
            String message = jsonResult.getString("message");

            Result<String> result = new Result<>();
            result.setCode(code);
            result.setData(data);
            result.setMessage(message);
            return result;
        } catch (Exception e) {
            log.error("AI分析失败", e);
            Result<String> result = new Result<>();
            result.setCode("500");
            result.setMessage("AI服务调用失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * AI智能问答
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, Object> request) {
        String url = getProjectUrl() + "/api/short-link/ai/chat";
        try {
            // 使用 String 接收响应，避免泛型擦除问题
            String projectResponse = restTemplate.postForObject(url, request, String.class);
            // 解析 project 返回的 JSON，获取 data 字段
            com.alibaba.fastjson2.JSONObject jsonResult = com.alibaba.fastjson2.JSON.parseObject(projectResponse);
            String code = jsonResult.getString("code");
            String data = jsonResult.getString("data");
            String message = jsonResult.getString("message");

            Result<String> result = new Result<>();
            result.setCode(code);
            result.setData(data);
            result.setMessage(message);
            return result;
        } catch (Exception e) {
            log.error("AI问答失败", e);
            Result<String> result = new Result<>();
            result.setCode("500");
            result.setMessage("AI服务调用失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 生成短链接描述建议
     */
    @PostMapping("/suggest")
    public Result<String> suggestDescription(@RequestBody Map<String, Object> request) {
        String url = getProjectUrl() + "/api/short-link/ai/suggest";
        try {
            String projectResponse = restTemplate.postForObject(url, request, String.class);
            com.alibaba.fastjson2.JSONObject jsonResult = com.alibaba.fastjson2.JSON.parseObject(projectResponse);
            String code = jsonResult.getString("code");
            String data = jsonResult.getString("data");
            String message = jsonResult.getString("message");

            Result<String> result = new Result<>();
            result.setCode(code);
            result.setData(data);
            result.setMessage(message);
            return result;
        } catch (Exception e) {
            log.error("AI建议失败", e);
            Result<String> result = new Result<>();
            result.setCode("500");
            result.setMessage("AI服务调用失败: " + e.getMessage());
            return result;
        }
    }
}
