<template>
  <div class="order-manage">
    <h2>工单管理</h2>
    <el-card>
      <el-form inline>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px" @change="loadData">
            <el-option label="待审核" :value="0" />
            <el-option label="审核驳回" :value="1" />
            <el-option label="待派单" :value="2" />
            <el-option label="待维修" :value="3" />
            <el-option label="维修中" :value="4" />
            <el-option label="已完成" :value="5" />
            <el-option label="已评价" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe>
        <el-table-column prop="orderNo" label="工单编号" width="180" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="studentName" label="报修学生" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row.id)">查看</el-button>
            <el-button v-if="row.status === 0" link type="success" size="small" @click="auditOrder(row, true)">通过</el-button>
            <el-button v-if="row.status === 0" link type="danger" size="small" @click="showRejectDialog(row)">驳回</el-button>
            <el-button v-if="row.status === 2" link type="warning" size="small" @click="showAssignDialog(row)">派单</el-button>
            <el-button v-if="row.status === 1 || row.status >= 5" link type="danger" size="small" @click="deleteOrder(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end"
        @current-change="loadData"
      />
    </el-card>

    <!-- 驳回对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="驳回工单" width="400px">
      <el-form :model="rejectForm">
        <el-form-item label="驳回原因" required>
          <el-input v-model="rejectForm.remark" type="textarea" :rows="3" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定驳回</el-button>
      </template>
    </el-dialog>

    <!-- 派单对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配维修人员" width="400px">
      <el-form :model="assignForm">
        <el-form-item label="选择维修人员" required>
          <el-select v-model="assignForm.repairUserId" placeholder="请选择" style="width: 100%">
            <el-option v-for="u in repairUsers" :key="u.id" :label="u.realName" :value="u.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确定派单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, userApi } from '../../api'

const router = useRouter()

const tableData = ref([])
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 10, status: null })
const repairUsers = ref([])

const rejectDialogVisible = ref(false)
const rejectForm = ref({ id: null, remark: '' })
const assignDialogVisible = ref(false)
const assignForm = ref({ id: null, repairUserId: null })

const statusMap = {
  0: { text: '待审核', type: 'info' }, 1: { text: '审核驳回', type: 'danger' },
  2: { text: '待派单', type: 'warning' }, 3: { text: '待维修', type: 'warning' },
  4: { text: '维修中', type: 'primary' }, 5: { text: '已完成', type: 'success' }, 6: { text: '已评价', type: 'success' }
}
const getStatusText = (s) => statusMap[s]?.text || '未知'
const getStatusType = (s) => statusMap[s]?.type || 'info'

onMounted(() => { loadData(); loadRepairUsers() })

const loadData = async () => {
  try {
    const res = await orderApi.page(queryParams.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) { console.error(error) }
}

const loadRepairUsers = async () => {
  try {
    const res = await userApi.page({ pageNum: 1, pageSize: 100, role: 'repair' })
    repairUsers.value = res.data.records || []
  } catch (error) { console.error(error) }
}

const viewDetail = (id) => { router.push(`/admin/order-detail/${id}`) }

const auditOrder = async (row, approved) => {
  try {
    await orderApi.audit(row.id, { approved, auditRemark: '' })
    ElMessage.success('审核成功')
    loadData()
  } catch (error) { console.error(error) }
}

const showRejectDialog = (row) => {
  rejectForm.value = { id: row.id, remark: '' }
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.value.remark) { ElMessage.warning('请输入驳回原因'); return }
  try {
    await orderApi.audit(rejectForm.value.id, { approved: false, auditRemark: rejectForm.value.remark })
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
}

const showAssignDialog = (row) => {
  assignForm.value = { id: row.id, repairUserId: null }
  assignDialogVisible.value = true
}

const confirmAssign = async () => {
  if (!assignForm.value.repairUserId) { ElMessage.warning('请选择维修人员'); return }
  try {
    await orderApi.assign(assignForm.value.id, { repairUserId: assignForm.value.repairUserId })
    ElMessage.success('派单成功')
    assignDialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
}

const deleteOrder = async (id) => {
  ElMessageBox.confirm('确定要删除该工单吗？', '提示', { type: 'warning' })
    .then(async () => {
      try {
        await orderApi.delete(id)
        ElMessage.success('删除成功')
        loadData()
      } catch (error) { console.error(error) }
    }).catch(() => {})
}
</script>
