package com.ryh.shortlink.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryh.shortlink.project.ai.MiniMaxAiClient;
import com.ryh.shortlink.project.dao.AiConversationMapper;
import com.ryh.shortlink.project.dao.AiMessageMapper;
import com.ryh.shortlink.project.entity.AiConversation;
import com.ryh.shortlink.project.entity.AiMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 对话服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private final AiConversationMapper conversationMapper;
    private final AiMessageMapper messageMapper;
    private final MiniMaxAiClient aiClient;

    private static final int MAX_CONTEXT_MESSAGES = 10; // 最大上下文消息数

    /**
     * 创建新对话
     */
    public AiConversation createConversation(Long userId) {
        AiConversation conversation = new AiConversation();
        conversation.setSessionId(UUID.randomUUID().toString().replace("-", ""));
        conversation.setTitle("新对话");
        conversation.setUserId(userId);
        conversation.setMessageCount(0);
        conversation.setStatus("active");
        conversation.setCreatedTime(new Date());
        conversation.setUpdatedTime(new Date());

        conversationMapper.insert(conversation);
        return conversation;
    }

    /**
     * 获取用户的所有对话
     */
    public List<AiConversation> getUserConversations(Long userId) {
        return conversationMapper.findByUserId(userId);
    }

    /**
     * 获取对话详情
     */
    public AiConversation getConversation(Long conversationId) {
        return conversationMapper.selectById(conversationId);
    }

    /**
     * 获取对话消息
     */
    public List<AiMessage> getConversationMessages(Long conversationId) {
        return messageMapper.findByConversationId(conversationId);
    }

    /**
     * 发送消息并获取 AI 回复
     */
    @Transactional(rollbackFor = Exception.class)
    public ChatResult sendMessage(Long conversationId, String content, Long userId) {
        long startTime = System.currentTimeMillis();

        ChatResult result = new ChatResult();

        // 1. 获取或创建对话
        AiConversation conversation;
        if (conversationId == null) {
            conversation = createConversation(userId);
        } else {
            conversation = conversationMapper.selectById(conversationId);
            if (conversation == null) {
                conversation = createConversation(userId);
            }
        }

        result.setConversationId(conversation.getId());
        result.setSessionId(conversation.getSessionId());

        try {
            // 2. 保存用户消息
            AiMessage userMessage = new AiMessage();
            userMessage.setConversationId(conversation.getId());
            userMessage.setRole("user");
            userMessage.setContent(content);
            userMessage.setCreatedTime(new Date());
            messageMapper.insert(userMessage);

            // 3. 获取历史上下文
            List<AiMessage> history = getRecentMessages(conversation.getId(), MAX_CONTEXT_MESSAGES);

            // 4. 构建带上下文的 Prompt
            String prompt = buildContextPrompt(history, content);

            // 5. 调用 AI
            String aiResponse = aiClient.chat(prompt);

            // 6. 保存 AI 回复
            AiMessage assistantMessage = new AiMessage();
            assistantMessage.setConversationId(conversation.getId());
            assistantMessage.setRole("assistant");
            assistantMessage.setContent(aiResponse);
            assistantMessage.setParentMessageId(userMessage.getId());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("latency", System.currentTimeMillis() - startTime);
            assistantMessage.setMetadata(metadata.toString());

            assistantMessage.setCreatedTime(new Date());
            messageMapper.insert(assistantMessage);

            // 7. 更新对话信息
            conversation.setMessageCount(conversation.getMessageCount() + 2);
            conversation.setLastMessage(content.length() > 50 ? content.substring(0, 50) + "..." : content);

            // 如果是第一轮对话，生成标题
            if (conversation.getMessageCount() == 2) {
                conversation.setTitle(generateTitle(content));
            }

            conversation.setUpdatedTime(new Date());
            conversationMapper.updateById(conversation);

            result.setMessageId(assistantMessage.getId());
            result.setContent(aiResponse);
            result.setSuccess(true);

        } catch (Exception e) {
            log.error("AI对话异常", e);
            result.setContent("抱歉，服务暂时不可用，请稍后再试。");
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * 获取最近的消息（倒序）
     */
    private List<AiMessage> getRecentMessages(Long conversationId, int limit) {
        List<AiMessage> messages = messageMapper.findRecentMessages(conversationId, limit * 2);
        // 倒序后取前 N 条，再正序
        Collections.reverse(messages);
        return messages.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 构建带上下文的 Prompt
     */
    private String buildContextPrompt(List<AiMessage> history, String newMessage) {
        StringBuilder prompt = new StringBuilder();

        if (!history.isEmpty()) {
            prompt.append("【对话历史】\n");
            for (AiMessage msg : history) {
                prompt.append(msg.getRole().equals("user") ? "用户" : "助手");
                prompt.append(": ");
                prompt.append(msg.getContent());
                prompt.append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("【新消息】\n");
        prompt.append("用户: ");
        prompt.append(newMessage);
        prompt.append("\n\n请根据对话历史继续回答用户的问题。");

        return prompt.toString();
    }

    /**
     * 生成对话标题（取用户第一条消息的前20字）
     */
    private String generateTitle(String firstMessage) {
        if (firstMessage.length() <= 20) {
            return firstMessage;
        }
        return firstMessage.substring(0, 20) + "...";
    }

    /**
     * 删除对话
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConversation(Long conversationId, Long userId) {
        // 验证归属
        AiConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            return false;
        }

        // 删除消息
        messageMapper.delete(new LambdaQueryWrapper<AiMessage>()
                .eq(AiMessage::getConversationId, conversationId));

        // 删除对话
        conversationMapper.deleteById(conversationId);

        return true;
    }

    /**
     * 重命名对话
     */
    public boolean renameConversation(Long conversationId, String newTitle, Long userId) {
        AiConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            return false;
        }

        conversation.setTitle(newTitle);
        conversation.setUpdatedTime(new Date());
        conversationMapper.updateById(conversation);

        return true;
    }

    /**
     * 聊天结果
     */
    @lombok.Data
    public static class ChatResult {
        private Long conversationId;
        private String sessionId;
        private Long messageId;
        private String content;
        private boolean success;
    }
}
