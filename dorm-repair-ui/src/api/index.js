import request from '../utils/request'

// 认证相关
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  getInfo: () => request.get('/auth/info'),
  changePassword: (data) => request.put('/auth/password', data)
}

// 报修工单
export const orderApi = {
  submit: (data) => request.post('/repair-orders', data),
  resubmit: (id, data) => request.put(`/repair-orders/resubmit/${id}`, data),
  page: (params) => request.get('/repair-orders/page', { params }),
  my: (params) => request.get('/repair-orders/my', { params }),
  detail: (id) => request.get(`/repair-orders/${id}`),
  cancel: (id) => request.put(`/repair-orders/cancel/${id}`),
  audit: (id, params) => request.put(`/repair-orders/audit/${id}`, null, { params }),
  assign: (id, params) => request.put(`/repair-orders/assign/${id}`, null, { params }),
  start: (id) => request.put(`/repair-orders/start/${id}`),
  finish: (id, params) => request.put(`/repair-orders/finish/${id}`, null, { params }),
  delete: (id) => request.delete(`/repair-orders/${id}`)
}

// 维修记录
export const recordApi = {
  byOrder: (orderId, params) => request.get(`/repair-records/order/${orderId}`, { params }),
  my: (params) => request.get('/repair-records/my', { params })
}

// 评价
export const evaluationApi = {
  submit: (data) => request.post('/evaluations', data),
  byOrder: (orderId) => request.get(`/evaluations/order/${orderId}`),
  page: (params) => request.get('/evaluations/page', { params }),
  my: (params) => request.get('/evaluations/my', { params })
}

// 公告
export const noticeApi = {
  page: (params) => request.get('/notices/page', { params }),
  list: (params) => request.get('/notices/list', { params }),
  detail: (id) => request.get(`/notices/${id}`),
  create: (data) => request.post('/notices', data),
  update: (id, data) => request.put(`/notices/${id}`, data),
  delete: (id) => request.delete(`/notices/${id}`),
  updateStatus: (id, params) => request.put(`/notices/status/${id}`, null, { params })
}

// 用户管理
export const userApi = {
  page: (params) => request.get('/users/page', { params }),
  detail: (id) => request.get(`/users/${id}`),
  create: (data) => request.post('/users', data),
  update: (id, data) => request.put(`/users/${id}`, data),
  delete: (id) => request.delete(`/users/${id}`),
  updateStatus: (id, params) => request.put(`/users/status/${id}`, null, { params }),
  resetPassword: (id) => request.put(`/users/reset/${id}`)
}

// 宿舍楼
export const buildingApi = {
  page: (params) => request.get('/buildings/page', { params }),
  list: () => request.get('/buildings/list'),
  detail: (id) => request.get(`/buildings/${id}`),
  create: (data) => request.post('/buildings', data),
  update: (id, data) => request.put(`/buildings/${id}`, data),
  delete: (id) => request.delete(`/buildings/${id}`)
}

// 宿舍房间
export const roomApi = {
  page: (params) => request.get('/rooms/page', { params }),
  list: (params) => request.get('/rooms/list', { params }),
  detail: (id) => request.get(`/rooms/${id}`),
  create: (data) => request.post('/rooms', data),
  update: (id, data) => request.put(`/rooms/${id}`, data),
  delete: (id) => request.delete(`/rooms/${id}`)
}

// 报修类型
export const repairTypeApi = {
  page: (params) => request.get('/repair-types/page', { params }),
  list: () => request.get('/repair-types/list'),
  detail: (id) => request.get(`/repair-types/${id}`),
  create: (data) => request.post('/repair-types', data),
  update: (id, data) => request.put(`/repair-types/${id}`, data),
  delete: (id) => request.delete(`/repair-types/${id}`),
  updateStatus: (id, params) => request.put(`/repair-types/status/${id}`, null, { params })
}

// 统计
export const statisticsApi = {
  overview: () => request.get('/statistics/overview'),
  monthly: () => request.get('/statistics/monthly'),
  type: () => request.get('/statistics/type'),
  repairUser: () => request.get('/statistics/repair-user'),
  satisfaction: () => request.get('/statistics/satisfaction')
}

// 图片上传
export const uploadApi = {
  image: (data) => request.post('/upload/image', data)
}
