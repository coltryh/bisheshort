import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      // admin 服务 (8002) - 原有功能
      '/api/short-link/admin': {
        target: 'http://localhost:8002',
        changeOrigin: true,
        ws: true,
        rewrite: (path) => path // 保留完整路径
      },
      // project 服务 (8001) - AI 功能
      '/api/short-link/v1': {
        target: 'http://localhost:8001',
        changeOrigin: true,
        ws: true,
        rewrite: (path) => path // 保留完整路径
      }
    }
  }
})