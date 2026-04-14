<template>
  <div class="common-layout">
    <el-container>
      <el-header height="54px" style="padding: 0">
        <div class="header">
          <div @click="toMySpace" class="logo">短链接管理系统@阮义亨</div>
          <div style="display: flex; align-items: center">
            <el-dropdown>
              <div class="block">
                <span
                    class="name-span"
                    style="text-decoration: none"
                >{{username}} ({{ isAdmin ? '管理员' : '普通用户' }})</span
                >
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="toMine">个人信息</el-dropdown-item>
                  <el-dropdown-item divided @click="logout">退出</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu
            active-text-color="#ffd04b"
            background-color="#545c64"
            class="el-menu-vertical-demo"
            :default-active="activeMenu"
            text-color="#fff"
            @select="handleSelect"
          >
            <el-menu-item index="/home/space">
              <el-icon><Link /></el-icon>
              <span>短链管理</span>
            </el-menu-item>
            <el-menu-item index="/home/recycleBin" v-if="isAdmin">
              <el-icon><Delete /></el-icon>
              <span>回收站</span>
            </el-menu-item>
            <el-menu-item index="/home/stats">
              <el-icon><DataAnalysis /></el-icon>
              <span>图表中心</span>
            </el-menu-item>
            <el-menu-item index="/home/ai">
              <el-icon><ChatDotRound /></el-icon>
              <span>AI分析</span>
            </el-menu-item>
            <el-sub-menu index="admin" v-if="isAdmin">
              <template #title>
                <el-icon><User /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/home/admin/user">用户管理</el-menu-item>
              <el-menu-item index="/home/admin/permission">权限管理</el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-aside>
        <el-main style="padding: 0">
          <div class="content-box">
            <RouterView class="content-space" />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { removeKey, removeUsername, getToken, getUsername } from '@/core/auth.js'
import { ElMessage } from 'element-plus'
import { Link, DataAnalysis, ChatDotRound, Delete, User } from '@element-plus/icons-vue'

const { proxy } = getCurrentInstance()
const API = proxy.$API
const router = useRouter()
const route = useRoute()

const username = ref('')
const isAdmin = ref(false)
const activeMenu = computed(() => route.path)

const handleSelect = (index) => {
  router.push(index)
}

const toMine = () => {
  router.push('/home' + '/account')
}

const logout = async () => {
  removeUsername()
  removeKey()
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('userInfo')
  router.push('/login')
  ElMessage.success('成功退出！')
}

const toMySpace = () => {
  router.push('/home' + '/space')
}

onMounted(async () => {
  const actualUsername = getUsername()
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr)
      username.value = userInfo.username || actualUsername
      isAdmin.value = userInfo.role === 'admin'
      // 如果没有role字段，尝试从API获取
      if (!userInfo.role) {
        const API = proxy.$API
        const res = await API.allinone.currentUser()
        if (res?.data?.data) {
          const freshUserInfo = res.data.data
          localStorage.setItem('userInfo', JSON.stringify(freshUserInfo))
          username.value = freshUserInfo.username || actualUsername
          isAdmin.value = freshUserInfo.role === 'admin'
        }
      }
    } catch (e) {
      username.value = actualUsername
    }
  } else {
    username.value = actualUsername
  }
})

const truncateText = (text, maxLength) => {
  return text.length > maxLength ? text.slice(0, maxLength) + '...' : text
}
</script>

<style lang="less" scoped>
@import '../../styles/design-system.less';

.el-container {
  height: 100vh;

  .el-aside {
    border: 0;
    background-color: #545c64;

    ul {
      border: 0px;
    }
  }

  .el-main {
    background-color: #F0EBE5;
  }
}

.header {
  color: #2C2C24;
  background: linear-gradient(135deg, #5D7052 0%, #4A5C3D 100%);
  padding: 0 0 0 20px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 12px rgba(93, 112, 82, 0.15);

  .block {
    cursor: pointer;
    display: flex;
    align-items: center;
    border: 0px;
    color: #F3F4F1;
    transition: all 0.3s ease;
  }

  .block:hover {
    color: #FFFFFF;
  }
}

.content-box {
  height: calc(100vh - 54px);
  background-color: #FDFCF8;
}

:deep(.el-tooltip__trigger:focus-visible) {
  outline: unset;
}

.logo {
  font-size: 16px;
  font-weight: 600;
  color: #F3F4F1;
  font-family: 'Fraunces', 'Nunito', sans-serif;
  cursor: pointer;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
}

.logo:hover {
  color: #FFFFFF;
  transform: translateY(-1px);
}

.name-span {
  color: #F3F4F1;
  opacity: 0.9;
  margin-right: 20px;
  font-size: 14px;
  font-family: 'Nunito', sans-serif;
  cursor: pointer;
  text-decoration: none;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-weight: 500;
  padding: 6px 12px;
  border-radius: @radius-pill;
  background-color: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.name-span:hover {
  background-color: rgba(255, 255, 255, 0.2);
  opacity: 1;
}
</style>
