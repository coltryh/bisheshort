package com.ryh.shortlink.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ryh.shortlink.project.ai.MiniMaxAiClient;
import com.ryh.shortlink.project.dao.KnowledgeDocumentMapper;
import com.ryh.shortlink.project.dao.RagQaLogMapper;
import com.ryh.shortlink.project.entity.KnowledgeDocument;
import com.ryh.shortlink.project.entity.RagQaLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG 服务
 * 实现基于知识库的智能问答
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RAGService {

    private final EmbeddingService embeddingService;
    private final MiniMaxAiClient aiClient;
    private final KnowledgeDocumentMapper documentMapper;
    private final RagQaLogMapper qaLogMapper;

    /**
     * RAG 问答
     *
     * @param question 用户问题
     * @param userId   用户ID
     * @return RAG 回答结果
     */
    public RAGResult answer(String question, Long userId) {
        long startTime = System.currentTimeMillis();

        RAGResult result = new RAGResult();
        result.setQuestion(question);

        try {
            // 1. 语义检索相关文档
            EmbeddingService.SearchResult searchResult = embeddingService.search(question, 5);

            if (searchResult.getDocuments().isEmpty()) {
                // 知识库为空，返回提示
                result.setAnswer("抱歉，知识库中暂无相关内容。建议您：\n1. 查看官方使用文档\n2. 联系客服获取帮助");
                result.setSources(null);
                return result;
            }

            // 2. 获取相关文档内容
            List<String> retrievedDocs = searchResult.getDocuments();
            result.setRetrievedDocuments(retrievedDocs);

            // 3. 构建系统提示词（包含检索到的文档）
            String systemPrompt = aiClient.buildRagSystemPrompt(retrievedDocs);

            // 4. 调用 AI 生成回答
            String answer = aiClient.chat(question, systemPrompt);
            result.setAnswer(answer);

            // 5. 记录问答日志
            long latency = System.currentTimeMillis() - startTime;
            saveQaLog(userId, question, String.join("\n\n", retrievedDocs), answer, latency);

        } catch (Exception e) {
            log.error("RAG问答异常", e);
            result.setAnswer("抱歉，服务暂时不可用，请稍后再试。");
        }

        return result;
    }

    /**
     * 获取知识库文档列表
     */
    public List<KnowledgeDocument> listDocuments() {
        return documentMapper.selectList(
                new LambdaQueryWrapper<KnowledgeDocument>()
                        .eq(KnowledgeDocument::getStatus, "active")
                        .orderByDesc(KnowledgeDocument::getCreatedTime)
        );
    }

    /**
     * 添加知识库文档
     */
    public boolean addDocument(String title, String content, Long userId) {
        try {
            KnowledgeDocument doc = new KnowledgeDocument();
            doc.setTitle(title);
            doc.setContent(content);
            doc.setChunkCount(1);
            doc.setCategory("custom");
            doc.setStatus("active");
            doc.setUserId(userId);
            doc.setCreatedTime(new Date());
            doc.setUpdatedTime(new Date());

            int rows = documentMapper.insert(doc);

            if (rows > 0) {
                // 同步到 Chroma
                embeddingService.addDocument(
                        "doc_" + doc.getId(),
                        content,
                        title
                );
                return true;
            }
        } catch (Exception e) {
            log.error("添加知识库文档失败", e);
        }
        return false;
    }

    /**
     * 初始化知识库（从数据库同步到 Chroma）
     */
    public void syncKnowledgeBase() {
        List<KnowledgeDocument> docs = listDocuments();
        for (KnowledgeDocument doc : docs) {
            embeddingService.addDocument(
                    "doc_" + doc.getId(),
                    doc.getContent(),
                    doc.getTitle()
            );
        }
        log.info("知识库同步完成，共 {} 条文档", docs.size());
    }

    /**
     * 保存问答日志
     */
    private void saveQaLog(Long userId, String question, String retrievedContent, String answer, long latency) {
        try {
            RagQaLog qaLog = new RagQaLog();
            qaLog.setUserId(userId);
            qaLog.setQuestion(question);
            qaLog.setRetrievedContent(retrievedContent);
            qaLog.setAnswer(answer);
            qaLog.setModel("abab6.5s-chat");
            qaLog.setLatencyMs((int) latency);
            qaLog.setCreatedTime(new Date());
            qaLogMapper.insert(qaLog);
        } catch (Exception e) {
            log.error("保存问答日志失败", e);
        }
    }

    /**
     * RAG 结果
     */
    @lombok.Data
    public static class RAGResult {
        private String question;
        private String answer;
        private List<String> retrievedDocuments;
        private List<String> sources;
    }
}
