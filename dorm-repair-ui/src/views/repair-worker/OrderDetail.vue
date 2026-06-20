<template>
  <div class="order-detail">
    <h2>工单详情</h2>
    <el-card v-if="order">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单编号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(order.status)">
            {{ getStatusText(order.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修学生">{{ order.studentName }}</el-descriptions-item>
        <el-descriptions-item label="报修类型">{{ order.repairTypeName }}</el-descriptions-item>
        <el-descriptions-item label="宿舍位置">{{ order.buildingName }} {{ order.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="派单时间">{{ formatDateTime(order.assignTime) }}</el-descriptions-item>
        <el-descriptions-item label="报修描述" :span="2">{{ order.description }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>操作记录</el-divider>

      <el-timeline>
        <el-timeline-item v-for="record in records" :key="record.id" :timestamp="formatDateTime(record.createTime)" placement="top">
          <p>{{ record.operation }} {{ record.remark ? `- ${record.remark}` : '' }}</p>
        </el-timeline-item>
      </el-timeline>

      <div style="margin-top: 20px">
        <el-button @click="$router.back()">返回</el-button>
        <el-button v-if="order.status === 3" type="success" @click="startRepair">开始维修</el-button>
        <el-button v-if="order.status === 4" type="warning" @click="showFinishDialog = true">完成维修</el-button>
      </div>
    </el-card>

    <el-dialog v-model="showFinishDialog" title="完成维修" width="400px">
      <el-form :model="finishForm">
        <el-form-item label="完成备注">
          <el-input v-model="finishForm.remark" type="textarea" :rows="3" placeholder="请输入完成备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFinishDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmFinish">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { orderApi, recordApi } from '../../api'
import { formatDateTime } from '../../utils/format'

const route = useRoute()
const order = ref(null)
const records = ref([])
const showFinishDialog = ref(false)
const finishForm = ref({ remark: '' })

const statusTextMap = {
  0: '待审核',
  1: '审核驳回',
  2: '待派单',
  3: '待维修',
  4: '维修中',
  5: '已完成',
  6: '已评价',
  7: '已取消'
}

const statusTagMap = {
  0: 'info',
  1: 'danger',
  2: 'warning',
  3: 'warning',
  4: 'primary',
  5: 'success',
  6: 'success',
  7: 'info'
}

const getStatusText = status => statusTextMap[status] || '未知状态'
const getStatusTag = status => statusTagMap[status] || 'info'

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

const startRepair = async () => {
  try {
    await orderApi.start(route.params.id)
    ElMessage.success('已开始维修')
    order.value.status = 4
  } catch (error) {
    console.error(error)
  }
}

const confirmFinish = async () => {
  try {
    await orderApi.finish(route.params.id, { finishRemark: finishForm.value.remark })
    ElMessage.success('已完成维修')
    showFinishDialog.value = false
    order.value.status = 5
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped>
.order-detail h2 {
  margin-bottom: 20px;
}
</style>

