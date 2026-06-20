<template>
  <div class="order-detail">
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
        <el-descriptions-item label="报修学生">{{ order.studentName }}</el-descriptions-item>
        <el-descriptions-item label="维修人员">{{ order.repairUserName || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatDateTime(order.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="派单时间">{{ formatDateTime(order.assignTime) }}</el-descriptions-item>
        <el-descriptions-item label="开始维修时间">{{ formatDateTime(order.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ formatDateTime(order.finishTime) }}</el-descriptions-item>
        <el-descriptions-item label="报修描述" :span="2">{{ order.description }}</el-descriptions-item>
        <el-descriptions-item v-if="order.auditRemark" label="审核备注" :span="2">{{ order.auditRemark }}</el-descriptions-item>
      </el-descriptions>

      <el-divider v-if="images.length > 0">报修图片</el-divider>
      <div v-if="images.length > 0" class="image-list">
        <el-image
          v-for="img in images"
          :key="img.id"
          :src="img.url"
          :preview-src-list="images.map(item => item.url)"
          fit="cover"
          style="width: 120px; height: 120px; border-radius: 4px"
        />
      </div>

      <el-divider v-if="evaluation">用户评价</el-divider>
      <el-card v-if="evaluation" shadow="never" style="margin-bottom: 20px">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="评分">
            <el-rate :model-value="evaluation.score" :max="5" disabled />
          </el-descriptions-item>
          <el-descriptions-item label="评价时间">{{ formatDateTime(evaluation.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="评价内容" :span="2">{{ evaluation.content || '无' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-divider>操作记录</el-divider>

      <el-timeline>
        <el-timeline-item
          v-for="record in records"
          :key="record.id"
          :timestamp="formatDateTime(record.createTime)"
          placement="top"
        >
          <p>{{ record.operation }} {{ record.remark ? `- ${record.remark}` : '' }}</p>
        </el-timeline-item>
      </el-timeline>

      <div style="margin-top: 20px">
        <el-button @click="$router.back()">返回</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { orderApi, recordApi, evaluationApi } from '../../api'
import { formatDateTime, splitImageUrls } from '../../utils/format'

const route = useRoute()
const order = ref(null)
const records = ref([])
const images = ref([])
const evaluation = ref(null)

const statusMap = {
  0: { text: '待审核', type: 'info' },
  1: { text: '审核驳回', type: 'danger' },
  2: { text: '待派单', type: 'warning' },
  3: { text: '待维修', type: 'warning' },
  4: { text: '维修中', type: 'primary' },
  5: { text: '已完成', type: 'success' },
  6: { text: '已评价', type: 'success' }
}

const getStatusText = status => statusMap[status]?.text || '未知'
const getStatusType = status => statusMap[status]?.type || 'info'

onMounted(async () => {
  try {
    const orderRes = await orderApi.detail(route.params.id)
    order.value = orderRes.data
    images.value = splitImageUrls(orderRes.data.images)

    const recordRes = await recordApi.byOrder(route.params.id, { pageNum: 1, pageSize: 100 })
    records.value = recordRes.data.records || []

    try {
      const evalRes = await evaluationApi.byOrder(route.params.id)
      if (evalRes.data) {
        evaluation.value = evalRes.data
      }
    } catch (error) {
      // 评价不存在时忽略
    }
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.order-detail h2 {
  margin-bottom: 20px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}
</style>
