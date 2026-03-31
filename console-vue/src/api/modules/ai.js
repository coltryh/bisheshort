import http from '../axios'

export default {
  // ========== 原有 AI 功能 ==========
  // AI分析短链接统计数据
  analyzeStats(data) {
    return http({
      url: '/ai/analyze',
      method: 'post',
      data
    })
  },

  // AI生成短链接描述建议
  suggestDescription(params) {
    return http({
      url: '/ai/suggest',
      method: 'post',
      params
    })
  },

  // AI智能问答
  chat(params) {
    return http({
      url: '/ai/chat',
      method: 'post',
      params
    })
  },

  // AI批量分析
  batchAnalyze(data) {
    return http({
      url: '/ai/batch-analyze',
      method: 'post',
      data
    })
  },

  // ========== 新增 AI 对话功能 ==========
  // 创建新对话
  createConversation() {
    return http({
      url: '/short-link/v1/ai/conversations',
      method: 'post',
      data: { userId: 1 }
    })
  },

  // 获取用户的所有对话
  getConversations(userId = 1) {
    return http({
      url: '/short-link/v1/ai/conversations',
      method: 'get',
      params: { userId }
    })
  },

  // 获取对话消息列表
  getMessages(conversationId) {
    return http({
      url: `/short-link/v1/ai/conversations/${conversationId}/messages`,
      method: 'get'
    })
  },

  // 发送消息
  sendMessage(conversationId, content) {
    return http({
      url: '/short-link/v1/ai/chat',
      method: 'post',
      data: {
        conversationId,
        content,
        userId: 1
      }
    })
  },

  // 删除对话
  deleteConversation(id) {
    return http({
      url: `/short-link/v1/ai/conversations/${id}`,
      method: 'delete',
      data: { userId: 1 }
    })
  },

  // 重命名对话
  renameConversation(id, title) {
    return http({
      url: `/short-link/v1/ai/conversations/${id}`,
      method: 'put',
      data: { title, userId: 1 }
    })
  },

  // ========== RAG 知识库功能 ==========
  // RAG 问答
  ragAsk(question) {
    return http({
      url: '/short-link/v1/rag/ask',
      method: 'post',
      data: { question, userId: 1 }
    })
  },

  // 获取知识库文档列表
  getDocuments() {
    return http({
      url: '/short-link/v1/rag/documents',
      method: 'get'
    })
  },

  // 添加知识库文档
  addDocument(title, content) {
    return http({
      url: '/short-link/v1/rag/documents',
      method: 'post',
      data: { title, content, userId: 1 }
    })
  },

  // 同步知识库
  syncKnowledgeBase() {
    return http({
      url: '/short-link/v1/rag/sync',
      method: 'post'
    })
  },

  // 健康检查
  checkHealth() {
    return http({
      url: '/short-link/v1/rag/health',
      method: 'get'
    })
  }
}
