<template>
  <div class="user-manage">
    <h2>用户管理</h2>
    <el-card>
      <el-form inline>
        <el-form-item label="角色">
          <el-select v-model="queryParams.role" placeholder="全部" clearable style="width: 100px">
            <el-option label="管理员" value="admin" />
            <el-option label="学生" value="student" />
            <el-option label="维修" value="repair" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button type="primary" @click="showDialog('add')">新增用户</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{ getRoleText(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="showDialog('edit', row)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'danger' : 'success'" size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="warning" size="small" @click="resetPassword(row.id)">重置密码</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="学生" value="student" />
            <el-option label="维修人员" value="repair" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
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
import { userApi } from '../../api'

const tableData = ref([])
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 10, role: '' })
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref()
const form = ref({ id: null, username: '', password: '', realName: '', role: 'student', phone: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const roleMap = { admin: { text: '管理员', type: 'danger' }, student: { text: '学生', type: '' }, repair: { text: '维修', type: 'warning' } }
const getRoleText = (role) => roleMap[role]?.text || role
const getRoleType = (role) => roleMap[role]?.type || ''

onMounted(() => { loadData() })

const loadData = async () => {
  try {
    const res = await userApi.page(queryParams.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) { console.error(error) }
}

const showDialog = (type, row = {}) => {
  if (type === 'add') {
    dialogTitle.value = '新增用户'
    form.value = { id: null, username: '', password: '', realName: '', role: 'student', phone: '' }
  } else {
    dialogTitle.value = '编辑用户'
    form.value = { ...row }
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (form.value.id) {
        await userApi.update(form.value.id, form.value)
        ElMessage.success('更新成功')
      } else {
        await userApi.create(form.value)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) { console.error(error) }
  })
}

const toggleStatus = async (row) => {
  try {
    await userApi.updateStatus(row.id, { status: row.status === 1 ? 0 : 1 })
    ElMessage.success('操作成功')
    loadData()
  } catch (error) { console.error(error) }
}

const resetPassword = async (id) => {
  ElMessageBox.confirm('确定要重置该用户密码为 123456 吗？', '提示', { type: 'warning' })
    .then(async () => {
      try {
        await userApi.resetPassword(id)
        ElMessage.success('密码已重置为 123456')
      } catch (error) { console.error(error) }
    }).catch(() => {})
}
</script>
