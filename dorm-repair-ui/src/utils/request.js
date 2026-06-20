import axios from 'axios'
import { ElMessage } from 'element-plus'

const TOKEN_KEY = 'token'
const USER_KEY = 'user'

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

// 请求拦截器：添加 token
request.interceptors.request.use(
  config => {
    const token = sessionStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理错误
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      ElMessage.error('未登录或token已过期')
      sessionStorage.removeItem(TOKEN_KEY)
      sessionStorage.removeItem(USER_KEY)
      window.location.href = '/login'
    } else if (error.response?.status === 403) {
      ElMessage.error('无权限访问')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
