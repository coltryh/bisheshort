import axios from 'axios'
import {getToken, getUsername} from '@/core/auth.js'
import {isNotEmpty} from '@/utils/plugins.js'
import router from "@/router";
import { ElMessage } from 'element-plus'

// 创建 admin 模块的 axios 实例（用于短链接AI分析功能）
const http = axios.create({
    baseURL: '/api/short-link/admin/v1',
    timeout: 60000  // 60秒超时，MiniMax API 可能较慢
})

// 请求拦截
http.interceptors.request.use((config) => {
    config.headers.Token = isNotEmpty(getToken()) ? getToken() : ''
    config.headers.Username = isNotEmpty(getUsername()) ? getUsername() : ''
    return config
})

// 响应拦截
http.interceptors.response.use((res) => {
    if (res.status == 0 || res.status == 200) {
        return Promise.resolve(res)
    }
    return Promise.reject(res)
}, (err) => {
    if (err.response && err.response.status === 401) {
        localStorage.removeItem('token')
        router.push('/login')
    }
    return Promise.reject(err)
})

export default {
    // AI智能问答（短链接分析）
    chat(data) {
        return http({
            url: '/ai/chat',
            method: 'post',
            data
        })
    },

    // AI分析短链接统计数据
    analyzeStats(data) {
        return http({
            url: '/ai/analyze',
            method: 'post',
            data
        })
    },

    // AI生成短链接描述建议
    suggestDescription(data) {
        return http({
            url: '/ai/suggest',
            method: 'post',
            data
        })
    },

    // AI批量分析
    batchAnalyze(data) {
        return http({
            url: '/ai/batch-analyze',
            method: 'post',
            data
        })
    }
}