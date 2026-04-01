import axios from 'axios'
import {getToken, getUsername} from '@/core/auth.js'
import {isNotEmpty} from '@/utils/plugins.js'
import router from "@/router";
import { ElMessage } from 'element-plus'

// 创建 admin 模块的 axios 实例（用于短链接AI分析功能）
const http = axios.create({
    baseURL: '/api/short-link/admin/v1',
    timeout: 15000
})

// 创建 project 模块的 axios 实例（用于AI对话功能）
const projectHttp = axios.create({
    baseURL: '/api/short-link',
    timeout: 15000
})

// 请求拦截 - admin
http.interceptors.request.use((config) => {
    config.headers.Token = isNotEmpty(getToken()) ? getToken() : ''
    config.headers.Username = isNotEmpty(getUsername()) ? getUsername() : ''
    return config
})

// 响应拦截 - admin
http.interceptors.response.use((res) => {
    if (res.status == 0 || res.status == 200) {
        return Promise.resolve(res)
    }
    return Promise.reject(res)
}, (err) => {
    if (err.response && err.response.status === 401) {
        localStorage.removeItem('token')
        router.push('/login')
    }
    return Promise.reject(err)
})

// 请求拦截 - project
projectHttp.interceptors.request.use((config) => {
    config.headers.Token = isNotEmpty(getToken()) ? getToken() : ''
    config.headers.Username = isNotEmpty(getUsername()) ? getUsername() : ''
    return config
})

// 响应拦截 - project
projectHttp.interceptors.response.use((res) => {
    if (res.status == 0 || res.status == 200) {
        return Promise.resolve(res)
    }
    return Promise.reject(res)
}, (err) => {
    if (err.response && err.response.status === 401) {
        localStorage.removeItem('token')
        router.push('/login')
    }
    return Promise.reject(err)
})

export default {
  // ========== 短链接 AI 分析功能（使用 admin 接口）==========
  // AI智能问答（短链接分析）
  chat(data) {
    return http({
      url: '/ai/chat',
      method: 'post',
      data
    })
  },

  // AI分析短链接统计数据
  analyzeStats(data) {
    return http({
      url: '/ai/analyze',
      method: 'post',
      data
    })
  },

  // AI生成短链接描述建议
  suggestDescription(data) {
    return http({
      url: '/ai/suggest',
      method: 'post',
      data
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

  // ========== AI 对话功能（使用 project 接口）==========
  // 创建新对话
  createConversation() {
    return projectHttp({
      url: '/v1/ai/conversations',
      method: 'post',
      data: { userId: 1 }
    })
  },

  // 获取用户的所有对话
  getConversations(userId = 1) {
    return projectHttp({
      url: '/v1/ai/conversations',
      method: 'get',
      params: { userId }
    })
  },

  // 获取对话消息列表
  getMessages(conversationId) {
    return projectHttp({
      url: `/v1/ai/conversations/${conversationId}/messages`,
      method: 'get'
    })
  },

  // 发送消息
  sendMessage(conversationId, content) {
    return projectHttp({
      url: '/v1/ai/chat',
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
    return projectHttp({
      url: `/v1/ai/conversations/${id}`,
      method: 'delete',
      data: { userId: 1 }
    })
  },

  // 重命名对话
  renameConversation(id, title) {
    return projectHttp({
      url: `/v1/ai/conversations/${id}`,
      method: 'put',
      data: { title, userId: 1 }
    })
  },

  // ========== RAG 知识库功能（使用 project 接口）==========
  // RAG 问答
  ragAsk(question) {
    return projectHttp({
      url: '/v1/rag/ask',
      method: 'post',
      data: { question, userId: 1 }
    })
  },

  // 获取知识库文档列表
  getDocuments() {
    return projectHttp({
      url: '/v1/rag/documents',
      method: 'get'
    })
  },

  // 添加知识库文档
  addDocument(title, content) {
    return projectHttp({
      url: '/v1/rag/documents',
      method: 'post',
      data: { title, content, userId: 1 }
    })
  },

  // 同步知识库
  syncKnowledgeBase() {
    return projectHttp({
      url: '/v1/rag/sync',
      method: 'post'
    })
  },

  // 健康检查
  checkHealth() {
    return projectHttp({
      url: '/v1/rag/health',
      method: 'get'
    })
  }
}