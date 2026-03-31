<template>
  <div class="ai-chat-container">
    <!-- 左侧对话列表 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h3>AI 助手</h3>
        <el-button type="primary" size="small" @click="createNewChat">
          <el-icon><Plus /></el-icon>
          新对话
        </el-button>
      </div>

      <div class="conversation-list">
        <div
          v-for="conv in conversations"
          :key="conv.id"
          :class="['conversation-item', { active: currentConversationId === conv.id }]"
          @click="selectConversation(conv)"
        >
          <div class="conv-info">
            <span class="conv-title">{{ conv.title }}</span>
            <span class="conv-time">{{ formatTime(conv.updatedTime) }}</span>
          </div>
          <el-dropdown trigger="click" @command="handleCommand($event, conv)">
            <el-icon class="more-icon"><MoreFilled /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="rename">重命名</el-dropdown-item>
                <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div v-if="conversations.length === 0" class="empty-list">
          <el-icon :size="32"><ChatDotRound /></el-icon>
          <span>暂无对话记录</span>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-area">
      <!-- 欢迎页 -->
      <div v-if="!currentConversationId" class="welcome-page">
        <div class="welcome-content">
          <div class="welcome-icon">
            <el-icon :size="64"><ChatDotSquare /></el-icon>
          </div>
          <h2>智能客服助手</h2>
          <p>基于 RAG 知识库的 AI 智能问答系统</p>
          <div class="quick-questions">
            <div class="quick-title">快捷问题：</div>
            <el-button
              v-for="q in quickQuestions"
              :key="q"
              text
              @click="sendQuickQuestion(q)"
            >
              {{ q }}
            </el-button>
          </div>
        </div>
      </div>

      <!-- 聊天内容 -->
      <div v-else class="chat-content">
        <div class="messages" ref="messagesRef">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.role]"
          >
            <div class="message-avatar">
              <el-icon v-if="msg.role === 'user'" :size="20"><User /></el-icon>
              <el-icon v-else :size="20"><MagicStick /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(msg.content)"></div>
              <div class="message-time">{{ formatTime(msg.createdTime) }}</div>
            </div>
          </div>

          <!-- 加载中 -->
          <div v-if="loading" class="message assistant loading">
            <div class="message-avatar">
              <el-icon :size="20"><MagicStick /></el-icon>
            </div>
            <div class="message-content">
              <div class="loading-dots">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-area">
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="2"
            placeholder="输入您的问题..."
            resize="none"
            @keydown.enter.ctrl="sendMessage"
          />
          <el-button type="primary" :loading="loading" @click="sendMessage">
            发送
            <el-icon class="ml-2"><Promotion /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 重命名对话框 -->
    <el-dialog v-model="renameDialogVisible" title="重命名对话" width="400px">
      <el-input v-model="newTitle" placeholder="输入新标题" />
      <template #footer>
        <el-button @click="renameDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRename">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  MoreFilled,
  ChatDotRound,
  ChatDotSquare,
  User,
  MagicStick,
  Promotion
} from '@element-plus/icons-vue'
import aiApi from '@/api/modules/ai'

// 状态
const conversations = ref([])
const currentConversationId = ref(null)
const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const messagesRef = ref(null)

// 重命名
const renameDialogVisible = ref(false)
const newTitle = ref('')
const currentRenameConv = ref(null)

// 快捷问题
const quickQuestions = [
  '如何创建短链接？',
  '如何设置链接有效期？',
  '链接打不开了怎么办？',
  '如何查看访问统计？'
]

// 加载对话列表
const loadConversations = async () => {
  try {
    const res = await aiApi.getConversations()
    if (res.data) {
      conversations.value = res.data
    }
  } catch (error) {
    console.error('加载对话列表失败', error)
  }
}

// 创建新对话
const createNewChat = async () => {
  try {
    const res = await aiApi.createConversation()
    if (res.data) {
      conversations.value.unshift(res.data)
      selectConversation(res.data)
    }
  } catch (error) {
    console.error('创建对话失败', error)
  }
}

// 选择对话
const selectConversation = async (conv) => {
  currentConversationId.value = conv.id
  messages.value = []

  try {
    const res = await aiApi.getMessages(conv.id)
    if (res.data) {
      messages.value = res.data
      scrollToBottom()
    }
  } catch (error) {
    console.error('加载消息失败', error)
  }
}

