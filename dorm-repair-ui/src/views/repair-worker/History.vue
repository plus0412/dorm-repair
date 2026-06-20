<template>
  <div class="history">
    <h2>历史维修记录</h2>
    <el-card>
      <el-table :data="tableData" stripe>
        <el-table-column prop="orderNo" label="工单编号" width="180" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="studentName" label="报修学生" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag type="success">{{ row.status === 6 ? '已评价' : '已完成' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="finishTime" label="完成时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.finishTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row.id)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../../api'
import { formatDateTime } from '../../utils/format'

const router = useRouter()

const tableData = ref([])
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  statusList: '5,6'
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    const res = await orderApi.page(queryParams.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const viewDetail = id => {
  router.push(`/repair/detail/${id}`)
}
</script>

<style scoped>
.history h2 {
  margin-bottom: 20px;
}
</style>
