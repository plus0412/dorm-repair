<template>
  <div class="building-manage">
    <h2>宿舍楼管理</h2>
    <el-card>
      <el-form inline>
        <el-form-item>
          <el-button type="primary" @click="showDialog('add')">新增宿舍楼</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe>
        <el-table-column prop="name" label="楼栋名称" />
        <el-table-column prop="description" label="描述" />
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="楼栋名称" prop="name">
          <el-input v-model="form.name" placeholder="如：1号宿舍楼" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
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
import { buildingApi } from '../../api'
import { formatDateTime } from '../../utils/format'

const tableData = ref([])
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 10 })
const dialogVisible = ref(false)
const dialogTitle = ref('新增宿舍楼')
const formRef = ref()
const form = ref({ id: null, name: '', description: '' })

const rules = {
  name: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }]
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    const res = await buildingApi.page(queryParams.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const showDialog = (type, row = {}) => {
  dialogTitle.value = type === 'add' ? '新增宿舍楼' : '编辑宿舍楼'
  form.value = row.id ? { ...row } : { id: null, name: '', description: '' }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async valid => {
    if (!valid) return
    try {
      if (form.value.id) {
        await buildingApi.update(form.value.id, form.value)
        ElMessage.success('更新成功')
      } else {
        await buildingApi.create(form.value)
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
        await buildingApi.delete(id)
        ElMessage.success('删除成功')
        loadData()
      } catch (error) {
        console.error(error)
      }
    })
    .catch(() => {})
}
</script>
