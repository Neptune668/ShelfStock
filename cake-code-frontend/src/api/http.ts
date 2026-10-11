import axios, { type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import type { Result } from './types'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE,
  timeout: 10000,
})

// 请求拦截器：携带 token
http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一处理业务错误与鉴权错误
http.interceptors.response.use(
  (response) => {
    const result = response.data as Result<unknown>
    // 业务错误：HTTP 200 但 code 非 200
    if (result && typeof result.code === 'number' && result.code !== 200) {
      ElMessage.error(result.message || '操作失败')
      return Promise.reject(new Error(result.message || '操作失败'))
    }
    return response
  },
  (error: AxiosError<Result<unknown>>) => {
    const status = error.response?.status
    if (status === 401) {
      // 未登录或 token 过期
      ElMessage.error('未登录或登录已过期，请重新登录')
      localStorage.removeItem('token')
      window.location.href = '/login'
    } else if (status === 403) {
      ElMessage.error('无权限访问')
    } else {
      ElMessage.error(error.response?.data?.message || '网络异常，请稍后重试')
    }
    return Promise.reject(error)
  },
)

export default http
