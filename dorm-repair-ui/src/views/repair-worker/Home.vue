<template>
  <div class="repair-home">
    <h2>维修人员首页</h2>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>待处理工单</template>
          <div class="stat-number">{{ stats.pending }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>维修中</template>
          <div class="stat-number">{{ stats.repairing }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>本月完成</template>
          <div class="stat-number">{{ stats.completed }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '../../api'

const stats = ref({ pending: 0, repairing: 0, completed: 0 })

onMounted(async () => {
  try {
    const res = await orderApi.my({ pageNum: 1, pageSize: 100 })
    const orders = res.data.records || []
    stats.value.pending = orders.filter(o => o.status === 3).length
    stats.value.repairing = orders.filter(o => o.status === 4).length
    stats.value.completed = orders.filter(o => o.status === 5).length
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.repair-home h2 {
  margin-bottom: 20px;
}
.stat-number {
  font-size: 36px;
  font-weight: bold;
  text-align: center;
  color: #67c23a;
}
</style>
