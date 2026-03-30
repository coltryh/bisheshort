<template>
  <div class="login-page">
    <!-- Organic Background Elements -->
    <div class="organic-blobs">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
      <div class="blob blob-3"></div>
    </div>

    <!-- Main Content -->
    <div class="login-container">
      <!-- Logo/Brand Section -->
      <div class="brand-section">
        <div class="logo-container">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L3 7v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V7l-9-5z" fill="currentColor" stroke="#2C2C24" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div>
            <h1 class="brand-title text-h2">短链接系统</h1>
            <p class="brand-subtitle text-body">创建、管理和追踪您的短链接</p>
          </div>
        </div>
      </div>

      <!-- Login/Register Card -->
      <div class="auth-card organic-card">
        <!-- Login Form -->
        <transition name="slide-fade" mode="out-in">
          <div v-if="isLogin" class="auth-form" key="login">
            <div class="auth-header">
              <h2 class="auth-title text-h3">欢迎回来</h2>
              <p class="auth-subtitle text-body">登录您的账户</p>
            </div>

            <el-form ref="loginFormRef1" :model="loginForm" :rules="loginFormRule" label-position="top">
              <el-form-item prop="username" class="organic-input">
                <template #prefix>
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="7" r="4" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </template>
                <el-input
                  v-model="loginForm.username"
                  placeholder="用户名"
                  size="large"
                  clearable
                />
              </el-form-item>

              <el-form-item prop="password" class="organic-input">
                <template #prefix>
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </template>
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="密码"
                  size="large"
                  show-password
                  clearable
                />
              </el-form-item>

              <div class="form-actions">
                <el-checkbox v-model="checked" class="organic-checkbox">
                  <span>记住我</span>
                </el-checkbox>
              </div>

              <div class="btn-group">
                <el-button
                  type="primary"
                  size="large"
                  class="organic-btn btn-block"
                  :loading="loading"
                  @click="login(loginFormRef1)">
                  <span class="btn-text">登录</span>
                  <svg class="btn-icon" viewBox="0 0 24 24" fill="none">
                    <path d="M5 12h14M12 5l7 7-7 7" stroke="#F3F4F1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </el-button>
              </div>
            </el-form>
          </div>

          <!-- Register Form -->
          <div v-else class="auth-form" key="register">
            <div class="auth-header">
              <h2 class="auth-title text-h3">开始旅程</h2>
              <p class="auth-subtitle text-body">创建新账户</p>
            </div>

            <el-form ref="loginFormRef2" :model="addForm" :rules="addFormRule" label-position="top">
              <el-form-item prop="username" class="organic-input">
                <template #prefix>
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="7" r="4" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </template>
                <el-input
                  v-model="addForm.username"
                  placeholder="用户名"
                  size="large"
                  clearable
                />
              </el-form-item>

              <el-form-item prop="realName" class="organic-input">
                <template #prefix>
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="12" cy="7" r="4" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </template>
                <el-input
                  v-model="addForm.realName"
                  placeholder="您的姓名"
                  size="large"
                  clearable
                />
              </el-form-item>

              <el-form-item prop="mail" class="organic-input">
                <template #prefix>
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <polyline points="22,6 12,13 2,6" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </template>
                <el-input
                  v-model="addForm.mail"
                  placeholder="邮箱地址"
                  size="large"
                  clearable
                />
              </el-form-item>

              <el-form-item prop="phone" class="organic-input">
                <template #prefix>
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </template>
                <el-input
                  v-model="addForm.phone"
                  placeholder="手机号码"
                  size="large"
                  clearable
                />
              </el-form-item>

              <el-form-item prop="password" class="organic-input">
                <template #prefix>
                  <svg class="input-icon" viewBox="0 0 24 24" fill="none">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" stroke="#78786C" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </template>
                <el-input
                  v-model="addForm.password"
                  type="password"
                  placeholder="密码"
                  size="large"
                  show-password
                  clearable
                />
              </el-form-item>

              <div class="btn-group">
                <el-button
                  type="primary"
                  size="large"
                  class="organic-btn btn-block"
                  :loading="loading"
                  @click="addUser(loginFormRef2)">
                  <span class="btn-text">注册</span>
                  <svg class="btn-icon" viewBox="0 0 24 24" fill="none">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="#F3F4F1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="8.5" cy="7" r="4" stroke="#F3F4F1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <line x1="20" y1="8" x2="20" y2="14" stroke="#F3F4F1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <line x1="23" y1="11" x2="17" y2="11" stroke="#F3F4F1" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </el-button>
              </div>
            </el-form>
          </div>
        </transition>

        <!-- Login/Register Toggle -->
        <div class="toggle-section">
          <p class="toggle-text">
            {{ isLogin ? '还没有账号？' : '已有账号？' }}
          </p>
          <el-button
            text
            class="toggle-btn"
            @click="changeLogin">
            {{ isLogin ? '去注册' : '去登录' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- Vanta 3D Background (optional) -->
    <div ref="vantaRef" class="vanta"></div>

    <!-- Verification Dialog -->
    <el-dialog v-model="isWC" title="人机验证" width="40%" :before-close="handleClose" class="organic-dialog">
      <div class="verification-flex">
        <span>扫码下方二维码，关注后回复：<strong class="accent-text">link</strong>，获取短链接系统人机验证码</span>
        <img class="qr-code" src="@/assets/png/公众号二维码.png" alt="公众号二维码">
        <el-form class="verification-form" :model="verification" :rules="verificationRule" ref="verificationRef">
          <el-form-item prop="code" label="验证码">
            <el-input v-model="verification.code" placeholder="请输入验证码" size="large" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="isWC = false">取消</el-button>
          <el-button type="primary" @click="verificationLogin(verificationRef)">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { setToken, setUsername, getUsername } from '@/core/auth.js'
import { ref, reactive, onMounted, onBeforeUnmount, watch, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as THREE from 'three'
import WAVES from 'vanta/src/vanta.waves'
const { proxy } = getCurrentInstance()
const API = proxy.$API
const loginFormRef1 = ref()
const loginFormRef2 = ref()
const router = useRouter()
const loginForm = reactive({
  username: 'admin',
  password: 'admin123456',
})
const addForm = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  mail: ''
})

const addFormRule = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      pattern: /^1[3|5|7|8|9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    },
    { min: 11, max: 11, message: '手机号必须是11位', trigger: 'blur' }
  ],
  username: [{ required: true, message: '请输入您的真实姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 15, message: '密码长度请在八位以上', trigger: 'blur' }
  ],
  mail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {
      pattern: /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/,
      message: '请输入正确的邮箱号',
      trigger: 'blur'
    }
  ],
  realNamee: [
    { required: true, message: '请输姓名', trigger: 'blur' },
  ]
})
const loginFormRule = reactive({
  username: [{ required: true, message: '请输入您的真实姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 15, message: '密码长度请在八位以上', trigger: 'blur' }
  ],
})
// 注册
const addUser = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      // 检测用户名是否已经存在
      const res1 = await API.user.hasUsername({ username: addForm.username })
      if (res1.data.success !== false) {
        // 注册
        const res2 = await API.user.addUser(addForm)
        // console.log(res2)
        if (res2.data.success === false) {
          ElMessage.warning(res2.data.message)
        } else {
          const res3 = await API.user.login({ username: addForm.username, password: addForm.password })
          const token = res3?.data?.data?.token
          // 将username和token保存到cookies中和localStorage中
          if (token) {
            setToken(token)
            setUsername(addForm.username)
            localStorage.setItem('token', token)
            localStorage.setItem('username', addForm.username)
          }
          ElMessage.success('注册登录成功！')
          router.push('/home')
        }
      } else {
        ElMessage.warning('用户名已存在！')
      }
    } else {
      return false
    }
  })

}
// 公众号验证码
const isWC = ref(false)
const verificationRef = ref()
const verification = reactive({
  code: ''
})
const verificationRule = reactive({
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
})
const handleClose = () => {
  isWC.value = false
}
const verificationLogin = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      const tempPassword = loginForm.password
      loginForm.password = verification.code
      const res1 = await API.user.login(loginForm)
      if (res1.data.code === '0') {
        const token = res1?.data?.data?.token
        // 将username和token保存到cookies中和localStorage中
        if (token) {
          setToken(token)
          setUsername(loginForm.username)
          localStorage.setItem('token', token)
          localStorage.setItem('username', loginForm.username)
        }
        ElMessage.success('登录成功！')
        router.push('/home')
      } else if (res1.data.message === '用户已登录') {
        // 如果已经登录了，判断一下浏览器保存的登录信息是不是再次登录的信息，如果是就正常登录
        const cookiesUsername = getUsername()
        if (cookiesUsername === loginForm.username) {
          ElMessage.success('登录成功！')
          router.push('/home')
        } else {
          ElMessage.warning('用户已在别处登录，请勿重复登录！')
        }
      } else {
        ElMessage.error('请输入正确的验证码!')
      }
      loginForm.password = tempPassword
    }
  })
}
// 登录
const login = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      const res1 = await API.user.login(loginForm)
      if (res1.data.code === '0') {
        const token = res1?.data?.data?.token
        // 将username和token保存到cookies中和localStorage中
        if (token) {
          setToken(token)
          setUsername(loginForm.username)
          localStorage.setItem('token', token)
          localStorage.setItem('username', loginForm.username)
        }
        ElMessage.success('登录成功！')
        router.push('/home')
      } else if (res1.data.message === '用户已登录') {
        // 如果已经登录了，判断一下浏览器保存的登录信息是不是再次登录的信息，如果是就正常登录
        const cookiesUsername = getUsername()
        if (cookiesUsername === loginForm.username) {
          ElMessage.success('登录成功！')
          router.push('/home')
        } else {
          ElMessage.warning('用户已在别处登录，请勿重复登录！')
        }
      } else if (res1.data.message === '用户不存在') {
        ElMessage.error('请输入正确的账号密码!')
      }
    } else {
      return false
    }
  })


}

