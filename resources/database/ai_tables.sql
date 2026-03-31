-- ============================================
-- LinkAI AI 功能扩展表结构
-- 创建时间: 2026-03-31
-- ============================================

-- 1. 知识库文档表
CREATE TABLE IF NOT EXISTS `t_knowledge_document` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文档标题',
    `content` TEXT NOT NULL COMMENT '文档完整内容',
    `chunk_count` INT DEFAULT 1 COMMENT '分块数量',
    `category` VARCHAR(50) DEFAULT 'general' COMMENT '文档分类',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态: active/inactive',
    `user_id` BIGINT DEFAULT NULL COMMENT '创建用户ID',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_status` (`status`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='知识库文档表';

-- 2. RAG 问答记录表
CREATE TABLE IF NOT EXISTS `t_rag_qa_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `question` TEXT NOT NULL COMMENT '用户问题',
    `retrieved_content` TEXT COMMENT '检索到的相关内容',
    `answer` TEXT COMMENT 'AI回答',
    `model` VARCHAR(50) DEFAULT 'abab6.5s-chat' COMMENT '使用的AI模型',
    `tokens_used` INT DEFAULT 0 COMMENT '消耗Token数',
    `latency_ms` INT DEFAULT 0 COMMENT '响应延迟(毫秒)',
    `sources` JSON COMMENT '引用来源',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='RAG问答记录表';

-- 3. AI 对话会话表
CREATE TABLE IF NOT EXISTS `t_ai_conversation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` VARCHAR(64) NOT NULL COMMENT '会话ID(唯一标识)',
    `title` VARCHAR(200) DEFAULT '新对话' COMMENT '会话标题',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `message_count` INT DEFAULT 0 COMMENT '消息数量',
    `last_message` TEXT COMMENT '最后一条消息预览',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态: active/archived',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_id` (`session_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_updated_time` (`updated_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI对话会话表';

-- 4. AI 消息记录表
CREATE TABLE IF NOT EXISTS `t_ai_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `conversation_id` BIGINT NOT NULL COMMENT '会话ID',
    `role` VARCHAR(20) NOT NULL COMMENT '角色: user/assistant/system',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `metadata` JSON COMMENT '元数据(模型/Token等)',
    `parent_message_id` BIGINT DEFAULT NULL COMMENT '父消息ID(用于树状结构)',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_conversation_id` (`conversation_id`),
    INDEX `idx_role` (`role`),
    INDEX `idx_parent_message_id` (`parent_message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI消息记录表';


-- ============================================
-- 初始化知识库数据（产品 FAQ）
-- ============================================

INSERT INTO `t_knowledge_document` (`title`, `content`, `chunk_count`, `category`, `status`) VALUES
('创建短链接', '在"我的空间"页面，点击"新建链接"按钮，输入您要缩短的原始链接（长链接），系统会自动生成一个短链接。您还可以自定义短链接的后缀、设置有效期和描述信息。', 1, '基础操作', 'active'),
('设置有效期', '创建短链接时，在有效期选项中选择：1. 永久有效 - 链接永不过期；2. 定时过期 - 设置具体的过期日期和时间；3. 点击次数限制 - 设置链接被访问的最大次数，达到后自动失效。设置后可以在管理后台随时修改。', 1, '高级功能', 'active'),
('批量创建', '支持批量创建短链接！您可以：1. 使用CSV文件导入，格式为：原始链接,描述,有效期（每行一条）；2. 在批量创建页面粘贴多行URL列表；3. 使用API接口程序化创建。批量创建单次最多支持1000条。', 1, '高级功能', 'active'),
('访问统计', '在链接列表中点击对应链接的"详情"按钮，可以查看：1. 实时PV（页面浏览量）、UV（独立访客数）、IP数；2. 访问趋势图，按小时/日/周展示；3. 访问来源地区分布；4. 使用的设备和浏览器统计；5. 网络运营商分析。所有数据支持导出为Excel。', 1, '数据分析', 'active'),
('链接故障排除', '链接打不开的常见原因：1. 原链接失效 - 被访问的原始网站已下线或设置了访问限制；2. 链接已过期 - 检查是否设置了有效期；3. 链接已被删除 - 在回收站中查看是否被误删；4. 被安全软件拦截 - 部分企业网络会拦截短链接。请先确认原因，然后对应处理。', 1, '常见问题', 'active'),
('自定义URL', '创建链接时，在自定义后缀输入框中填写您想要的后缀（如：my-link），系统会生成 linkai.com/my-link 格式的短链接。注意：后缀只能包含字母、数字、中划线和下划线，长度3-32字符，且不能与已有链接重复。', 1, '基础操作', 'active'),
('AI智能分析', 'LinkAI集成了AI智能分析功能，可以：1. 分析访问趋势 - 识别流量高峰和异常波动；2. 用户画像分析 - 分析访问者的设备、地区、行为特征；3. 优化建议 - 基于数据给出提升链接效果的运营建议；4. 自动生成描述 - AI自动为您的链接生成吸引人的描述文案。', 1, 'AI功能', 'active'),
('联系客服', '您可以通过以下方式联系我们的客服团队：1. 在线客服 - 点击页面右下角的"在线咨询"按钮；2. 邮件支持 - 发送邮件到 support@linkai.com；3. 智能客服 - 24小时AI客服随时为您解答常见问题；4. 工单系统 - 在"帮助中心"提交工单，我们会在24小时内回复。', 1, '其他', 'active');
