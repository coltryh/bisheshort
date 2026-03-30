<template>
  <div class="common-layout">
    <el-container>
      <el-header height="54px" style="padding: 0">
        <div class="header">
          <div @click="toMySpace" class="logo">短链接管理系统@阮义亨</div>
          <div style="display: flex; align-items: center">
            <!-- 原作者链接已注释 -->
            <!--
            <a
              class="link-span"
              style="text-decoration: none"
              target="_blank"
              href="https://nageoffer.com/shortlink/"
              >官方文档</a
            >
            <a
              class="link-span"
              style="text-decoration: none"
              target="_blank"
              href="https://nageoffer.com/planet/group/"
              >加沟通群</a
            >
            <a
                class="link-span"
                style="text-decoration: none"
                target="_blank"
                href="https://nageoffer.com/shortlink/video/"
            >🔥视频教程</a
            >
            <a
                class="link-span"
                style="text-decoration: none"
                target="_blank"
                href="http://shortlink.nageoffer.com"
            >演示环境</a
            >
            -->
            <el-dropdown>
              <div class="block">
                <span
                    class="name-span"
                    style="text-decoration: none"
                >{{username}}</span
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
      <el-main style="padding: 0">
        <div class="content-box">
          <RouterView class="content-space" />
        </div>
      </el-main>
      <!-- <el-container>
        <el-aside width="180px">
          <el-menu
            active-text-color="#073372"
            background-color="#0e5782"
            class="el-menu-vertical-demo"
            :default-active="getLasteRoute(route.path)"
            text-color="#fff"
            @select="handleSelect"
          >
            <template v-for="item in menuInfos" :key="item.name">
              <el-menu-item :index="item.path">
                <el-icon><icon-menu /></el-icon>
                <span>{{ item.name }}</span>
              </el-menu-item>
            </template>
          </el-menu></el-aside
        >

      </el-container> -->
    </el-container>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { removeKey, removeUsername, getToken, getUsername } from '@/core/auth.js'
import { ElMessage } from 'element-plus'
const { proxy } = getCurrentInstance()
const API = proxy.$API
// 当当前路径和菜单不匹配时，菜单不会被选中
const router = useRouter()
const squareUrl = ref('https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png')
const toMine = () => {
  router.push('/home' + '/account')
}
// 登出
const logout = async () => {
  const token = getToken()
  const username = getUsername()
  // 请求登出的接口
  await API.user.logout({ token, username })
  // 删除cookies中的token和username
  removeUsername()
  removeKey()
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/login')
  ElMessage.success('成功退出！')
}
// 点击左上方的图片跳转到我的空间
const toMySpace = () => {
  router.push('/home' + '/space')
}
const username = ref('')
onMounted(async () => {
  const actualUsername = getUsername()
  const res = await API.user.queryUserInfo(actualUsername)
  // firstName.value = res?.data?.data?.realName?.split('')[0]
  username.value = truncateText(actualUsername, 8)
})
const extractColorByName = (name) => {
  var temp = []
  temp.push('#')
  for (let index = 0; index < name.length; index++) {
    temp.push(parseInt(name[index].charCodeAt(0), 10).toString(16))
  }
  return temp.slice(0, 5).join('').slice(0, 4)
}

// 辅助函数，用于截断文本
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
    background-color: #5D7052;

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

.link-span {
  color: #F3F4F1;
  opacity: 0.85;
  margin-right: 30px;
  font-size: 15px;
  font-family: 'Nunito', sans-serif;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s ease;
}

.link-span:hover {
  text-decoration: none !important;
  opacity: 1;
  color: #FFFFFF;
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

.avatar {
  transform: translateY(-2px);
}
</style>
