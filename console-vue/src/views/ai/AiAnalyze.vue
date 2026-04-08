<template>
  <div class="ai-analyze-container">
    <div class="page-header">
      <h2>AI 分析</h2>
    </div>

    <el-card class="stats-summary-card">
      <template #header>
        <span>数据概览</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">总访问量 (PV)</div>
            <div class="stat-value">{{ analysisData.totalPv || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">独立访客 (UV)</div>
            <div class="stat-value">{{ analysisData.totalUv || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">独立IP (UIP)</div>
            <div class="stat-value">{{ analysisData.totalUip || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">短链接数</div>
            <div class="stat-value">{{ analysisData.linkCount || 0 }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="chat-card" style="margin-top: 20px;">
      <template #header>
        <span>AI 智能问答</span>
      </template>
      <div class="chat-container" ref="chatContainerRef">
        <div v-for="(msg, index) in chatMessages" :key="index" :class="['message', msg.role]">
          <div class="message-content">{{ msg.content }}</div>
        </div>
        <div v-if="loading" class="message assistant">
          <div class="message-content">AI 正在思考中...</div>
        </div>
      </div>
      <div class="chat-input-area">
        <el-input
          v-model="chatInput"
          placeholder="输入您的问题..."
          :disabled="loading"
          @keyup.enter="sendMessage"
        />
        <el-button type="primary" @click="sendMessage" :disabled="loading || !chatInput.trim()">
          发送
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import allinoneAPI from '@/api/modules/allinone.js'

const loading = ref(false)
const chatInput = ref('')
const chatMessages = ref([])
const chatContainerRef = ref(null)
const analysisData = ref({})

const fetchAnalysisData = async () => {
  try {
    const res = await allinoneAPI.aiAnalyze()
    if (res.data.code === '0') {
      analysisData.value = res.data.data || {}
    }
  } catch (error) {
    console.error('获取分析数据失败', error)
  }
}

const sendMessage = async () => {
  if (!chatInput.value.trim() || loading.value) return

  const message = chatInput.value.trim()
  chatInput.value = ''

  chatMessages.value.push({
    role: 'user',
    content: message
  })

  scrollToBottom()
  loading.value = true

  try {
    const res = await allinoneAPI.aiChat({ message })

    if (res.data.code === '0') {
      chatMessages.value.push({
        role: 'assistant',
        content: res.data.data || '抱歉，AI 暂时无法回答这个问题。'
      })
    } else {
      ElMessage.error(res.data.message || 'AI 响应失败')
      chatMessages.value.push({
        role: 'assistant',
        content: '抱歉，AI 服务暂时无法使用。'
      })
    }
  } catch (error) {
    ElMessage.error('发送消息失败')
    chatMessages.value.push({
      role: 'assistant',
      content: '抱歉，AI 服务暂时无法使用。'
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainerRef.value) {
      chatContainerRef.value.scrollTop = chatContainerRef.value.scrollHeight
    }
  })
}

onMounted(() => {
  fetchAnalysisData()

  chatMessages.value.push({
    role: 'assistant',
    content: '您好！我是短链接管理系统的 AI 助手。我可以帮您分析短链接的访问数据，或者回答关于链接统计的问题。请问有什么可以帮您的？'
  })
})
</script>

<style scoped>
.ai-analyze-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.stats-summary-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stats-summary-card :deep(.el-card__header) {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.stats-summary-card :deep(.el-card__body) {
  color: white;
}

.stat-item {
  text-align: center;
  padding: 10px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
}

.chat-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: 400px;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 10px;
}

.message {
  margin-bottom: 15px;
  display: flex;
}

.message.user {
  justify-content: flex-end;
}

.message.assistant {
  justify-content: flex-start;
}

.message-content {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 10px;
  line-height: 1.5;
  white-space: pre-wrap;
}

.message.user .message-content {
  background: #409eff;
  color: white;
}

.message.assistant .message-content {
  background: white;
  color: #333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chat-input-area {
  display: flex;
  gap: 10px;
}

.chat-input-area .el-input {
  flex: 1;
}
</style>
