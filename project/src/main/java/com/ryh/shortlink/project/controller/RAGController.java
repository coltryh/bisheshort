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

import com.ryh.shortlink.project.common.convention.result.Result;
import com.ryh.shortlink.project.common.convention.result.Results;
import com.ryh.shortlink.project.service.EmbeddingService;
import com.ryh.shortlink.project.service.RAGService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * RAG 知识库问答接口
 */
@RestController
@RequestMapping("/api/short-link/v1/rag")
@RequiredArgsConstructor
public class RAGController {

    private final RAGService ragService;
    private final EmbeddingService embeddingService;

    /**
     * RAG 问答
     */
    @PostMapping("/ask")
    public Result<RAGService.RAGResult> ask(@RequestBody Map<String, Object> request) {
        String question = (String) request.get("question");
        Long userId = getCurrentUserId(request);

        if (question == null || question.trim().isEmpty()) {
            return new Result<RAGService.RAGResult>().setCode("0").setMessage("问题不能为空");
        }

        RAGService.RAGResult result = ragService.answer(question.trim(), userId);
        return Results.success(result);
    }

    /**
     * 获取知识库文档列表
     */
    @GetMapping("/documents")
    public Result<List> listDocuments() {
        return Results.success(ragService.listDocuments());
    }

    /**
     * 添加知识库文档
     */
    @PostMapping("/documents")
    public Result<Boolean> addDocument(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String content = (String) request.get("content");
        Long userId = getCurrentUserId(request);

        if (title == null || content == null) {
            return new Result<Boolean>().setCode("0").setMessage("标题和内容不能为空");
        }

        boolean success = ragService.addDocument(title, content, userId);
        if (success) {
            return Results.success(true);
        }
        return new Result<Boolean>().setCode("0").setMessage("添加失败");
    }

    /**
     * 同步知识库到 Chroma
     */
    @PostMapping("/sync")
    public Result<String> syncKnowledgeBase() {
        ragService.syncKnowledgeBase();
        return Results.success("同步完成");
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> healthCheck() {
        boolean embeddingHealthy = embeddingService.healthCheck();
        return Results.success(Map.of(
                "status", embeddingHealthy ? "ok" : "degraded",
                "embeddingService", embeddingHealthy ? "connected" : "disconnected"
        ));
    }

    /**
     * 获取当前用户ID（从请求头或上下文）
     */
    private Long getCurrentUserId(Map<String, Object> request) {
        Object userId = request.get("userId");
        if (userId != null) {
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            }
            if (userId instanceof Long) {
                return (Long) userId;
            }
            if (userId instanceof String) {
                try {
                    return Long.parseLong((String) userId);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 1L; // 默认用户ID，实际应从登录上下文获取
    }
}