/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ryh.shortlink.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ryh.shortlink.project.entity.AiMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI 消息记录 Mapper
 */
@Mapper
public interface AiMessageMapper extends BaseMapper<AiMessage> {

    /**
     * 查询会话的所有消息（按时间正序）
     */
    @Select("SELECT * FROM t_ai_message WHERE conversation_id = #{conversationId} ORDER BY created_time ASC")
    List<AiMessage> findByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 查询最近 N 条消息（用于上下文）
     */
    @Select("SELECT * FROM t_ai_message WHERE conversation_id = #{conversationId} ORDER BY created_time DESC LIMIT #{limit}")
    List<AiMessage> findRecentMessages(@Param("conversationId") Long conversationId, @Param("limit") Integer limit);
}