const loading = ref(false)
// 是否记住密码
const checked = ref(true)
const vantaRef = ref()
// 动态背景
let vantaEffect = null
onMounted(() => {
  vantaEffect = WAVES({
    el: vantaRef.value,
    THREE: THREE,
    mouseControls: true,
    touchControls: true,
    gyroControls: false,
    minHeight: 200.0,
    minWidth: 200.0,
    scale: 1.0,
    scaleMobile: 1.0
  })
})
onBeforeUnmount(() => {
  if (vantaEffect) {
    vantaEffect.destroy()
  }
})
// 展示登录还是展示注册
const isLogin = ref(true)
const changeLogin = () => {
  let domain = window.location.host
  if (domain === 'shortlink.magestack.cn' || domain === 'shortlink.ryh.com') {
    ElMessage.warning('演示环境暂不支持注册')
    return
  }
  isLogin.value = !isLogin.value
}
</script>

<style lang="less" scoped>
// Import design tokens
@import '../../styles/design-system.less';

// Layout
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: @spacing-lg;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #FDFCF8 0%, #F0EBE5 100%);
}

// Organic Blobs Background
.organic-blobs {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  opacity: 0.6;
}

.blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
}

// Blob 1 - Top Left
.blob-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, #7A8B6E 0%, #5D7052 70%);
  top: -100px;
  left: -100px;
  animation: float-1 20s infinite ease-in-out;
}

