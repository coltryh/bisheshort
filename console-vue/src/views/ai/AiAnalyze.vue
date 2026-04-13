<template>
  <div class="ai-analyze-container">
    <div class="page-header">
      <h2>AI 分析</h2>
    </div>

    <!-- 分组和短链接级联选择器 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-select v-model="selectedGroup" placeholder="请选择分组" clearable filterable @change="onGroupChange">
          <el-option
            v-for="item in groupList"
            :key="item.gid"
            :label="item.name + ' (' + (item.shortLinkCount || 0) + ')'"
            :value="item.gid"
          />
        </el-select>
      </el-col>
      <el-col :span="8">
        <el-select
          v-model="selectedShortLink"
          placeholder="请选择短链接"
          clearable
          filterable
          :filter-method="filterShortLinks"
          @change="onShortLinkChange"
          :disabled="!selectedGroup"
        >
          <el-option
            v-for="item in filteredShortLinks"
            :key="item.fullShortUrl"
            :label="item.describe + ' - ' + item.fullShortUrl"
            :value="item.fullShortUrl"
          />
        </el-select>
      </el-col>
      <el-col :span="6">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          :shortcuts="shortcuts"
          @change="onDateChange"
        />
      </el-col>
    </el-row>

    <!-- 数据概览 -->
    <el-card class="stats-summary-card">
      <template #header>
        <span>数据概览 {{ selectedShortLink ? '- ' + selectedShortLink : '(全部链接)' }}</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">总访问量 (PV)</div>
            <div class="stat-value">{{ currentStats.totalPv || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">独立访客 (UV)</div>
            <div class="stat-value">{{ currentStats.totalUv || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">独立IP (UIP)</div>
            <div class="stat-value">{{ currentStats.totalUip || 0 }}</div>
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

    <!-- 选中短链接的详细统计 -->
    <el-card v-if="selectedShortLink && detailStats" class="detail-stats-card" style="margin-top: 20px;">
      <template #header>
        <span>详细统计</span>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="访问设备">
          <div class="device-stats">
            <span>电脑: {{ deviceStats.pc }}</span>
            <span>移动设备: {{ deviceStats.mobile }}</span>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="访问网络">
          <div class="device-stats">
            <span>WIFI: {{ networkStats.wifi }}</span>
            <span>移动数据: {{ networkStats.mobile }}</span>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="访客类型">
          <div class="device-stats">
            <span>新访客: {{ visitorStats.newVisitor }}</span>
            <span>旧访客: {{ visitorStats.oldVisitor }}</span>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="操作系统" :span="3">
          {{ osStats }}
        </el-descriptions-item>
        <el-descriptions-item label="浏览器" :span="3">
          {{ browserStats }}
        </el-descriptions-item>
      </el-descriptions>
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
import { ref, onMounted, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import allinoneAPI from '@/api/modules/allinone.js'
import smallLinkPage from '@/api/modules/smallLinkPage.js'

const loading = ref(false)
const chatInput = ref('')
const chatMessages = ref([])
const chatContainerRef = ref(null)
const analysisData = ref({})

// 分组和短链接选择相关
const groupList = ref([])
const shortLinkList = ref([])
const filteredShortLinks = ref([])
const selectedGroup = ref(null)
const selectedShortLink = ref(null)
const dateRange = ref([])
const detailStats = ref(null)

const currentStats = computed(() => {
  if (selectedShortLink.value && detailStats.value) {
    return {
      totalPv: detailStats.value.totalPv || 0,
      totalUv: detailStats.value.totalUv || 0,
      totalUip: detailStats.value.totalUip || 0
    }
  }
  return analysisData.value
})

// 详细统计数据
const deviceStats = computed(() => {
  if (!detailStats.value?.deviceStats) return { pc: 0, mobile: 0 }
  let pc = 0, mobile = 0
  detailStats.value.deviceStats.forEach(item => {
    if (item.device === 'Mobile') mobile = item.cnt
    else pc = item.cnt
  })
  return { pc, mobile }
})

const networkStats = computed(() => {
  if (!detailStats.value?.networkStats) return { wifi: 0, mobile: 0 }
  let wifi = 0, mobile = 0
  detailStats.value.networkStats.forEach(item => {
    if (item.device === 'Mobile') mobile = item.cnt
    else wifi = item.cnt
  })
  return { wifi, mobile }
})

const visitorStats = computed(() => {
  if (!detailStats.value?.uvTypeStats) return { newVisitor: 0, oldVisitor: 0 }
  let newVisitor = 0, oldVisitor = 0
  detailStats.value.uvTypeStats.forEach(item => {
    if (item.uvType === 'newUser') newVisitor = item.cnt
    else if (item.uvType === 'oldUser') oldVisitor = item.cnt
  })
  return { newVisitor, oldVisitor }
})

const osStats = computed(() => {
  if (!detailStats.value?.osStats?.length) return '无数据'
  return detailStats.value.osStats.map(item => `${item.os}: ${item.cnt}`).join(', ')
})

const browserStats = computed(() => {
  if (!detailStats.value?.browserStats?.length) return '无数据'
  return detailStats.value.browserStats.map(item => `${item.browser}: ${item.cnt}`).join(', ')
})

const shortcuts = [
  {
    text: '近七天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '近三十天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  }
]

// 获取分组列表
const fetchGroupList = async () => {
  try {
    const res = await allinoneAPI.listGroups()
    if (res?.data?.code === '0') {
      groupList.value = res.data?.data || []
    }
  } catch (error) {
    console.error('获取分组列表失败', error)
  }
}

// 获取短链接列表
const fetchShortLinkList = async (gid) => {
  try {
    const res = await allinoneAPI.listLinks(gid)
    if (res?.data?.code === '0') {
      shortLinkList.value = res.data?.data || []
      filteredShortLinks.value = [...shortLinkList.value]
    }
  } catch (error) {
    console.error('获取短链接列表失败', error)
  }
}

// 分组选择变化
const onGroupChange = (gid) => {
  selectedShortLink.value = null
  if (gid) {
    fetchShortLinkList(gid)
  } else {
    shortLinkList.value = []
    filteredShortLinks.value = []
  }
}

// 过滤短链接（搜索功能）
const filterShortLinks = (keyword) => {
  if (!keyword) {
    filteredShortLinks.value = [...shortLinkList.value]
  } else {
    const lowerKeyword = keyword.toLowerCase()
    filteredShortLinks.value = shortLinkList.value.filter(item =>
      item.describe?.toLowerCase().includes(lowerKeyword) ||
      item.fullShortUrl?.toLowerCase().includes(lowerKeyword) ||
      item.originUrl?.toLowerCase().includes(lowerKeyword)
    )
  }
}

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

// 获取单个短链接详细统计
const fetchDetailStats = async (fullShortUrl) => {
  if (!fullShortUrl) {
    detailStats.value = null
    return
  }

  loading.value = true
  try {
    const params = {
      gid: selectedGroup.value || '',
      fullShortUrl: fullShortUrl,
      startDate: dateRange.value?.[0] || '',
      endDate: dateRange.value?.[1] || ''
    }

    const res = await smallLinkPage.queryLinkStats(params)
    if (res?.data?.code === '0') {
      detailStats.value = res.data?.data || {}
    } else {
      ElMessage.error(res.data?.message || '获取详细统计数据失败')
    }
  } catch (error) {
    ElMessage.error('获取详细统计数据失败')
  } finally {
    loading.value = false
  }
}

const onShortLinkChange = (val) => {
  if (val) {
    fetchDetailStats(val)
  } else {
    detailStats.value = null
  }
}

const onDateChange = () => {
  if (selectedShortLink.value) {
    fetchDetailStats(selectedShortLink.value)
  }
}

// 构建AI上下文
const buildAiContext = () => {
  if (!selectedShortLink.value || !detailStats.value) {
    return '当前是全局数据统计。'
  }

  const stats = detailStats.value
  let context = `用户选择了短链接: ${selectedShortLink.value}\n`
  context += `统计数据:\n`
  context += `- 总访问量(PV): ${stats.totalPv || 0}\n`
  context += `- 独立访客(UV): ${stats.totalUv || 0}\n`
  context += `- 独立IP(UIP): ${stats.totalUip || 0}\n`
  context += `- 设备: 电脑 ${deviceStats.value.pc}, 移动设备 ${deviceStats.value.mobile}\n`
  context += `- 网络: WIFI ${networkStats.value.wifi}, 移动数据 ${networkStats.value.mobile}\n`
  context += `- 访客: 新访客 ${visitorStats.value.newVisitor}, 旧访客 ${visitorStats.value.oldVisitor}\n`

  if (stats.osStats?.length) {
    context += `- 操作系统: ${stats.osStats.map(item => `${item.os}: ${item.cnt}`).join(', ')}\n`
  }
  if (stats.browserStats?.length) {
    context += `- 浏览器: ${stats.browserStats.map(item => `${item.browser}: ${item.cnt}`).join(', ')}\n`
  }

  return context
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
    const context = buildAiContext()
    const res = await allinoneAPI.aiChat({
      message,
      context
    })

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
  fetchGroupList()

  chatMessages.value.push({
    role: 'assistant',
    content: '您好！我是短链接管理系统的 AI 助手。\n\n您可以选择一个分组，再选择该分组下的短链接，我会基于该链接的访问统计数据（PV、UV、设备等）为您提供更精准的分析。\n\n或者您也可以询问关于全局数据的任何问题。请问有什么可以帮您的？'
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

.detail-stats-card :deep(.el-card__header) {
  background: #f5f7fa;
}

.device-stats {
  display: flex;
  gap: 15px;
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
