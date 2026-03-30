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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * AI智能助手控制器
 * 调用project服务的AI功能
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/ai")
public class ShortLinkAiController {

    private final RestTemplate restTemplate;

    private static final String PROJECT_URL = "http://short-link-project";

    /**
     * 分析短链接访问统计
     */
    @PostMapping("/analyze")
    public Result<String> analyzeStats(@RequestBody String statsData) {
        String url = PROJECT_URL + "/api/short-link/ai/analyze";
        try {
            String result = restTemplate.postForObject(url, statsData, String.class);
            return Results.success(result);
        } catch (Exception e) {
            log.error("AI分析失败", e);
            Result<String> result = new Result<>();
            result.setCode("500");
            result.setMessage("AI服务调用失败");
            return result;
        }
    }

    /**
     * AI智能问答
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestParam String question, @RequestParam(required = false) String context) {
        String url = PROJECT_URL + "/api/short-link/ai/chat?question=" + question;
        if (context != null && !context.isEmpty()) {
            url += "&context=" + context;
        }
        try {
            String result = restTemplate.getForObject(url, String.class);
            return Results.success(result);
        } catch (Exception e) {
            log.error("AI问答失败", e);
            Result<String> result = new Result<>();
            result.setCode("500");
            result.setMessage("AI服务调用失败");
            return result;
        }
    }

    /**
     * 生成短链接描述建议
     */
    @PostMapping("/suggest")
    public Result<String> suggestDescription(@RequestParam String originUrl,
                                           @RequestParam(required = false) String statsData) {
        String url = PROJECT_URL + "/api/short-link/ai/suggest?originUrl=" + originUrl;
        if (statsData != null && !statsData.isEmpty()) {
            url += "&statsData=" + statsData;
        }
        try {
            String result = restTemplate.getForObject(url, String.class);
            return Results.success(result);
        } catch (Exception e) {
            log.error("AI建议失败", e);
            Result<String> result = new Result<>();
            result.setCode("500");
            result.setMessage("AI服务调用失败");
            return result;
        }
    }
}
