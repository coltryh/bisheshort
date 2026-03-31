package com.ryh.shortlink.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * AI 对话会话实体
 */
@Data
@TableName("t_ai_conversation")
public class AiConversation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID(唯一标识)
     */
    private String sessionId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 最后一条消息预览
     */
    private String lastMessage;

    /**
     * 状态: active/archived
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}
