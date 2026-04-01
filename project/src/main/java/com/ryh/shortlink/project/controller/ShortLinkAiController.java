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

package com.ryh.shortlink.project.controller;

import com.ryh.shortlink.project.ai.MiniMaxAiClient;
import com.ryh.shortlink.project.common.convention.result.Result;
import com.ryh.shortlink.project.common.convention.result.Results;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI智能助手控制器
 * 提供短链接访问分析的智能问答功能
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/ai")
public class ShortLinkAiController {

    private final MiniMaxAiClient miniMaxAiClient;

    /**
     * 分析短链接访问统计
     * 根据传入的统计数据，AI自动给出分析和建议
     */
    @PostMapping("/analyze")
    public Result<String> analyzeStats(@RequestBody String statsData) {
        log.info("AI分析短链接统计: {}", statsData);
        String analysis = miniMaxAiClient.analyzeStats(statsData);
        return Results.success(analysis);
    }

    /**
     * 生成短链接描述建议
     * 根据原始链接和访问数据，AI推荐合适的描述
     */
    @PostMapping("/suggest")
    public Result<String> suggestDescription(@RequestBody Map<String, Object> request) {
        String originUrl = (String) request.get("originUrl");
        String statsData = request.get("statsData") != null ? request.get("statsData").toString() : null;
        log.info("AI生成描述建议: {}", originUrl);
        String suggestion = miniMaxAiClient.suggestDescription(originUrl, statsData);
        return Results.success(suggestion);
    }

    /**
     * AI智能问答
     * 用户可以向AI提问关于短链接的任何问题
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, Object> request) {
        String question = (String) request.get("question");
        String context = request.get("context") != null ? request.get("context").toString() : null;
        log.info("AI问答: {}", question);
        String answer = miniMaxAiClient.answer(question, context);
        return Results.success(answer);
    }

    /**
     * 批量分析多个短链接
     */
    @PostMapping("/batch-analyze")
    public Result<String> batchAnalyze(@RequestBody String shortLinksData) {
        log.info("AI批量分析短链接");
        String prompt = "请分析以下多个短链接的访问情况，并给出对比分析：\n\n" + shortLinksData;
        String analysis = miniMaxAiClient.chat(prompt);
        return Results.success(analysis);
    }
}