package com.ryh.shortlink.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * AI 消息记录实体
 */
@Data
@TableName("t_ai_message")
public class AiMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 角色: user/assistant/system
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 元数据(模型/Token等)
     */
    private String metadata;

    /**
     * 父消息ID(用于树状结构)
     */
    private Long parentMessageId;

    /**
     * 创建时间
     */
    private Date createdTime;
}
