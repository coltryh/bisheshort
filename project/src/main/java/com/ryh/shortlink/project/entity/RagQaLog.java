package com.ryh.shortlink.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * RAG 问答记录实体
 */
@Data
@TableName("t_rag_qa_log")
public class RagQaLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户问题
     */
    private String question;

    /**
     * 检索到的相关内容
     */
    private String retrievedContent;

    /**
     * AI回答
     */
    private String answer;

    /**
     * 使用的AI模型
     */
    private String model;

    /**
     * 消耗Token数
     */
    private Integer tokensUsed;

    /**
     * 响应延迟(毫秒)
     */
    private Integer latencyMs;

    /**
     * 引用来源
     */
    private String sources;

    /**
     * 创建时间
     */
    private Date createdTime;
}
