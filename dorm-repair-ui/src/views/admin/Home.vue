<template>
  <div class="admin-home">
    <h2>管理员首页</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <template #header>报修总数</template>
          <div class="stat-number">{{ overview.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>待审核</template>
          <div class="stat-number warning">{{ overview.pendingAudit }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>维修中</template>
          <div class="stat-number primary">{{ overview.repairing }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>已完成</template>
          <div class="stat-number success">{{ overview.completed }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { statisticsApi } from '../../api'

const overview = ref({ total: 0, pendingAudit: 0, repairing: 0, completed: 0 })

onMounted(async () => {
  try {
    const res = await statisticsApi.overview()
    overview.value = res.data
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.admin-home h2 {
  margin-bottom: 20px;
}
.stat-number {
  font-size: 32px;
  font-weight: bold;
  text-align: center;
}
.stat-number.warning {
  color: #e6a23c;
}
.stat-number.primary {
  color: #409eff;
}
.stat-number.success {
  color: #67c23a;
}
</style>
