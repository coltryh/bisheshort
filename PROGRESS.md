# ShortLink 项目 AI 化改造 - 进度跟踪

> 最后更新: 2026-03-31

---

## 整体进度

| 阶段 | 状态 | 说明 |
|------|------|------|
| Phase 1: 首页开发 | ✅ 已完成 | |
| Phase 2: AI 客服助手 | ⏳ 进行中 | RAG + Chroma |
| Phase 3: 对话历史持久化 | ⏳ 待开始 | |
| Phase 4: 项目整合 | ⏳ 待开始 | |

---

## 阶段 1: 首页开发 ✅

### 已完成

- [x] Figma 设计首页原型
  - [x] 导出为 React + Tailwind CSS 代码
  - [x] 设计包含: Header, Hero, Features, Process, Footer

- [x] 创建 `LandingIndex.vue` 组件
  - [x] 路径: `console-vue/src/views/landing/LandingIndex.vue`
  - [x] 实现 Hero 区域
  - [x] 实现 Features 功能卡片 (4个)
  - [x] 实现 Process 流程图 (3步)
  - [x] 实现 AI Assistant 区域
  - [x] 实现 Footer 页脚
  - [x] 跳转到登录页功能

- [x] 添加路由配置
  - [x] 修改 `router/index.js`，添加首页路由 `/`
  - [x] 修改登录页跳转逻辑 (已登录跳转首页)
  - [x] 路由守卫：首页不需要登录

### 设计预览
```
┌────────────────────────────────────────────────────┐
│  LinkAI Logo    首页  功能  AI助手      [登录]     │ ← Header
├────────────────────────────────────────────────────┤
│                  AI驱动的智能平台                   │
│                 智能短链接平台                      │
│              AI驱动的链接管理与分析平台              │
│                    [立即开始]                       │
│  ┌──────────────────────────────────────────┐     │
│  │ linkAI.com ████████████                   │     │ ← 装饰卡片
│  │           ████████                         │     │
│  │         ████████████████                  │     │
│  └──────────────────────────────────────────┘     │
├────────────────────────────────────────────────────┤
│                    强大功能                         │
│  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐         │
│  │短链接│  │访问  │  │AI智能│  │智能  │         │ ← Features
│  │生成  │  │统计  │  │分析  │  │客服  │         │
│  └──────┘  └──────┘  └──────┘  └──────┘         │
├────────────────────────────────────────────────────┤
│                    简单三步                         │
│    01        →       02        →       03         │
│  注册账号           创建链接         分享追踪       │ ← Process
├────────────────────────────────────────────────────┤
│                   AI 智能助手                       │
│         [用户问题]                                  │
│         [AI回答]                                   │ ← Assistant
├────────────────────────────────────────────────────┤
│  LinkAI    快速链接    支持                        │
│  Logo      产品功能    帮助中心                     │
│  描述...    价格方案    联系我们                    │ ← Footer
│  © 2026 LinkAI                                  │
└────────────────────────────────────────────────────┘
```

### 设计资源
- Figma 源文件: `C:\Users\2471197\Downloads\LinkAI_design\`
- Vue 组件: `C:\Users\2471197\IdeaProjects\short\console-vue\src\views\landing\`

### 关键文件变更
```
修改:
✅ console-vue/src/router/index.js - 添加首页路由，修改路由守卫

