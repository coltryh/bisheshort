package com.ryh.shortlink.project.controller;

import com.ryh.shortlink.project.common.results.Results;
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
    public Results<RAGService.RAGResult> ask(@RequestBody Map<String, Object> request) {
        String question = (String) request.get("question");
        Long userId = getCurrentUserId(request);

        if (question == null || question.trim().isEmpty()) {
            return Results.fail("问题不能为空");
        }

        RAGService.RAGResult result = ragService.answer(question.trim(), userId);
        return Results.success(result);
    }

    /**
     * 获取知识库文档列表
     */
    @GetMapping("/documents")
    public Results<List> listDocuments() {
        return Results.success(ragService.listDocuments());
    }

    /**
     * 添加知识库文档
     */
    @PostMapping("/documents")
    public Results<Boolean> addDocument(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String content = (String) request.get("content");
        Long userId = getCurrentUserId(request);

        if (title == null || content == null) {
            return Results.fail("标题和内容不能为空");
        }

        boolean success = ragService.addDocument(title, content, userId);
        return success ? Results.success(true) : Results.fail("添加失败");
    }

    /**
     * 同步知识库到 Chroma
     */
    @PostMapping("/sync")
    public Results<String> syncKnowledgeBase() {
        ragService.syncKnowledgeBase();
        return Results.success("同步完成");
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Results<Map<String, Object>> healthCheck() {
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
