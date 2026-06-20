<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="header-left">
        <h3>管理员端 - 宿舍报修系统</h3>
      </div>
      <div class="header-right">
        <span>欢迎，{{ userStore.displayName }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="sidebar">
        <el-menu :default-active="$route.path" router>
          <el-menu-item index="/admin/home">
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard">
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/buildings">
            <span>宿舍楼管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/rooms">
            <span>房间管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/types">
            <span>报修类型</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <span>工单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/notices">
            <span>公告管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/evaluations">
            <span>评价查看</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #409eff;
  color: white;
}
.header-left h3 {
  margin: 0;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}
.sidebar {
  background: #f5f5f5;
}
.main-content {
  background: #fff;
}
</style>
