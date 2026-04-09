<template>
  <div class="ai-assistant">
    <!-- AI浮动按钮 -->
    <el-button
      class="ai-float-btn"
      @click="dialogVisible = true"
      title="AI智能分析"
    >
      <el-icon size="24"><ChatDotRound /></el-icon>
    </el-button>

    <!-- AI对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="AI智能分析"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="ai-chat-container">
        <!-- 对话内容 -->
        <div class="chat-messages" ref="chatContainer">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.role]"
          >
            <div class="message-avatar">
              <el-icon v-if="msg.role === 'assistant'" size="20"><ChatDotRound /></el-icon>
              <el-icon v-else size="20"><User /></el-icon>
            </div>
            <div class="message-content markdown-body" v-html="renderMarkdown(msg.content)"></div>
          </div>
          <div v-if="loading" class="message assistant">
            <div class="message-avatar">
              <el-icon size="20"><ChatDotRound /></el-icon>
            </div>
            <div class="message-content loading">
              <span>思考中...</span>
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions">
          <el-button size="small" @click="sendQuickAction('分析访问趋势')">分析访问趋势</el-button>
          <el-button size="small" @click="sendQuickAction('给出优化建议')">优化建议</el-button>
          <el-button size="small" @click="sendQuickAction('分析用户画像')">用户画像</el-button>
        </div>

        <!-- 输入框 -->
        <div class="input-area">
          <el-input
            v-model="userInput"
            placeholder="请输入您的问题..."
            @keyup.enter="sendMessage"
            :disabled="loading"
          />
          <el-button type="primary" @click="sendMessage" :disabled="loading || !userInput.trim()">
            发送
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'
import { ChatDotRound, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import API from '@/api'

const dialogVisible = ref(false)
const messages = ref([
  {
    role: 'assistant',
    content: '您好！我是短链接AI智能分析助手，可以帮您分析短链接的访问数据、解答运营问题。请问有什么可以帮您？'
  }
])
const userInput = ref('')
const loading = ref(false)
const chatContainer = ref(null)

// Props接收统计数据
const props = defineProps({
  statsData: {
    type: Object,
    default: () => ({})
  },
  shortLinkInfo: {
    type: Object,
    default: () => ({})
  }
})

// 内部状态
const internalStatsData = ref({})
const internalShortLinkInfo = ref({})

// 监听外部props变化
watch(() => props.statsData, (newVal) => {
  internalStatsData.value = newVal || {}
}, { immediate: true })

watch(() => props.shortLinkInfo, (newVal) => {
  internalShortLinkInfo.value = newVal || {}
}, { immediate: true })

// 发送消息
const sendMessage = async () => {
  if (!userInput.value.trim() || loading.value) return

  const userMsg = { role: 'user', content: userInput.value }
  messages.value.push(userMsg)
  userInput.value = ''
  loading.value = true

  try {
    // 准备上下文
    let context = ''
    if (internalStatsData.value && Object.keys(internalStatsData.value).length > 0) {
      context = '短链接访问数据：' + JSON.stringify(internalStatsData.value)
    }
    if (internalShortLinkInfo.value && internalShortLinkInfo.value.fullShortUrl) {
      context += '\n短链接信息：' + JSON.stringify(internalShortLinkInfo.value)
    }

    const response = await API.ai.chat({
      question: userMsg.content,
      context: context
    })

    // 解析响应
    const result = response.data
    if (result.code === '0' || result.code === 0) {
      messages.value.push({ role: 'assistant', content: result.data })
    } else {
      ElMessage.error(result.message || 'AI服务异常')
      messages.value.push({
        role: 'assistant',
        content: '抱歉，AI服务暂时不可用，请稍后再试。'
      })
    }
  } catch (error) {
    console.error('AI请求失败:', error)
    ElMessage.error('AI服务调用失败，请检查网络')
    messages.value.push({
      role: 'assistant',
      content: '抱歉，AI服务暂时不可用，请稍后再试。'
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

// 快捷操作
const sendQuickAction = async (action) => {
  userInput.value = action
  await sendMessage()
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

// 监听对话框打开
watch(() => dialogVisible.value, (val) => {
  if (val) {
    nextTick(() => scrollToBottom())
  }
})

// 暴露方法给父组件调用
const openDialog = (statsData, shortLinkInfo) => {
  dialogVisible.value = true
  // 重置消息
  messages.value = [
    {
      role: 'assistant',
      content: '您好！我是短链接AI智能分析助手，可以帮您分析短链接的访问数据、解答运营问题。请问有什么可以帮您？'
    }
  ]
  // 设置数据
  if (statsData) {
    internalStatsData.value = statsData
  }
  if (shortLinkInfo) {
    internalShortLinkInfo.value = shortLinkInfo
  }
}

defineExpose({
  openDialog
})

const renderMarkdown = (text) => {
  if (!text) return ''
  return marked(text)
}
</script>

<style scoped>
.ai-assistant {
  position: relative;
}

/* Markdown 内容样式 */
.markdown-body {
  font-size: 14px;
  line-height: 1.6;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4) {
  margin: 8px 0 6px 0;
  font-weight: 600;
  color: #2c3e50;
  font-size: 15px;
}

.markdown-body :deep(h1) {
  font-size: 17px;
}

.markdown-body :deep(h2) {
  font-size: 16px;
}

.markdown-body :deep(h3) {
  font-size: 15px;
}

.markdown-body :deep(p) {
  margin: 8px 0;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  margin: 8px 0;
  padding-left: 24px;
}

.markdown-body :deep(li) {
  margin: 4px 0;
}

.markdown-body :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.9em;
  color: #e74c3c;
}

.markdown-body :deep(pre) {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
}

.markdown-body :deep(pre code) {
  background: none;
  padding: 0;
  color: inherit;
}

.markdown-body :deep(table) {
  border-collapse: collapse;
  margin: 8px 0;
  width: 100%;
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.markdown-body :deep(th) {
  background: #f5f5f5;
  font-weight: 600;
}

.markdown-body :deep(strong) {
  font-weight: 600;
  color: #2c3e50;
}

.markdown-body :deep(blockquote) {
  margin: 8px 0;
  padding: 8px 12px;
  border-left: 4px solid #3498db;
  background: #f9f9f9;
  color: #555;
}

.ai-float-btn {
  position: fixed;
  right: 20px;
  bottom: 100px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #059669, #10b981);
  border: none;
  color: white;
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.4);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-float-btn:hover {
  background: linear-gradient(135deg, #047857, #059669);
  transform: scale(1.05);
}

.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: 400px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 10px;
}

.message {
  display: flex;
  margin-bottom: 12px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: #059669;
  color: white;
  margin-right: 0;
  margin-left: 8px;
}

.message-content {
  max-width: 70%;
  padding: 8px 12px;
  border-radius: 8px;
  background: white;
  line-height: 1.5;
}

.message.user .message-content {
  background: #059669;
  color: white;
}

.message-content.loading {
  color: #999;
}

.quick-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.input-area {
  display: flex;
  gap: 10px;
}
</style>
