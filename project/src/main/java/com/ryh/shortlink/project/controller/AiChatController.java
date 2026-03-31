package com.ryh.shortlink.project.controller;

import com.ryh.shortlink.project.common.results.Results;
import com.ryh.shortlink.project.entity.AiConversation;
import com.ryh.shortlink.project.entity.AiMessage;
import com.ryh.shortlink.project.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI 对话接口
 */
@RestController
@RequestMapping("/api/short-link/v1/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService chatService;

    /**
     * 创建新对话
     */
    @PostMapping("/conversations")
    public Results<AiConversation> createConversation(@RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId(request);
        AiConversation conversation = chatService.createConversation(userId);
        return Results.success(conversation);
    }

    /**
     * 获取用户的所有对话
     */
    @GetMapping("/conversations")
    public Results<List<AiConversation>> getConversations(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = 1L;
        }
        List<AiConversation> conversations = chatService.getUserConversations(userId);
        return Results.success(conversations);
    }

    /**
     * 获取对话详情
     */
    @GetMapping("/conversations/{id}")
    public Results<AiConversation> getConversation(@PathVariable("id") Long id) {
        AiConversation conversation = chatService.getConversation(id);
        if (conversation == null) {
            return Results.fail("对话不存在");
        }
        return Results.success(conversation);
    }

    /**
     * 获取对话消息列表
     */
    @GetMapping("/conversations/{id}/messages")
    public Results<List<AiMessage>> getMessages(@PathVariable("id") Long conversationId) {
        List<AiMessage> messages = chatService.getConversationMessages(conversationId);
        return Results.success(messages);
    }

    /**
     * 发送消息
     */
    @PostMapping("/chat")
    public Results<AiChatService.ChatResult> chat(@RequestBody Map<String, Object> request) {
        Long conversationId = null;
        if (request.containsKey("conversationId")) {
            Object cid = request.get("conversationId");
            if (cid instanceof Number) {
                conversationId = ((Number) cid).longValue();
            }
        }

        String content = (String) request.get("content");
        Long userId = getCurrentUserId(request);

        if (content == null || content.trim().isEmpty()) {
            return Results.fail("消息内容不能为空");
        }

        AiChatService.ChatResult result = chatService.sendMessage(conversationId, content.trim(), userId);
        return Results.success(result);
    }

    /**
     * 删除对话
     */
    @DeleteMapping("/conversations/{id}")
    public Results<Boolean> deleteConversation(@PathVariable("id") Long id, @RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId(request);
        boolean success = chatService.deleteConversation(id, userId);
        return success ? Results.success(true) : Results.fail("删除失败");
    }

    /**
     * 重命名对话
     */
    @PutMapping("/conversations/{id}")
    public Results<Boolean> renameConversation(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> request
    ) {
        String title = (String) request.get("title");
        Long userId = getCurrentUserId(request);

        if (title == null || title.trim().isEmpty()) {
            return Results.fail("标题不能为空");
        }

        boolean success = chatService.renameConversation(id, title.trim(), userId);
        return success ? Results.success(true) : Results.fail("重命名失败");
    }

    /**
     * 获取当前用户ID（从请求头或上下文）
     */
    private Long getCurrentUserId(Map<String, Object> request) {
        Object userId = request.get("userId");
        if (userId != null) {
            if (userId instanceof Number) {
                return ((Number) userId).longValue();
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
