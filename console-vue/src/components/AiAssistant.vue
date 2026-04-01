<template>
  <div class="ai-assistant">
    <!-- AI助手按钮 -->
    <el-button
      type="primary"
      :icon="Robot"
      circle
      size="large"
      class="ai-float-btn"
      @click="dialogVisible = true"
      title="AI智能分析"
    ></el-button>

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
              <el-icon v-if="msg.role === 'user'"><User /></el-icon>
              <el-icon v-else><Robot /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(msg.content)"></div>
            </div>
          </div>
          <!-- 加载中 -->
          <div v-if="loading" class="message assistant loading">
            <div class="message-avatar">
              <el-icon><Robot /></el-icon>
            </div>
            <div class="message-content">
              <span class="loading-text">AI正在分析中...</span>
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions" v-if="messages.length <= 1">
          <el-button
            v-for="action in quickActions"
            :key="action.label"
            size="small"
            @click="handleQuickAction(action)"
          >
            {{ action.label }}
          </el-button>
        </div>

        <!-- 输入区域 -->
        <div class="input-area">
          <el-input
            v-model="userInput"
            placeholder="请输入您的问题..."
            :disabled="loading"
            @keyup.enter="sendMessage"
          >
            <template #append>
              <el-button :icon="Promotion" @click="sendMessage" :loading="loading" />
            </template>
          </el-input>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, watch } from 'vue'
import { ChatDotRound, Promotion, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import API from '@/api'

const Robot = ChatDotRound

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

// 快捷操作
const quickActions = [
  {
    label: '分析访问趋势',
    prompt: '请分析以下短链接的访问趋势数据：'
  },
  {
    label: '优化建议',
    prompt: '基于以下数据，请给出短链接运营优化建议：'
  },
  {
    label: '用户画像',
    prompt: '请根据以下数据，分析访问用户画像：'
  }
]

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
    console.log('AI调试 - statsData:', internalStatsData.value)
    console.log('AI调试 - shortLinkInfo:', internalShortLinkInfo.value)
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

    // 解析响应：axios响应的结构是 response.data = {code, data, success}
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
    messages.value.push({
      role: 'assistant',
      content: '抱歉，AI服务调用失败，请检查网络后重试。'
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

// 快捷操作
const handleQuickAction = (action) => {
  userInput.value = action.prompt + '\n\n' + JSON.stringify(props.statsData, null, 2)
  sendMessage()
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

// 格式化消息（简单换行处理）
const formatMessage = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
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
</script>

<script>
export default {
  name: 'AiAssistant'
}
</script>

<style scoped>
.ai-assistant {
  position: relative;
}

.ai-float-btn {
  position: fixed;
  right: 30px;
  bottom: 100px;
  width: 60px;
  height: 60px;
  font-size: 28px;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.ai-float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.ai-chat-container {
  height: 400px;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 10px;
}

.message {
  display: flex;
  margin-bottom: 15px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 10px;
}

.message.user .message-avatar {
  background: #409eff;
  color: white;
}

.message.assistant .message-avatar {
  background: #67c23a;
  color: white;
}

.message-content {
  max-width: 80%;
  padding: 10px 14px;
  border-radius: 8px;
  word-break: break-word;
}

.message.user .message-content {
  background: #409eff;
  color: white;
}

.message.assistant .message-content {
  background: white;
  border: 1px solid #e4e7ed;
}

.message-text {
  line-height: 1.6;
  font-size: 14px;
}

.loading-text {
  color: #909399;
  font-style: italic;
}

.quick-actions {
  padding: 10px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.input-area {
  display: flex;
  gap: 10px;
}
</style>