// 发送消息
const sendMessage = async () => {
  const content = inputText.value.trim()
  if (!content || loading.value) return

  // 如果没有选中对话，自动创建
  if (!currentConversationId.value) {
    await createNewChat()
  }

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: content,
    createdTime: new Date()
  })

  inputText.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await aiApi.sendMessage(currentConversationId.value, content)
    if (res.data) {
      // 添加 AI 回复
      messages.value.push({
        role: 'assistant',
        content: res.data.content,
        createdTime: new Date()
      })

      // 更新对话标题（如果是第一条消息）
      if (messages.value.length === 2) {
        const conv = conversations.value.find(c => c.id === currentConversationId.value)
        if (conv) {
          conv.title = content.slice(0, 20) + (content.length > 20 ? '...' : '')
        }
      }

      scrollToBottom()
    }
  } catch (error) {
    ElMessage.error('发送消息失败')
    // 移除用户消息
    messages.value.pop()
  } finally {
    loading.value = false
  }
}

// 发送快捷问题
const sendQuickQuestion = async (question) => {
  if (!currentConversationId.value) {
    await createNewChat()
  }
  inputText.value = question
  await sendMessage()
}

// 处理下拉菜单命令
const handleCommand = async (command, conv) => {
  if (command === 'rename') {
    currentRenameConv.value = conv
    newTitle.value = conv.title
    renameDialogVisible.value = true
  } else if (command === 'delete') {
    try {
      await ElMessageBox.confirm('确定要删除这个对话吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      await aiApi.deleteConversation(conv.id)
      conversations.value = conversations.value.filter(c => c.id !== conv.id)

      if (currentConversationId.value === conv.id) {
        currentConversationId.value = null
        messages.value = []
      }

      ElMessage.success('删除成功')
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
      }
    }
  }
}

// 确认重命名
const confirmRename = async () => {
  if (!newTitle.value.trim()) {
    ElMessage.warning('标题不能为空')
    return
  }

  try {
    await aiApi.renameConversation(currentRenameConv.value.id, newTitle.value)
    currentRenameConv.value.title = newTitle.value
    renameDialogVisible.value = false
    ElMessage.success('重命名成功')
  } catch (error) {
    ElMessage.error('重命名失败')
  }
}

// 格式化消息（支持换行）
const formatMessage = (content) => {
  return content.replace(/\n/g, '<br>').replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'

  return date.toLocaleDateString()
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

// 初始化
onMounted(() => {
  loadConversations()
})
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  height: calc(100vh - 60px);
  background: #f5f7fa;
}

/* 侧边栏 */
.sidebar {
  width: 280px;
  background: white;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.conversation-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.conversation-item {
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  transition: all 0.2s;
}

.conversation-item:hover {
  background: #f5f7fa;
}

.conversation-item.active {
  background: #ecfdf5;
  color: #059669;
}

.conv-info {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.conv-title {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conv-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.more-icon {
  opacity: 0;
  transition: opacity 0.2s;
}

.conversation-item:hover .more-icon {
  opacity: 1;
}

.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
  gap: 8px;
}

/* 聊天区域 */
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 欢迎页 */
.welcome-page {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.welcome-content {
  text-align: center;
}

.welcome-icon {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #059669, #10b981);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 24px;
}

.welcome-content h2 {
  margin: 0 0 8px;
  color: #303133;
}

.welcome-content p {
  color: #909399;
  margin-bottom: 32px;
}

.quick-questions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.quick-title {
  color: #606266;
  margin-bottom: 8px;
}

/* 聊天内容 */
.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
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
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: #409eff;
  color: white;
}

.message.assistant .message-avatar {
  background: linear-gradient(135deg, #059669, #10b981);
  color: white;
}

.message-content {
  max-width: 70%;
}

.message.user .message-content {
  text-align: right;
}

.message-text {
  background: white;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.message.user .message-text {
  background: #409eff;
  color: white;
}

.message.assistant .message-text {
  background: white;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 加载动画 */
.loading-dots {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  background: #059669;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 输入区域 */
.input-area {
  padding: 16px;
  background: white;
  border-top: 1px solid #e4e7ed;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-area :deep(.el-textarea__inner) {
  border-radius: 8px;
}

.input-area .el-button {
  height: 64px;
  border-radius: 8px;
}
</style>
