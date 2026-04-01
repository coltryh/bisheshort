# AI 功能修复记录

## 问题概述

从 Git 拉取 `feat: 添加 AI 功能 - 智能客服 + RAG 知识库` 提交后，代码存在多个编译错误和配置问题，无法正常启动运行。

---

## 修复过程

### 1. 编译错误修复

#### 1.1 Import 路径错误
**问题**：`AiChatController.java` 和 `RAGController.java` 中导入了错误的包路径
- 错误：`com.ryh.shortlink.project.common.results.Results`
- 正确：`com.ryh.shortlink.project.common.convention.result.Results`

**涉及文件**：
- `project/src/main/java/com/ryh/shortlink/project/controller/AiChatController.java`
- `project/src/main/java/com/ryh/shortlink/project/controller/RAGController.java`

#### 1.2 Results.fail() 方法不存在
**问题**：`Results` 类没有 `fail()` 方法，只有 `failure()` 方法

**修复**：将所有 `Results.fail("错误信息")` 改为：
```java
new Result<T>().setCode("0").setMessage("错误信息")
```

#### 1.3 MyBatis Mapper 扫描问题
**问题**：`@MapperScan` 只扫描 `dao.mapper` 包，但 AI Mapper 位于 `dao` 包下

**修复**：修改 `ShortLinkApplication.java`：
```java
@MapperScan({"com.ryh.shortlink.project.dao.mapper", "com.ryh.shortlink.project.dao"})
```

---

### 2. 配置修复

#### 2.1 Redis 密码更新
**问题**：Redis 密码从 `123456` 改为 `Ryh3.1415926`

**修改文件**：
- `admin/src/main/resources/application.yaml`
- `gateway/src/main/resources/application.yaml`
- `project/src/main/resources/application.yaml`
- `aggregation/src/main/resources/application.yaml`

#### 2.2 MiniMax AI 配置
**问题**：原使用智谱 AI，需要改为 MiniMax

**修改文件**：`project/src/main/resources/application.yaml`
```yaml
minimax:
  api-key: sk-cp-xxx
  group-id: 2022478885660140272
  base-url: https://api.minimax.chat/v
  chat-model: abab6.5s-chat
  embedding-model: emb-01
```

#### 2.3 MiniMaxAiClient 缺失方法
**问题**：`MiniMaxAiClient` 缺少 `analyzeStats`、`suggestDescription`、`answer` 方法

**修复**：在 `MiniMaxAiClient.java` 中添加这些方法

---

### 3. Python Embedding 服务修复

#### 3.1 NumPy 版本兼容问题
**问题**：`chromadb==0.4.22` 不兼容 `numpy>=2.0`

**错误信息**：
```
AttributeError: `np.float_` was removed in the NumPy 2.0 release
```

**修复**：在 `requirements.txt` 中添加版本限制：
```
numpy<2.0
```

---

### 4. 前端修复

#### 4.1 Element Plus Icons 不存在
**问题**：`LandingIndex.vue` 使用了不存在的图标 `Link2`、`UserPlus`、`Message`、`Position`

**修复**：
- `Link2` → `Link` (别名 `LinkIcon`)
- `UserPlus` → `User`
- `Message` → `ChatLineSquare`
- `Position` → `LocationInformation`

**涉及文件**：`console-vue/src/views/landing/LandingIndex.vue`

#### 4.2 AI API 路径问题
**问题**：`ai.js` 使用了 `admin` 模块的 baseURL，导致 API 路径重复

**修复**：创建独立的 axios 实例
```javascript
const projectHttp = axios.create({
    baseURL: '/api/short-link',
    timeout: 15000
})
```

**涉及文件**：`console-vue/src/api/modules/ai.js`

#### 4.3 Vite 代理配置
**问题**：Vite 代理将所有请求发到 `localhost:8002` (admin)，但 AI 功能在 `localhost:8001` (project)

**修复**：`vite.config.js`
```javascript
proxy: {
    '/api/short-link/admin': {
        target: 'http://localhost:8002',
        changeOrigin: true
    },
    '/api/short-link/v1': {
        target: 'http://localhost:8001',
        changeOrigin: true
    }
}
```

#### 4.4 Gateway Token 白名单
**问题**：AI 路径未加入 Token 验证白名单

**修复**：`gateway/src/main/resources/application-dev.yaml`
```yaml
- name: TokenValidate
  args:
    whitePathList:
      - /api/short-link/v1/ai
      - /api/short-link/v1/rag
```

#### 4.5 前端响应数据处理
**问题**：`AiChatIndex.vue` 中直接使用 `res.data` 但实际返回格式是 `{code, data: {...}}`

**修复**：多处 `res.data` 改为 `res.data.data`
```javascript
// 之前
conversations.value = res.data
// 之后
conversations.value = res.data.data
```

---

### 5. 后端 Bug 修复

#### 5.1 Metadata JSON 格式错误
**问题**：`AiChatService` 中使用 `metadata.toString()` 生成无效 JSON