创建:
✅ console-vue/src/views/landing/LandingIndex.vue
```

---

## 阶段 2: AI 客服助手（RAG + Chroma）✅ 已完成

### 后端实体类 ✅
```
project/src/main/java/com/ryh/shortlink/project/entity/
├── KnowledgeDocument.java     # 知识库文档实体
├── RagQaLog.java             # RAG问答记录实体
├── AiConversation.java        # AI对话会话实体
└── AiMessage.java            # AI消息记录实体
```

### 后端 Mapper ✅
```
project/src/main/java/com/ryh/shortlink/project/dao/
├── KnowledgeDocumentMapper.java  # 知识库文档Mapper
├── RagQaLogMapper.java          # RAG问答记录Mapper
├── AiConversationMapper.java   # 对话会话Mapper
└── AiMessageMapper.java        # 消息记录Mapper
```

### 后端配置 ✅
```
project/src/main/java/com/ryh/shortlink/project/config/
├── EmbeddingConfig.java       # Embedding服务配置
└── MiniMaxConfig.java         # MiniMax AI配置
```

### 后端服务 ✅
```
project/src/main/java/com/ryh/shortlink/project/
├── ai/MiniMaxAiClient.java    # MiniMax AI客户端
├── service/EmbeddingService.java  # Embedding服务
├── service/RAGService.java     # RAG问答服务
├── service/AiChatService.java # AI对话服务
├── controller/RAGController.java   # RAG接口
└── controller/AiChatController.java # 对话接口
```

### 前端 API ✅
- 更新 `console-vue/src/api/modules/ai.js`
- 添加对话相关 API
- 添加 RAG 知识库 API

### 前端页面 ✅
- `console-vue/src/views/ai/AiChatIndex.vue` - AI对话页面
  - 左侧对话列表
  - 右侧聊天区域
  - 欢迎页 + 快捷问题
  - 消息发送/接收

### 路由和导航 ✅
- 更新 `router/index.js` - 添加 `/home/ai` 路由
- 更新 `HomeIndex.vue` - 添加 AI助手按钮

### Python Embedding 服务 ✅

创建目录: `embedding-service/`

- [x] `requirements.txt` - 依赖列表
- [x] `app.py` - Flask 主应用，提供 HTTP API
- [x] `chroma_service.py` - Chroma 向量数据库封装
- [x] `init_knowledge.py` - 知识库初始化脚本

**服务接口**:
| 接口 | 方法 | 功能 |
|------|------|------|
| `/health` | GET | 健康检查 |
| `/embed` | POST | 获取文本向量 |
| `/search` | POST | 语义检索 |
| `/add` | POST | 添加文档 |
| `/upsert` | POST | 添加文档(自动embedding) |
| `/list` | GET | 获取所有文档 |
| `/delete` | POST | 删除文档 |

**启动方式**:
```bash
cd embedding-service
pip install -r requirements.txt
python init_knowledge.py  # 初始化知识库
python app.py              # 启动服务 (端口5000)
```

### 数据库表 ✅

创建文件: `resources/database/ai_tables.sql`

- [x] `t_knowledge_document` - 知识库文档表
- [x] `t_rag_qa_log` - RAG问答记录表
- [x] `t_ai_conversation` - AI对话会话表
- [x] `t_ai_message` - AI消息记录表

---

## 使用说明

### 1. 初始化数据库

```bash
# 执行 SQL 文件创建表
mysql -u root -p your_database < resources/database/ai_tables.sql
```

### 2. 启动 Python Embedding 服务

```bash
cd embedding-service
pip install -r requirements.txt
python init_knowledge.py  # 初始化知识库（可选）
python app.py              # 启动服务 (端口5000)
```

### 3. 配置 MiniMax API

在 `application.yml` 中添加配置：
```yaml
minimax:
  api-key: your-api-key
  group-id: your-group-id
  chat-model: abab6.5s-chat
  embedding-model: emb-01

embedding:
  base-url: http://localhost:5000
```

### 4. 访问系统

- 首页: http://localhost:5173/
- 管理后台: http://localhost:5173/home
- AI助手: http://localhost:5173/home/ai

---

## 简历项目描述

```
【LinkAI - 智能短链接平台】

技术栈：Vue3 + Spring Cloud Alibaba + MyBatis-Plus + Redis + MiniMax AI + Chroma

项目亮点：
1. 基于 RAG + Chroma 向量数据库实现智能客服系统，支持语义检索和产品知识库问答
2. Python/Flask 构建 Embedding 微服务，调用 MiniMax API 实现文本向量化
3. AI 对话历史 MySQL 持久化，支持多轮上下文续接
4. Spring Cloud 微服务架构，Redis 缓存与分布式锁
5. ShardingSphere 分库分表，支持海量数据存储
6. Sentinel 接口限流与防刷机制
```

---

## 项目文件结构

```
short/
├── console-vue/                    # 前端
│   ├── src/
│   │   ├── views/
│   │   │   ├── landing/           # ✅ 首页
│   │   │   │   └── LandingIndex.vue
│   │   │   └── ai/               # ✅ AI 对话
│   │   │       └── AiChatIndex.vue
│   │   ├── api/modules/
│   │   │   └── ai.js             # ✅ AI API
│   │   └── router/
│   │       └── index.js          # ✅ 路由配置
│   └── ...
├── project/                        # 后端
│   └── src/main/java/
│       └── com/ryh/shortlink/project/
│           ├── ai/
│           │   └── MiniMaxAiClient.java  # ✅
│           ├── config/
│           │   ├── MiniMaxConfig.java     # ✅
│           │   └── EmbeddingConfig.java   # ✅
│           ├── controller/
│           │   ├── RAGController.java     # ✅
│           │   └── AiChatController.java   # ✅
│           ├── service/
│           │   ├── RAGService.java       # ✅
│           │   ├── AiChatService.java     # ✅
│           │   └── EmbeddingService.java  # ✅
│           ├── dao/
│           │   ├── KnowledgeDocumentMapper.java  # ✅
│           │   ├── RagQaLogMapper.java           # ✅
│           │   ├── AiConversationMapper.java    # ✅
│           │   └── AiMessageMapper.java         # ✅
│           └── entity/
│               ├── KnowledgeDocument.java  # ✅
│               ├── RagQaLog.java           # ✅
│               ├── AiConversation.java     # ✅
│               └── AiMessage.java          # ✅
├── embedding-service/              # ✅ Python Embedding 服务
│   ├── app.py
│   ├── chroma_service.py
│   ├── init_knowledge.py
│   └── requirements.txt
└── resources/
    └── database/
        └── ai_tables.sql          # ✅ 数据库表
```
