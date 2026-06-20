<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>高校宿舍维修报修系统</h2>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-link type="primary" @click="$router.push('/register')">还没有账号？去注册</el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import { authApi } from '../api'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const res = await authApi.login(form.value)
      console.log('完整响应:', JSON.stringify(res))
      const data = res.data || res
      console.log('data.role:', data.role, '| data.user.role:', data.user?.role)
      userStore.login(data, data.token)

      ElMessage.success('登录成功')

      const role = data.user?.role || data.role
      console.log('最终role:', role)
      if (role === 'admin') router.push('/admin/home')
      else if (role === 'repair') router.push('/repair/home')
      else router.push('/student/home')
    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error(error.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
}
.login-card h2 {
  text-align: center;
  margin: 0;
  color: #333;
}
</style>
