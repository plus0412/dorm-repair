<template>
  <div class="repair-type-manage">
    <h2>报修类型管理</h2>
    <el-card>
      <el-form inline>
        <el-form-item>
          <el-button type="primary" @click="showDialog('add')">新增类型</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe>
        <el-table-column prop="typeName" label="类型名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDialog('edit', row)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'danger' : 'success'" size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="如：水电维修" />
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
import { ElMessage } from 'element-plus'
import { repairTypeApi } from '../../api'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增类型')
const formRef = ref()
const form = ref({ id: null, typeName: '', description: '' })

const rules = { typeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }] }

onMounted(() => { loadData() })

const loadData = async () => {
  try {
    const res = await repairTypeApi.list()
    tableData.value = res.data || []
  } catch (error) { console.error(error) }
}

const showDialog = (type, row = {}) => {
  dialogTitle.value = type === 'add' ? '新增类型' : '编辑类型'
  form.value = row.id ? { ...row } : { id: null, typeName: '', description: '' }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (form.value.id) {
        await repairTypeApi.update(form.value.id, form.value)
        ElMessage.success('更新成功')
      } else {
        await repairTypeApi.create(form.value)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) { console.error(error) }
  })
}

const toggleStatus = async (row) => {
  try {
    await repairTypeApi.updateStatus(row.id, { status: row.status === 1 ? 0 : 1 })
    ElMessage.success('操作成功')
    loadData()
  } catch (error) { console.error(error) }
}
</script>
