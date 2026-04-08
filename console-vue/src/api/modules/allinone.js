import axios from 'axios'
import {getToken, getUsername} from '@/core/auth.js'
import {isNotEmpty} from '@/utils/plugins.js'
import router from "@/router";
import { ElMessage } from 'element-plus'

// allinone 后端 API 实例
const http = axios.create({
    baseURL: '/api',
    timeout: 15000
})

http.interceptors.request.use(
    (config) => {
        config.headers.Token = isNotEmpty(getToken()) ? getToken() : ''
        config.headers.Username = isNotEmpty(getUsername()) ? getUsername() : ''
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

http.interceptors.response.use(
    (res) => {
        if (res.status === 200 || res.status === 0) {
            return Promise.resolve(res)
        }
        return Promise.reject(res)
    },
    (err) => {
        if (err.response?.status === 401) {
            localStorage.removeItem('token')
            router.push('/login')
        }
        return Promise.reject(err)
    }
)

export default {
    // 登录
    login(data) {
        return http({
            url: '/login',
            method: 'post',
            data
        })
    },
    // 注册
    register(data) {
        return http({
            url: '/register',
            method: 'post',
            data
        })
    },
    // 登出
    logout() {
        return http({
            url: '/logout',
            method: 'post'
        })
    },
    // 获取当前用户
    currentUser() {
        return http({
            url: '/currentUser',
            method: 'get'
        })
    },
    // 用户列表
    listUsers() {
        return http({
            url: '/admin/user/list',
            method: 'get'
        })
    },
    // 更新用户
    updateUser(data) {
        return http({
            url: '/admin/user/update',
            method: 'put',
            data
        })
    },
    // 删除用户
    deleteUser(id) {
        return http({
            url: `/admin/user/delete/${id}`,
            method: 'delete'
        })
    },
    // 权限列表
    listPermissions() {
        return http({
            url: '/admin/permission/list',
            method: 'get'
        })
    },
    // 获取用户权限
    getUserPermissions(userId) {
        return http({
            url: `/admin/permission/user/${userId}`,
            method: 'get'
        })
    },
    // 分配权限
    assignPermissions(data) {
        return http({
            url: '/admin/permission/assign',
            method: 'post',
            data
        })
    },
    // 分组列表
    listGroups() {
        return http({
            url: '/group/list',
            method: 'get'
        })
    },
    // 创建分组
    createGroup(data) {
        return http({
            url: '/group/save',
            method: 'post',
            data
        })
    },
    // 更新分组
    updateGroup(data) {
        return http({
            url: '/group/update',
            method: 'put',
            data
        })
    },
    // 删除分组
    deleteGroup(gid) {
        return http({
            url: `/group/delete/${gid}`,
            method: 'delete'
        })
    },
    // 短链接列表
    listLinks(gid) {
        return http({
            url: '/link/list',
            method: 'get',
            params: gid ? { gid } : {}
        })
    },
    // 创建短链接
    createLink(data) {
        return http({
            url: '/link/save',
            method: 'post',
            data
        })
    },
    // 更新短链接
    updateLink(data) {
        return http({
            url: '/link/update',
            method: 'put',
            data
        })
    },
    // 删除短链接
    deleteLink(id) {
        return http({
            url: `/link/delete/${id}`,
            method: 'delete'
        })
    },
    // 统计概览
    statsOverview() {
        return http({
            url: '/stats/overview',
            method: 'get'
        })
    },
    // AI 分析
    aiAnalyze(params) {
        return http({
            url: '/ai/analyze',
            method: 'get',
            params
        })
    },
    // AI 对话
    aiChat(data) {
        return http({
            url: '/ai/chat',
            method: 'post',
            data
        })
    },
    // 回收站列表
    listRecycleBin() {
        return http({
            url: '/recycle-bin/list',
            method: 'get'
        })
    },
    // 恢复短链接
    recoverLink(id) {
        return http({
            url: `/recycle-bin/recover/${id}`,
            method: 'post'
        })
    },
    // 永久删除短链接
    removeLink(id) {
        return http({
            url: `/recycle-bin/remove/${id}`,
            method: 'delete'
        })
    }
}
