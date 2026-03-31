package com.ryh.shortlink.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.project.entity.RagQaLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * RAG 问答记录 Mapper
 */
@Mapper
public interface RagQaLogMapper extends BaseMapper<RagQaLog> {
}
