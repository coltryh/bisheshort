<template>
  <div class="user-manage-container">
    <div class="page-header">
      <h2>用户管理</h2>
    </div>

    <el-card class="table-card">
      <el-table :data="userList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'info'">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="mail" label="邮箱" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" :disabled="row.role === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑用户" width="500px">
      <el-form :model="currentUser" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="currentUser.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="currentUser.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="currentUser.role">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="currentUser.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="currentUser.mail" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import allinoneAPI from '@/api/modules/allinone.js'

const loading = ref(false)
const userList = ref([])
const dialogVisible = ref(false)
const currentUser = ref({})

const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await allinoneAPI.listUsers()
    if (res.data.code === '0') {
      userList.value = res.data.data || []
    } else {
      ElMessage.error(res.data.message || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleEdit = (row) => {
  currentUser.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    const res = await allinoneAPI.updateUser(currentUser.value)
    if (res.data.code === '0') {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      fetchUserList()
    } else {
      ElMessage.error(res.data.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await allinoneAPI.deleteUser(row.id)
    if (res.data.code === '0') {
      ElMessage.success('删除成功')
      fetchUserList()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleString()
}

onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.user-manage-container {
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
