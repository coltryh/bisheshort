package com.ryh.shortlink.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 知识库文档实体
 */
@Data
@TableName("t_knowledge_document")
public class KnowledgeDocument {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档完整内容
     */
    private String content;

    /**
     * 分块数量
     */
    private Integer chunkCount;

    /**
     * 文档分类
     */
    private String category;

    /**
     * 状态: active/inactive
     */
    private String status;

    /**
     * 创建用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}
