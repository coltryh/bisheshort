<template>
  <div class="permission-manage-container">
    <div class="page-header">
      <h2>权限管理</h2>
    </div>

    <el-card class="table-card">
      <el-table :data="permissionList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="权限名称" width="150" />
        <el-table-column prop="code" label="权限代码" width="150">
          <template #default="{ row }">
            <el-tag type="info">{{ row.code }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
      </el-table>
    </el-card>

    <el-card class="table-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>用户权限分配</span>
        </div>
      </template>
      <el-form :inline="true" :model="userSelectForm">
        <el-form-item label="选择用户">
          <el-select v-model="userSelectForm.userId" placeholder="请选择用户" @change="handleUserChange">
            <el-option v-for="user in userList" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-table :data="permissionList" style="width: 100%" v-loading="loadingPermissions">
        <el-table-column prop="name" label="权限名称" width="150" />
        <el-table-column prop="code" label="权限代码" width="150">
          <template #default="{ row }">
            <el-tag :type="hasPermission(row.code) ? 'success' : 'info'">
              {{ hasPermission(row.code) ? '已拥有' : '未拥有' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              size="small"
              :type="hasPermission(row.code) ? 'danger' : 'success'"
              @click="togglePermission(row)"
            >
              {{ hasPermission(row.code) ? '移除' : '授予' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import allinoneAPI from '@/api/modules/allinone.js'

const loading = ref(false)
const loadingPermissions = ref(false)
const permissionList = ref([])
const userList = ref([])
const userPermissionCodes = ref([])
const userSelectForm = ref({ userId: null })

const fetchPermissionList = async () => {
  loading.value = true
  try {
    const res = await allinoneAPI.listPermissions()
    if (res.data.code === '0') {
      permissionList.value = res.data.data || []
    } else {
      ElMessage.error(res.data.message || '获取权限列表失败')
    }
  } catch (error) {
    ElMessage.error('获取权限列表失败')
  } finally {
    loading.value = false
  }
}

const fetchUserList = async () => {
  try {
    const res = await allinoneAPI.listUsers()
    if (res.data.code === '0') {
      userList.value = res.data.data || []
    }
  } catch (error) {
    console.error('获取用户列表失败', error)
  }
}

const fetchUserPermissions = async (userId) => {
  if (!userId) return
  loadingPermissions.value = true
  try {
    const res = await allinoneAPI.getUserPermissions(userId)
    if (res.data.code === '0') {
      userPermissionCodes.value = (res.data.data || []).map(p => p.code)
    }
  } catch (error) {
    console.error('获取用户权限失败', error)
  } finally {
    loadingPermissions.value = false
  }
}

const handleUserChange = (userId) => {
  fetchUserPermissions(userId)
}

const hasPermission = (code) => {
  return userPermissionCodes.value.includes(code)
}

const togglePermission = async (permission) => {
  if (!userSelectForm.value.userId) {
    ElMessage.warning('请先选择用户')
    return
  }

  const currentCodes = [...userPermissionCodes.value]
  let newCodes

  if (hasPermission(permission.code)) {
    newCodes = currentCodes.filter(c => c !== permission.code)
  } else {
    newCodes = [...currentCodes, permission.code]
  }

  const permissionIds = permissionList.value
    .filter(p => newCodes.includes(p.code))
    .map(p => p.id)

  try {
    const res = await allinoneAPI.assignPermissions({
      userId: userSelectForm.value.userId,
      permissionIds: permissionIds
    })
    if (res.data.code === '0') {
      ElMessage.success('权限分配成功')
      fetchUserPermissions(userSelectForm.value.userId)
    } else {
      ElMessage.error(res.data.message || '权限分配失败')
    }
  } catch (error) {
    ElMessage.error('权限分配失败')
  }
}

onMounted(() => {
  fetchPermissionList()
  fetchUserList()
})
</script>

<style scoped>
.permission-manage-container {
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

.card-header {
  font-weight: 600;
}
</style>