@keyframes float-1 {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(-30px, 30px);
  }
}

// Blob 2 - Bottom Right
.blob-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #E6DCCD 0%, #C18C5D 70%);
  bottom: -80px;
  right: -80px;
  animation: float-2 25s infinite ease-in-out;
}

@keyframes float-2 {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(20px, -20px);
  }
}

// Blob 3 - Center Bottom
.blob-3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, #F0EBE5 0%, #78786C 70%);
  bottom: -50px;
  left: 50%;
  transform: translateX(-50%);
  animation: float-3 15s infinite ease-in-out;
}

@keyframes float-3 {
  0%, 100% {
    transform: translateX(-50%) translateY(0);
  }
  50% {
    transform: translateX(-50%) translateY(-20px);
  }
}

// Main Container
.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 480px;
  animation: fade-in-up 0.6s ease-out;
}

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// Brand Section
.brand-section {
  text-align: center;
  margin-bottom: @spacing-xl;
}

.logo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: @spacing-md;
}

.logo-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #5D7052 0%, #4A5C3D 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #F3F4F1;
  box-shadow: 0 8px 24px rgba(93, 112, 82, 0.25);
}

.logo-icon svg {
  width: 36px;
  height: 36px;
}

.brand-title {
  font-family: 'Fraunces', -apple-system, serif;
  font-size: 1.5rem;
  font-weight: 600;
  line-height: 1.3;
  color: #2C2C24;
  margin: 0;
}

.brand-subtitle {
  font-family: 'Nunito', 'Quicksand', -apple-system, sans-serif;
  font-size: 1rem;
  line-height: 1.6;
  color: #78786C;
  margin: 0;
}

// Auth Card
.auth-card {
  background: #FFFFFF;
  border-radius: @radius-xl;
  box-shadow: @shadow-card;
  padding: @spacing-xl;
  overflow: hidden;
}

.auth-header {
  text-align: center;
  margin-bottom: @spacing-lg;
}

