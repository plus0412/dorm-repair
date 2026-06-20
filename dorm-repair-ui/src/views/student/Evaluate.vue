<template>
  <div class="evaluate">
    <h2>评价维修服务</h2>
    <el-card>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="form.rating" show-text :texts="['极差', '差', '一般', '满意', '非常满意']" />
        </el-form-item>
        <el-form-item label="评价内容" prop="comment">
          <el-input v-model="form.comment" type="textarea" :rows="4" placeholder="请分享您的维修体验" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">提交评价</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { evaluationApi } from '../../api'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const loading = ref(false)
const form = ref({
  rating: 5,
  comment: ''
})

const rules = {
  rating: [{ required: true, message: '请选择评分', trigger: 'change' }]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await evaluationApi.submit({
        orderId: route.params.id,
        rating: form.value.rating,
        comment: form.value.comment
      })
      ElMessage.success('评价成功，感谢您的反馈！')
      router.push('/student/list')
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.evaluate h2 {
  margin-bottom: 20px;
}
</style>
