import axios from 'axios'
import {getToken, getUsername} from '@/core/auth.js'
import {isNotEmpty} from '@/utils/plugins.js'
import router from "@/router";

// allinone API instance with baseURL: '/api'
const allinoneHttp = axios.create({
    baseURL: '/api',
    timeout: 15000
})
allinoneHttp.interceptors.request.use(
    (config) => {
        config.headers.Token = isNotEmpty(getToken()) ? getToken() : ''
        config.headers.Username = isNotEmpty(getUsername()) ? getUsername() : ''
        return config
    },
    (error) => Promise.reject(error)
)
allinoneHttp.interceptors.response.use(
    (res) => (res.status === 200 || res.status === 0) ? Promise.resolve(res) : Promise.reject(res),
    (err) => {
        if (err.response?.status === 401) {
            localStorage.removeItem('token')
            router.push('/login')
        }
        return Promise.reject(err)
    }
)

import http from '../axios'
export default {
  queryPage(data) {
    return http({
      url: '/page',
      method: 'get',
      params: data
    })
  },
  addSmallLink(data) {
    return http({
      url: '/create',
      method: 'post',
      data
    })
  },
  addLinks(data) {
    return http({
      responseType: 'arraybuffer',
      url: '/create/batch',
      method: 'post',
      data,
      // responseType: 'blob'
    })
  },
  editSmallLink(data) {
    return http({
      url: '/update',
      method: 'post',
      data
    })
  },
  // 通过链接查询标题
  queryTitle(data) {
    return http({
      method: 'get',
      url: '/title',
      params: data
    })
  },
  // 移动到回收站
  toRecycleBin(data) {
    return http({
      url: '/recycle-bin/save',
      method: 'post',
      data
    })
  },
  // 查询回收站数据
  queryRecycleBin(data) {
    return http({
      url: '/recycle-bin/page',
      method: 'get',
      params: data
    })
  },
  // 恢复短链接
  recoverLink(data) {
    return http({
      method: 'post',
      url: '/recycle-bin/recover',
      data
    })
  },
  removeLink(data) {
    return http({
      method: 'post',
      url: '/recycle-bin/remove',
      data
    })
  },
  // 查询单链的图表数据
  queryLinkStats(data) {
    return allinoneHttp({
      method: 'get',
      params: data,
      url: '/stats'
    })
  },
  // 查询分组的访问记录
  queryLinkTable(data) {
    return http({
      method: 'get',
      params: data,
      url: 'stats/access-record'
    })
  }
}