**错误**：`{latency=1469}` （无效 JSON）
**修复**：`JSON.toJSONString(metadata)` 生成 `{"latency":1469}`

**涉及文件**：`project/src/main/java/com/ryh/shortlink/project/service/AiChatService.java`

#### 5.2 MiniMax API 调用问题
**问题**：MiniMax API 返回 308 重定向

**可能原因**：
1. Coding Plan 的 API Key 可能有特殊限制
2. API 路径或模型名称不正确
3. 需要使用不同的 API 地址

**临时修复**：添加详细日志便于排查
- 增加 `setFollowRedirects(false)` 避免自动重定向
- 增加配置信息日志

---

## 踩坑总结

### 1. Git 提交前务必确保代码能编译
提交 `feat: 添加 AI 功能` 时，代码存在 import 错误、方法不存在等问题，说明提交前未进行编译验证。

### 2. 前后端联调注意路径匹配
- Gateway 路由配置
- Vite 代理配置
- Controller 实际路径
- 前端 axios baseURL

### 3. API 响应格式要一致
后端返回 `{code, data}` 格式，前端需要正确解析 `res.data.data`。

### 4. 数据库表需要手动创建
新增的 AI 相关表（`t_ai_conversation`、`t_ai_message` 等）不会自动创建，需要执行 SQL 脚本。

### 5. 第三方服务 API 需要验证
MiniMax API 等第三方服务需要通过 curl 等方式先验证可用性，再集成到代码中。

---

## 待解决问题

~~### MiniMax API 308 问题~~
~~**现象**：`https://api.minimax.chat/v/text/chatcompletion_v2` 返回 308 重定向到官网~~

~~**可能原因**：~~
~~1. Coding Plan 的 API Key 不支持该 API 端点~~
~~2. API 地址或模型名称不正确~~
~~3. 需要在 MiniMax 后台确认正确的 API 用法~~

~~**建议**：~~
~~1. 登录 MiniMax 后台检查 Coding Plan 支持的 API~~
~~2. 确认正确的 API 地址和模型名称~~
~~3. 或暂时使用 Mock 模式进行开发测试~~

✅ **已解决** - 2026-04-01 下午

---

### MiniMax API 响应格式解析问题

**问题**：MiniMax API 返回 200，但响应中 `message` 字段为 null

**错误**：`Cannot invoke "JSONObject.getString(String)" because "message" is null`

**排查过程**：
1. 添加详细日志打印完整响应内容
2. 发现 MiniMax API 响应格式与代码预期不符
3. 实际返回格式可能是 `messages` (JSONArray) 而不是 `message` (JSONObject)

**修复方案**：
1. 同时支持 `messages` (JSONArray) 和 `message` (JSONObject) 两种格式
2. 添加 `base_resp` 错误码检查
3. 打印详细日志便于调试

**修改文件**：`MiniMaxAiClient.java`

**最终正确的 MiniMax 配置**：
```yaml
minimax:
  api-key: sk-cp-ci7wMCIWzMmkymTp0VdexCloEVWjevQZ-OqJzHzpcMPfYMPbRWHUzP50_QbSREsD7UTszpw4O1fEMU8T2-qaORrvGdnr7f-La3dJ7Qd7uw85sxgk349JAl0
  group-id: 2022478885660140272
  base-url: https://api.minimaxi.com/v1
  chat-model: MiniMax-M2.7
  embedding-model: emb-01
```

**MiniMax API 地址说明**（区分国际/国内版）：
| 地区 | API Host |
|------|----------|
| 国际版 | `https://api.minimax.io` |
| 国内版（Coding Plan） | `https://api.minimaxi.com` |

**API 路径**：`/v1/text/chatcompletion_v2`

---

## 修改文件清单

| 文件 | 修改类型 |
|------|----------|
| `ShortLinkApplication.java` | 功能修复 |
| `AiChatController.java` | 功能修复 |
| `RAGController.java` | 功能修复 |
| `ShortLinkAiController.java` | 功能修复 |
| `MiniMaxAiClient.java` | Bug修复 + 功能完善 |
| `AiChatService.java` | Bug修复 |
| `EmbeddingService.java` | 代码完善 |
| `RAGService.java` | 代码完善 |
| `MiniMaxConfig.java` | 配置完善 |
| `EmbeddingConfig.java` | 配置完善 |
| `application.yaml` (project) | 配置更新（多次） |
| `application.yaml` (admin) | 配置更新 |
| `application.yaml` (gateway) | 配置更新 |
| `application-dev.yaml` (gateway) | 配置更新 |
| `application.yaml` (aggregation) | 配置更新 |
| `requirements.txt` | 依赖修复 |
| `ai.js` | 功能修复 |
| `AiChatIndex.vue` | 功能修复 |
| `LandingIndex.vue` | Bug修复 |
| `vite.config.js` | 配置修复 |

---

*文档生成时间：2026-04-01*
*作者：Claude Code*
