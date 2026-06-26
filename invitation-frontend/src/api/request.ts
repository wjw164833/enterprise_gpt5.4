import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import Cookies from 'js-cookie'
import router from '@/router'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1'

const service: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

let isRefreshing = false
let pendingRequests: Array<(token: string) => void> = []

function getAccessToken(): string {
  return Cookies.get('access_token') || localStorage.getItem('access_token') || ''
}

function setTokens(accessToken: string, refreshToken: string): void {
  Cookies.set('access_token', accessToken, { expires: 1 })
  Cookies.set('refresh_token', refreshToken, { expires: 7 })
  localStorage.setItem('access_token', accessToken)
  localStorage.setItem('refresh_token', refreshToken)
}

function clearTokens(): void {
  Cookies.remove('access_token')
  Cookies.remove('refresh_token')
  localStorage.removeItem('access_token')
  localStorage.removeItem('refresh_token')
}

async function refreshToken(): Promise<string> {
  const refreshTokenValue = Cookies.get('refresh_token') || localStorage.getItem('refresh_token') || ''
  if (!refreshTokenValue) {
    throw new Error('No refresh token')
  }
  // P0-06: 修复Token刷新API路径，与后端 POST /api/v1/auth/refresh 匹配
  const { data } = await axios.post(`${BASE_URL}/auth/refresh`, {
    refreshToken: refreshTokenValue
  })
  if (data.code === 200 && data.data) {
    setTokens(data.data.accessToken, data.data.refreshToken)
    return data.data.accessToken
  }
  throw new Error('Refresh token failed')
}

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getAccessToken()
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response
    if (data.code !== undefined && data.code !== 200) {
      const errorMessage = data.message || '请求失败'
      if (data.code === 401) {
        handleUnauthorized()
        return Promise.reject(new Error('登录已过期'))
      }
      if (data.code === 403) {
        ElMessage.error('没有权限访问')
        return Promise.reject(new Error('没有权限'))
      }
      ElMessage.error(errorMessage)
      return Promise.reject(new Error(errorMessage))
    }
    return data
  },
  async (error) => {
    if (!error.response) {
      ElMessage.error('网络异常，请检查网络连接')
      return Promise.reject(error)
    }
    const { status, data } = error.response
    if (status === 401) {
      const config = error.config as AxiosRequestConfig & { _retry?: boolean }
      if (config._retry) {
        handleUnauthorized()
        return Promise.reject(error)
      }
      if (isRefreshing) {
        return new Promise((resolve) => {
          pendingRequests.push((token: string) => {
            if (config.headers) {
              config.headers.Authorization = `Bearer ${token}`
            }
            resolve(service(config))
          })
        })
      }
      config._retry = true
      isRefreshing = true
      try {
        const newToken = await refreshToken()
        isRefreshing = false
        pendingRequests.forEach((cb) => cb(newToken))
        pendingRequests = []
        if (config.headers) {
          config.headers.Authorization = `Bearer ${newToken}`
        }
        return service(config)
      } catch (refreshError) {
        isRefreshing = false
        pendingRequests = []
        handleUnauthorized()
        return Promise.reject(refreshError)
      }
    }
    if (status === 403) {
      ElMessage.error('没有权限访问')
    } else if (status === 404) {
      ElMessage.error('请求的资源不存在')
    } else if (status >= 500) {
      ElMessage.error('服务器异常，请稍后重试')
    } else {
      ElMessage.error(data?.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

function handleUnauthorized(): void {
  clearTokens()
  const currentRoute = router.currentRoute.value
  if (currentRoute.path !== '/login' && !currentRoute.path.startsWith('/guest')) {
    router.push({
      path: '/login',
      query: { redirect: currentRoute.fullPath }
    })
  }
}

export { service as request, getAccessToken, setTokens, clearTokens }
