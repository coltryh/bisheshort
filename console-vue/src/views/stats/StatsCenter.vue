<template>
  <div class="stats-center-container">
    <div class="page-header">
      <h2>图表中心</h2>
    </div>

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
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import allinoneAPI from '@/api/modules/allinone.js'

const loading = ref(false)
const statsData = ref({})
const pvChartRef = ref(null)
const uvChartRef = ref(null)
let pvChart = null
let uvChart = null

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

const initCharts = () => {
  if (pvChartRef.value) {
    pvChart = echarts.init(pvChartRef.value)
  }
  if (uvChartRef.value) {
    uvChart = echarts.init(uvChartRef.value)
  }
}

const handleResize = () => {
  pvChart?.resize()
  uvChart?.resize()
}

onMounted(() => {
  initCharts()
  fetchStats()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pvChart?.dispose()
  uvChart?.dispose()
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
</style>
