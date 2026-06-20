import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/login' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/student',
    component: () => import('../views/student/Layout.vue'),
    meta: { requiresAuth: true, role: 'student' },
    children: [
      { path: 'home', name: 'StudentHome', component: () => import('../views/student/Home.vue'), meta: { title: '首页' } },
      { path: 'submit/:id?', name: 'RepairSubmit', component: () => import('../views/student/RepairSubmit.vue'), meta: { title: '提交报修' } },
      { path: 'list', name: 'RepairList', component: () => import('../views/student/RepairList.vue'), meta: { title: '我的报修' } },
      { path: 'detail/:id', name: 'StudentRepairDetail', component: () => import('../views/student/RepairDetail.vue'), meta: { title: '工单详情' } },
      { path: 'evaluate/:id', name: 'Evaluate', component: () => import('../views/student/Evaluate.vue'), meta: { title: '评价' } }
    ]
  },
  {
    path: '/repair',
    component: () => import('../views/repair-worker/Layout.vue'),
    meta: { requiresAuth: true, role: 'repair' },
    children: [
      { path: 'home', name: 'RepairHome', component: () => import('../views/repair-worker/Home.vue'), meta: { title: '首页' } },
      { path: 'orders', name: 'RepairOrderList', component: () => import('../views/repair-worker/OrderList.vue'), meta: { title: '待维修工单' } },
      { path: 'detail/:id', name: 'RepairOrderDetail', component: () => import('../views/repair-worker/OrderDetail.vue'), meta: { title: '工单详情' } },
      { path: 'history', name: 'RepairHistory', component: () => import('../views/repair-worker/History.vue'), meta: { title: '历史记录' } }
    ]
  },
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    meta: { requiresAuth: true, role: 'admin' },
    children: [
      { path: 'home', name: 'AdminHome', component: () => import('../views/admin/Home.vue'), meta: { title: '首页' } },
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '数据统计' } },
      { path: 'users', name: 'UserManage', component: () => import('../views/admin/UserManage.vue'), meta: { title: '用户管理' } },
      { path: 'buildings', name: 'BuildingManage', component: () => import('../views/admin/BuildingManage.vue'), meta: { title: '宿舍楼管理' } },
      { path: 'rooms', name: 'RoomManage', component: () => import('../views/admin/RoomManage.vue'), meta: { title: '房间管理' } },
      { path: 'types', name: 'RepairTypeManage', component: () => import('../views/admin/RepairTypeManage.vue'), meta: { title: '报修类型管理' } },
      { path: 'orders', name: 'OrderManage', component: () => import('../views/admin/OrderManage.vue'), meta: { title: '工单管理' } },
      { path: 'order-detail/:id', name: 'AdminOrderDetail', component: () => import('../views/admin/OrderDetailFixed.vue'), meta: { title: '工单详情' } },
      { path: 'notices', name: 'NoticeManage', component: () => import('../views/admin/NoticeManage.vue'), meta: { title: '公告管理' } },
      { path: 'evaluations', name: 'EvaluationList', component: () => import('../views/admin/EvaluationList.vue'), meta: { title: '评价查看' } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? to.meta.title + ' - 宿舍报修系统' : '宿舍报修系统'
  next()
})

export default router
