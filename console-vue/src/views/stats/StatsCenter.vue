<template>
  <div class="stats-center-container">
    <div class="page-header">
      <h2>图表中心</h2>
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

    <!-- 全局统计视图 -->
    <div v-if="!selectedShortLink">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-label">总 PV</div>
              <div class="stat-value">{{ statsData.totalPv || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-label">总 UV</div>
              <div class="stat-value">{{ statsData.totalUv || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-label">总 IP</div>
              <div class="stat-value">{{ statsData.totalUip || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-label">链接数</div>
              <div class="stat-value">{{ statsData.linkCount || 0 }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>访问量统计</span>
            </template>
            <div ref="pvChartRef" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>UV vs IP 统计</span>
            </template>
            <div ref="uvChartRef" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 单个短链接详细图表视图 -->
    <div v-else class="detail-charts">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="访问数据" name="accessData">
          <div class="content-box scroll-box" style="height: calc(100vh - 280px); overflow: scroll">
            <!-- 访问曲线 -->
            <TitleContent class="chart-item" style="width: 800px" title="访问曲线" @onMounted="initLineChart">
              <template #titleButton>
                <el-button @click="isLineChart = !isLineChart">切换为{{ isLineChart ? '表格' : '曲线' }}</el-button>
              </template>
              <template #content>
                <div class="list-chart">
                  <div v-show="isLineChart" class="top10" style="padding-top: 20px">
                    <div class="key-value" style="margin-top: 10px">
                      <span>访问次数</span>
                      <span>{{ detailStats.totalPv || 0 }}</span>
                    </div>
                    <div class="key-value" style="margin-top: 10px">
                      <span>访问人数</span>
                      <span>{{ detailStats.totalUv || 0 }}</span>
                    </div>
                    <div class="key-value" style="margin-top: 10px">
                      <span>访问IP数</span>
                      <span>{{ detailStats.totalUip || 0 }}</span>
                    </div>
                  </div>
                  <div v-show="isLineChart" ref="lineChartRef" class="lineChart"></div>
                  <div v-show="!isLineChart" style="padding: 20px">
                    <el-table :data="visitsData" border style="width: 100%; height: 210px; overflow: scroll"
                      :header-cell-style="{ background: '#eef1f6', color: '#606266' }">
                      <el-table-column prop="date" label="时间" width="160" />
                      <el-table-column prop="pv" label="访问次数" width="160" />
                      <el-table-column prop="uv" label="访问人数" width="160" />
                      <el-table-column prop="uip" label="访问IP数" width="160" />
                    </el-table>
                  </div>
                </div>
              </template>
            </TitleContent>

            <!-- 24小时分布 -->
            <TitleContent class="chart-item" title="24小时分布" style="width: 800px">
              <template #content>
                <BarChart style="height: 100%; width: 100%" :chartData="{
                  xAxis: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23],
                  value: detailStats.hourStats || new Array(24).fill(0)
                }"></BarChart>
              </template>
            </TitleContent>

            <!-- 一周分布 -->
            <TitleContent class="chart-item" title="一周分布" style="width: 800px">
              <template #content>
                <BarChart style="height: 100%; width: 100%" :chartData="{
                  xAxis: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
                  value: detailStats.weekdayStats || new Array(7).fill(0)
                }"></BarChart>
              </template>
            </TitleContent>

            <!-- 高频访问IP -->
            <TitleContent class="chart-item" title="高频访问IP" style="width: 390px">
              <template #content>
                <KeyValue :dataLists="detailStats.topIpStats" style="height: 100%; width: 100%"></KeyValue>
              </template>
            </TitleContent>

            <!-- 操作系统 -->
            <TitleContent class="chart-item" title="操作系统" style="width: 390px">
              <template #content>
                <ProgressLine style="height: 100%; width: 100%" :dataLists="detailStats.osStats"></ProgressLine>
              </template>
            </TitleContent>

            <!-- 访问浏览器 -->
            <TitleContent class="chart-item" title="访问浏览器" style="width: 390px">
              <template #content>
                <ProgressLine style="height: 100%; width: 100%" :dataLists="detailStats.browserStats"></ProgressLine>
              </template>
            </TitleContent>

            <!-- 访客类型 -->
            <TitleContent class="chart-item" title="访客类型" style="width: 390px">
              <template #content>
                <ProgressPie style="height: 100%; width: 100%" :labels="['新访客', '旧访客']" :data="userTypeList"></ProgressPie>
              </template>
            </TitleContent>

            <!-- 访问网络 -->
            <TitleContent class="chart-item" title="访问网络" style="width: 390px">
              <template #content>
                <ProgressPie style="height: 100%; width: 100%" :labels="['WIFI', '移动数据']" :data="netWorkList"></ProgressPie>
              </template>
            </TitleContent>

            <!-- 访问设备 -->
            <TitleContent class="chart-item" title="访问设备" style="width: 390px">
              <template #content>
                <ProgressPie style="height: 100%; width: 100%" :labels="['电脑', '移动设备']" :data="deviceList"></ProgressPie>
              </template>
            </TitleContent>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import allinoneAPI from '@/api/modules/allinone.js'
import smallLinkPage from '@/api/modules/smallLinkPage.js'
import TitleContent from '@/views/mySpace/components/chartsInfo/TitleContent.vue'
import BarChart from '@/views/mySpace/components/chartsInfo/BarChart.vue'
import KeyValue from '@/views/mySpace/components/chartsInfo/KeyValue.vue'
import ProgressLine from '@/views/mySpace/components/chartsInfo/ProgressLine.vue'
import ProgressPie from '@/views/mySpace/components/chartsInfo/ProgressPie.vue'

const loading = ref(false)
const statsData = ref({})
const pvChartRef = ref(null)
const uvChartRef = ref(null)
let pvChart = null
let uvChart = null

// 分组和短链接选择相关
const groupList = ref([])
const shortLinkList = ref([])
const filteredShortLinks = ref([])
const selectedGroup = ref(null)
const selectedShortLink = ref(null)
const dateRange = ref([])
const activeTab = ref('accessData')
const isLineChart = ref(true)
const lineChartRef = ref(null)
let lineChart = null

// 详细统计数据
const detailStats = ref({})
const visitsData = ref([])

// 设备/网络/访客类型数据
const deviceList = ref([0, 0])
const netWorkList = ref([0, 0])
const userTypeList = ref([0, 0])

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

// 短链接选择变化
const onShortLinkChange = (val) => {
  if (val) {
    fetchDetailStats(val)
  } else {
    detailStats.value = {}
  }
}

// 日期范围变化
const onDateChange = () => {
  if (selectedShortLink.value) {
    fetchDetailStats(selectedShortLink.value)
  }
}

// 获取全局统计
const fetchStats = async () => {
  loading.value = true
  try {
    const res = await allinoneAPI.statsOverview()
    if (res.data.code === '0') {
      statsData.value = res.data.data || {}
      updateCharts()
    } else {
      ElMessage.error(res.data.message || '获取统计数据失败')
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

// 获取单个短链接详细统计
const fetchDetailStats = async (fullShortUrl) => {
  if (!fullShortUrl) return

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
      processDetailData()
      // 确保 DOM 更新后再初始化图表
      nextTick(() => {
        if (lineChartRef.value && !lineChart) {
          initLineChart()
        } else if (lineChart) {
          updateLineChart()
        }
      })
    } else {
      ElMessage.error(res.data?.message || '获取详细统计数据失败')
    }
  } catch (error) {
    ElMessage.error('获取详细统计数据失败')
  } finally {
    loading.value = false
  }
}

// 处理详细数据
const processDetailData = () => {
  // 处理访客类型数据
  userTypeList.value = [0, 0]
  detailStats.value?.uvTypeStats?.forEach((item) => {
    if (item.uvType === 'newUser') {
      userTypeList.value[0] = item.cnt
    } else if (item.uvType === 'oldUser') {
      userTypeList.value[1] = item.cnt
    }
  })

  // 处理访问设备数据
  deviceList.value = [0, 0]
  detailStats.value?.deviceStats?.forEach((item) => {
    if (item.device === 'Mobile') {
      deviceList.value[1] = item.cnt
    } else {
      deviceList.value[0] = item.cnt
    }
  })

  // 处理访问网络数据
  netWorkList.value = [0, 0]
  detailStats.value?.networkStats?.forEach((item) => {
    if (item.device === 'Mobile') {
      netWorkList.value[1] = item.cnt
    } else {
      netWorkList.value[0] = item.cnt
    }
  })

  // 处理访问曲线表格数据
  visitsData.value = detailStats.value?.daily || []
}

// 更新全局图表
const updateCharts = () => {
  if (pvChart) {
    pvChart.setOption({
      xAxis: {
        type: 'category',
        data: ['PV', 'UV', 'UIP']
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        data: [
          { value: statsData.value.totalPv || 0, itemStyle: { color: '#5470c6' } },
          { value: statsData.value.totalUv || 0, itemStyle: { color: '#91cc75' } },
          { value: statsData.value.totalUip || 0, itemStyle: { color: '#fac858' } }
        ],
        type: 'bar'
      }]
    })
  }

  if (uvChart) {
    uvChart.setOption({
      tooltip: {
        trigger: 'item'
      },
      series: [{
        name: '访问统计',
        type: 'pie',
        radius: '60%',
        data: [
          { value: statsData.value.totalUv || 0, name: 'UV (独立访客)' },
          { value: statsData.value.totalUip || 0, name: 'UIP (独立IP)' }
        ]
      }]
    })
  }
}

// 初始化全局图表
const initCharts = () => {
  if (pvChartRef.value) {
    pvChart = echarts.init(pvChartRef.value)
  }
  if (uvChartRef.value) {
    uvChart = echarts.init(uvChartRef.value)
  }
}

// 初始化详细图表中的折线图
const initLineChart = () => {
  if (lineChartRef.value) {
    lineChart = echarts.init(lineChartRef.value)
    updateLineChart()
  }
}

// 更新折线图
const updateLineChart = () => {
  if (!lineChart) return

  const dailyData = detailStats.value?.daily || []
  const xAxisData = []
  const pvData = []
  const uvData = []
  const uipData = []

  dailyData.forEach((item) => {
    const date = item.date || ''
    xAxisData.push(date.split('-')[1] + '月' + date.split('-')[2] + '日')
    pvData.push(item.pv || 0)
    uvData.push(item.uv || 0)
    uipData.push(item.uip || 0)
  })

  lineChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['访问次数', '访问人数', '访问IP数']
    },
    grid: {
      left: '3%',
      right: '9%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxisData
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '访问次数',
        type: 'line',
        data: pvData
      },
      {
        name: '访问人数',
        type: 'line',
        data: uvData
      },
      {
        name: '访问IP数',
        type: 'line',
        data: uipData
      }
    ]
  })
}

const handleResize = () => {
  pvChart?.resize()
  uvChart?.resize()
  lineChart?.resize()
}

// 监听 detailStats 变化，更新折线图
watch(() => detailStats.value.daily, (newDaily) => {
  if (newDaily && newDaily.length > 0) {
    updateLineChart()
  }
}, { deep: true })

onMounted(() => {
  initCharts()
  fetchStats()
  fetchGroupList()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pvChart?.dispose()
  uvChart?.dispose()
  lineChart?.dispose()
})
</script>

<style scoped>
.stats-center-container {
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

.stat-card {
  text-align: center;
}

.stat-item {
  padding: 10px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.detail-charts {
  margin-top: 20px;
}

.content-box {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  width: 100%;
}

.chart-item {
  height: 300px;
  min-width: 300px;
  margin: 10px;
}

.list-chart {
  display: flex;
  justify-content: space-between;
}

.top10 {
  padding: 15px 30px;
  width: 400px;
  height: 270px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.top-item {
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
}

.key-value {
  display: flex;
  justify-content: space-between;
  width: 150px;
}

.lineChart {
  margin: 10px;
  width: 600px;
  height: 200px;
}

.scroll-box {
  overflow-y: auto;
}
</style>