.auth-title {
  font-family: 'Fraunces', -apple-system, serif;
  font-size: 1.25rem;
  font-weight: 600;
  line-height: 1.4;
  color: #2C2C24;
  margin: 0 0 @spacing-xs 0;
}

.auth-subtitle {
  font-family: 'Nunito', 'Quicksand', -apple-system, sans-serif;
  font-size: 1rem;
  line-height: 1.6;
  color: #78786C;
  margin: 0;
}

// Form Styles
.auth-form {
  margin-top: @spacing-lg;
}

.organic-input {
  margin-bottom: @spacing-md;
}

.organic-input :deep(.el-input__wrapper) {
  border-radius: @radius-lg;
  border: 2px solid #DED8CF;
  background: #FFFFFF;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 4px 12px;
  box-shadow: none;
}

.organic-input :deep(.el-input__wrapper:hover) {
  border-color: #5D7052;
}

.organic-input.is-focus :deep(.el-input__wrapper) {
  border-color: #5D7052;
  box-shadow: 0 0 0 4px rgba(93, 112, 82, 0.1);
}

.organic-input :deep(.el-input__inner) {
  font-size: 1rem;
  color: #2C2C24;
}

// Icons in inputs
.input-icon {
  width: 20px;
  height: 20px;
  margin-right: @spacing-sm;
  color: #78786C;
}

// Form Actions
.form-actions {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: @spacing-lg;
}

.organic-checkbox :deep(.el-checkbox__label) {
  color: #78786C !important;
  font-weight: 500;
}

.organic-checkbox :deep(.el-checkbox__inner) {
  border-radius: 4px;
  border-color: #DED8CF;
}

.organic-checkbox:hover :deep(.el-checkbox__inner) {
  border-color: #5D7052;
}

.organic-checkbox.is-checked :deep(.el-checkbox__inner) {
  background-color: #5D7052;
  border-color: #5D7052;
}

// Button Styles
.btn-group {
  margin-top: @spacing-lg;
}

.organic-btn {
  width: 100%;
  height: 50px;
  border: none;
  position: relative;
  overflow: hidden;
  font-weight: 600;
  letter-spacing: 0.5px;
  border-radius: @radius-pill;
  background: linear-gradient(135deg, #5D7052 0%, #4A5C3D 100%);
  box-shadow: 0 4px 16px rgba(93, 112, 82, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.organic-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(93, 112, 82, 0.4);
}

.organic-btn:active {
  transform: translateY(0);
}

.btn-block {
  display: block;
  width: 100%;
}

.organic-btn .btn-text {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: @spacing-sm;
  color: #F3F4F1;
  font-size: 1rem;
}

.btn-icon {
  width: 20px;
  height: 20px;
  transition: transform 0.3s ease;
}

.organic-btn:hover .btn-icon {
  transform: translateX(3px);
}

// Toggle Section
.toggle-section {
  text-align: center;
  margin-top: @spacing-lg;
  padding-top: @spacing-lg;
  border-top: 1px solid #F0EBE5;
}

.toggle-text {
  font-family: 'Nunito', 'Quicksand', -apple-system, sans-serif;
  font-size: 1rem;
  line-height: 1.6;
  color: #78786C;
  margin: 0 0 @spacing-sm 0;
}

.toggle-btn {
  color: #5D7052;
  font-weight: 600;
  padding: @spacing-xs @spacing-md;
  border-radius: @radius-pill;
  transition: all 0.3s ease;
}

.toggle-btn:hover {
  background-color: rgba(93, 112, 82, 0.1);
}

// Form Transition
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

.slide-fade-enter-to,
.slide-fade-leave-from {
  opacity: 1;
  transform: translateX(0);
}

// Vanta 3D Background (Keep original)
.vanta {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
  opacity: 0.3;
}

// Verification Dialog
.verification-flex {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: @spacing-md;
}

.qr-code {
  align-self: center;
  max-width: 200px;
  border-radius: @radius-lg;
  border: 2px solid #DED8CF;
  padding: @spacing-sm;
}

.verification-form {
  width: 100%;
  margin-top: @spacing-md;
}

.accent-text {
  color: #5D7052;
}

// Responsive
@media (max-width: @screen-sm) {
  .login-container {
    padding: @spacing-md;
  }

  .organic-blobs {
    opacity: 0.4;
  }

  .blob-1 {
    width: 300px;
    height: 300px;
  }

  .blob-2 {
    width: 250px;
    height: 250px;
  }

  .blob-3 {
    width: 200px;
    height: 200px;
  }

  .auth-card {
    padding: @spacing-lg;
  }
}
</style>
