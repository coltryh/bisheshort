import http from '../axios'

export default {
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
  }
}
