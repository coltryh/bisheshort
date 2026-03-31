package com.ryh.shortlink.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.project.entity.AiConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI 对话会话 Mapper
 */
@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversation> {

    /**
     * 查询用户的所有会话
     */
    @Select("SELECT * FROM t_ai_conversation WHERE user_id = #{userId} ORDER BY updated_time DESC")
    List<AiConversation> findByUserId(@Param("userId") Long userId);
}
