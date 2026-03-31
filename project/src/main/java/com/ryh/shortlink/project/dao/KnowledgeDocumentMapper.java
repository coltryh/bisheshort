package com.ryh.shortlink.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.project.entity.KnowledgeDocument;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库文档 Mapper
 */
@Mapper
public interface KnowledgeDocumentMapper extends BaseMapper<KnowledgeDocument> {
}
