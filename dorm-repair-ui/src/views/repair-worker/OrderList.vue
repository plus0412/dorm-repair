<template>
  <div class="order-list">
    <h2>待维修工单</h2>
    <el-card>
      <el-table :data="tableData" stripe>
        <el-table-column prop="orderNo" label="工单编号" width="180" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="studentName" label="报修学生" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 3 ? 'warning' : 'primary'">
              {{ row.status === 3 ? '待维修' : '维修中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignTime" label="派单时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row.id)">查看</el-button>
            <el-button v-if="row.status === 3" link type="success" size="small" @click="startRepair(row.id)">开始</el-button>
            <el-button v-if="row.status === 4" link type="warning" size="small" @click="finishRepair(row.id)">完成</el-button>
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
import { ElMessage } from 'element-plus'
import { orderApi } from '../../api'

const router = useRouter()

const tableData = ref([])
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  statusList: '3,4'
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

const startRepair = async id => {
  try {
    await orderApi.start(id)
    ElMessage.success('已开始维修')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const finishRepair = async id => {
  try {
    await orderApi.finish(id)
    ElMessage.success('已完成维修')
    loadData()
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped>
.order-list h2 {
  margin-bottom: 20px;
}
</style>
