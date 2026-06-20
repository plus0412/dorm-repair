<template>
  <div class="student-home">
    <h2>学生首页</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <template #header>我的报修总数</template>
          <div class="stat-number">{{ stats.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>待审核</template>
          <div class="stat-number">{{ stats.pending }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>维修中</template>
          <div class="stat-number">{{ stats.repairing }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>已完成</template>
          <div class="stat-number">{{ stats.completed }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>最新公告</template>
      <el-table :data="notices" stripe>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi, noticeApi } from '../../api'
import { formatDateTime } from '../../utils/format'

const stats = ref({ total: 0, pending: 0, repairing: 0, completed: 0 })
const notices = ref([])

onMounted(async () => {
  try {
    const orderRes = await orderApi.my({ pageNum: 1, pageSize: 100 })
    const orders = orderRes.data.records || []
    stats.value.total = orderRes.data.total || 0
    stats.value.pending = orders.filter(item => item.status === 0).length
    stats.value.repairing = orders.filter(item => [3, 4].includes(item.status)).length
    stats.value.completed = orders.filter(item => [5, 6].includes(item.status)).length

    const noticeRes = await noticeApi.list({ pageNum: 1, pageSize: 5 })
    notices.value = noticeRes.data.records || []
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.student-home h2 {
  margin-bottom: 20px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  text-align: center;
  color: #409eff;
}
</style>
