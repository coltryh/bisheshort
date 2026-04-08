<template>
  <div class="recycle-bin-container">
    <div class="page-header">
      <h2>回收站</h2>
    </div>

    <el-card class="table-card">
      <el-table :data="tableData" stripe style="width: 100%">
        <el-table-column prop="fullShortUrl" label="短链接" width="200" />
        <el-table-column prop="originUrl" label="原始链接" min-width="300" show-overflow-tooltip />
        <el-table-column prop="gid" label="分组ID" width="120" />
        <el-table-column prop="delTime" label="删除时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.delTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleRecover(row.id)">恢复</el-button>
            <el-button type="danger" link size="small" @click="handleRemove(row.id)">永久删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!tableData.length" description="回收站为空" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import allinoneAPI from '@/api/modules/allinone.js'

const tableData = ref([])

const fetchRecycleBin = async () => {
  try {
    const res = await allinoneAPI.listRecycleBin()
    if (res.data.code === '0') {
      tableData.value = res.data.data || []
    } else {
      ElMessage.error(res.data.message || '获取回收站失败')
    }
  } catch (error) {
    console.error('获取回收站失败', error)
    ElMessage.error('获取回收站失败')
  }
}

const handleRecover = async (id) => {
  try {
    const res = await allinoneAPI.recoverLink(id)
    if (res.data.code === '0') {
      ElMessage.success('恢复成功')
      fetchRecycleBin()
    } else {
      ElMessage.error(res.data.message || '恢复失败')
    }
  } catch (error) {
    console.error('恢复失败', error)
    ElMessage.error('恢复失败')
  }
}

const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('确定要永久删除吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await allinoneAPI.removeLink(id)
    if (res.data.code === '0') {
      ElMessage.success('删除成功')
      fetchRecycleBin()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  }
}

const formatTime = (timestamp) => {
  if (!timestamp) return '-'
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  fetchRecycleBin()
})
</script>

<style scoped>
.recycle-bin-container {
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

.table-card {
  margin-top: 10px;
}
</style>
