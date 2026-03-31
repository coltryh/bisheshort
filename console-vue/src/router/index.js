import { createRouter, createWebHistory } from 'vue-router'
import { isNotEmpty } from '@/utils/plugins'
import { getToken, setToken, setUsername } from '@/core/auth' // 验权
import user from '@/api/modules/user'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Landing',
      component: () => import('@/views/landing/LandingIndex.vue')
    },
    {
      path: '/login',
      name: 'LoginIndex',
      component: () => import('@/views/login/LoginIndex.vue')
    },
    {
      path: '/home',
      name: 'LayoutIndex',
      redirect: '/home/space',
      component: () => import('@/views/home/HomeIndex.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          // 前面不能加/
          path: 'space',
          name: 'MySpace',
          component: () => import('@/views/mySpace/MySpaceIndex.vue'),
          meta: { title: '我的空间', requiresAuth: true }
        },
        {
          path: 'recycleBin',
          name: 'RecycleBin',
          component: () => import('@/views/recycleBin/RecycleBinIndex.vue'),
          meta: { title: '账户设置', requiresAuth: true }
        },
        {
          path: 'account',
          name: 'Mine',
          component: () => import('@/views/mine/MineIndex.vue'),
          meta: { title: '个人中心', requiresAuth: true }
        },
        {
          path: 'ai',
          name: 'AiChat',
          component: () => import('@/views/ai/AiChatIndex.vue'),
          meta: { title: 'AI助手', requiresAuth: true }
        }
      ]
    }
  ]
})

// eslint-disable-next-line no-unused-vars
router.beforeEach(async (to, from, next) => {
  // 从localstorage中先获取token，并赋给cookies
  setToken(localStorage.getItem('token'))
  setUsername(localStorage.getItem('username'))
  const token = getToken()

  // 首页和登录页不需要登录
  if (to.path === '/' || to.path === '/login') {
    // 如果已登录，跳转到首页
    if (to.path === '/login' && isNotEmpty(token)) {
      next('/home')
    } else {
      next()
    }
    return
  }

  // 其他页面需要登录
  if (isNotEmpty(token)) {
    next()
  } else {
    next('/login')
  }
})

export default router
