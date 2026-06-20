<template>
  <div class="repair-list">
    <h2>我的报修</h2>
    <el-card>
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待审核" :value="0" />
            <el-option label="审核驳回" :value="1" />
            <el-option label="待派单" :value="2" />
            <el-option label="待维修" :value="3" />
            <el-option label="维修中" :value="4" />
            <el-option label="已完成" :value="5" />
            <el-option label="已评价" :value="6" />
            <el-option label="已取消" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button type="primary" @click="$router.push('/student/submit')">新增报修</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe>
        <el-table-column prop="orderNo" label="工单编号" width="180" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row.id)">查看</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="resubmitOrder(row.id)">重新提交</el-button>
            <el-button v-if="row.status === 5" link type="success" size="small" @click="goEvaluate(row.id)">去评价</el-button>
            <el-button v-if="row.status === 0" link type="danger" size="small" @click="cancelOrder(row.id)">取消</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '../../api'
import { formatDateTime } from '../../utils/format'

const router = useRouter()

const tableData = ref([])
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  status: null
})

const statusMap = {
  0: { text: '待审核', type: 'info' },
  1: { text: '审核驳回', type: 'danger' },
  2: { text: '待派单', type: 'warning' },
  3: { text: '待维修', type: 'warning' },
  4: { text: '维修中', type: 'primary' },
  5: { text: '已完成', type: 'success' },
  6: { text: '已评价', type: 'success' },
  7: { text: '已取消', type: 'info' }
}

const getStatusText = status => statusMap[status]?.text || '未知'
const getStatusType = status => statusMap[status]?.type || 'info'

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
  router.push(`/student/detail/${id}`)
}

const resubmitOrder = id => {
  router.push(`/student/submit/${id}`)
}

const goEvaluate = id => {
  router.push(`/student/evaluate/${id}`)
}

const cancelOrder = id => {
  ElMessageBox.confirm('确定要取消该报修吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await orderApi.cancel(id)
      ElMessage.success('取消成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}
</script>

<style scoped>
.repair-list h2 {
  margin-bottom: 20px;
}
</style>
