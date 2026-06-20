<template>
  <div class="room-manage">
    <h2>房间管理</h2>
    <el-card>
      <el-form inline>
        <el-form-item label="所属宿舍楼">
          <el-select v-model="queryParams.buildingId" placeholder="全部" clearable style="width: 150px" @change="loadData">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="showDialog('add')">新增房间</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe>
        <el-table-column prop="roomNo" label="房间号" />
        <el-table-column prop="buildingName" label="所属宿舍楼" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDialog('edit', row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="deleteRow(row.id)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属宿舍楼" prop="buildingId">
          <el-select v-model="form.buildingId" style="width: 100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="form.roomNo" placeholder="如：101" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { roomApi, buildingApi } from '../../api'
import { formatDateTime } from '../../utils/format'

const tableData = ref([])
const total = ref(0)
const buildings = ref([])
const queryParams = ref({ pageNum: 1, pageSize: 10, buildingId: null })
const dialogVisible = ref(false)
const dialogTitle = ref('新增房间')
const formRef = ref()
const form = ref({ id: null, buildingId: null, roomNo: '' })

const rules = {
  buildingId: [{ required: true, message: '请选择宿舍楼', trigger: 'change' }],
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
}

onMounted(async () => {
  await loadBuildings()
  loadData()
})

const loadBuildings = async () => {
  try {
    const res = await buildingApi.list()
    buildings.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const loadData = async () => {
  try {
    const res = await roomApi.page(queryParams.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const showDialog = (type, row = {}) => {
  dialogTitle.value = type === 'add' ? '新增房间' : '编辑房间'
  form.value = row.id ? { ...row } : { id: null, buildingId: null, roomNo: '' }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async valid => {
    if (!valid) return
    try {
      if (form.value.id) {
        await roomApi.update(form.value.id, form.value)
        ElMessage.success('更新成功')
      } else {
        await roomApi.create(form.value)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) {
      console.error(error)
    }
  })
}

const deleteRow = async id => {
  ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
    .then(async () => {
      try {
        await roomApi.delete(id)
        ElMessage.success('删除成功')
        loadData()
      } catch (error) {
        console.error(error)
      }
    })
    .catch(() => {})
}
</script>
