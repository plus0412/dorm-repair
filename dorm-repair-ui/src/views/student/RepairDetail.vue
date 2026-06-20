<template>
  <div class="repair-detail">
    <h2>工单详情</h2>
    <el-card v-if="order">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单编号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修标题">{{ order.title }}</el-descriptions-item>
        <el-descriptions-item label="报修类型">{{ order.repairTypeName }}</el-descriptions-item>
        <el-descriptions-item label="宿舍楼">{{ order.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ order.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="报修描述" :span="2">{{ order.description }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatDateTime(order.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="派单时间">{{ formatDateTime(order.assignTime) }}</el-descriptions-item>
        <el-descriptions-item label="开始维修时间">{{ formatDateTime(order.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ formatDateTime(order.finishTime) }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>操作记录</el-divider>

      <el-timeline>
        <el-timeline-item v-for="record in records" :key="record.id" :timestamp="formatDateTime(record.createTime)" placement="top">
          <p>{{ record.operation }}{{ record.remark ? ` - ${record.remark}` : '' }}</p>
        </el-timeline-item>
      </el-timeline>

      <div style="margin-top: 20px">
        <el-button @click="$router.back()">返回</el-button>
        <el-button v-if="order.status === 1" type="warning" @click="$router.push(`/student/submit/${order.id}`)">重新提交</el-button>
        <el-button v-if="order.status === 5" type="success" @click="$router.push(`/student/evaluate/${order.id}`)">去评价</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { orderApi, recordApi } from '../../api'
import { formatDateTime } from '../../utils/format'

const route = useRoute()
const order = ref(null)
const records = ref([])

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

onMounted(async () => {
  try {
    const [orderRes, recordRes] = await Promise.all([
      orderApi.detail(route.params.id),
      recordApi.byOrder(route.params.id, { pageNum: 1, pageSize: 100 })
    ])
    order.value = orderRes.data
    records.value = recordRes.data.records || []
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.repair-detail h2 {
  margin-bottom: 20px;
}
</style>

