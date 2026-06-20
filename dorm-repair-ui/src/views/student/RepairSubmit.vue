<template>
  <div class="repair-submit">
    <h2>{{ isResubmit ? '重新提交报修' : '提交报修' }}</h2>
    <el-card>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="报修标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入报修标题" />
        </el-form-item>
        <el-form-item label="报修类型" prop="repairTypeId">
          <el-select v-model="form.repairTypeId" placeholder="请选择报修类型" style="width: 100%">
            <el-option v-for="type in types" :key="type.id" :label="type.typeName" :value="type.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍楼" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择宿舍楼" style="width: 100%" @change="loadRooms">
            <el-option v-for="b in buildings" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍房间" prop="roomId">
          <el-select v-model="form.roomId" placeholder="请选择宿舍房间" style="width: 100%">
            <el-option v-for="r in rooms" :key="r.id" :label="r.roomNo" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述报修内容" />
        </el-form-item>

        <el-form-item v-if="isResubmit && existingImages.length > 0 && uploadFileList.length === 0" label="原报修图片">
          <div class="existing-image-wrap">
            <el-image
              v-for="img in existingImages"
              :key="img.id"
              :src="img.url"
              :preview-src-list="existingImages.map(item => item.url)"
              fit="cover"
              class="existing-image"
            />
          </div>
          <div class="image-tip">不重新上传则保留原图，重新上传后将用新图片覆盖原图片。</div>
        </el-form-item>

        <el-form-item label="上传图片">
          <el-upload
            v-model:file-list="uploadFileList"
            action="#"
            :auto-upload="false"
            :limit="3"
            list-type="picture-card"
            :on-change="syncFiles"
            :on-remove="syncFiles"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            {{ isResubmit ? '重新提交' : '提交报修' }}
          </el-button>
          <el-button @click="$router.push('/student/list')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { buildingApi, orderApi, repairTypeApi, roomApi } from '../../api'
import { splitImageUrls } from '../../utils/format'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const loading = ref(false)
const uploadFileList = ref([])
const files = ref([])
const existingImages = ref([])
const form = ref({
  title: '',
  repairTypeId: null,
  buildingId: null,
  roomId: null,
  description: ''
})

const buildings = ref([])
const rooms = ref([])
const types = ref([])
const isResubmit = computed(() => !!route.params.id)

const rules = {
  title: [{ required: true, message: '请输入报修标题', trigger: 'blur' }],
  repairTypeId: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  buildingId: [{ required: true, message: '请选择宿舍楼', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择宿舍房间', trigger: 'change' }],
  description: [{ required: true, message: '请输入报修描述', trigger: 'blur' }]
}

onMounted(async () => {
  try {
    const [buildingRes, typeRes] = await Promise.all([
      buildingApi.list(),
      repairTypeApi.list()
    ])
    buildings.value = buildingRes.data || []
    types.value = typeRes.data || []

    if (isResubmit.value) {
      await loadRejectedOrder()
    }
  } catch (error) {
    console.error(error)
  }
})

const loadRejectedOrder = async () => {
  const res = await orderApi.detail(route.params.id)
  const order = res.data
  form.value = {
    title: order.title,
    repairTypeId: order.repairTypeId,
    buildingId: order.buildingId,
    roomId: order.roomId,
    description: order.description
  }
  existingImages.value = splitImageUrls(order.images)
  await loadRooms(order.roomId)
}

const loadRooms = async (selectedRoomId = null) => {
  if (!form.value.buildingId) {
    rooms.value = []
    form.value.roomId = null
    return
  }

  try {
    const res = await roomApi.list({ buildingId: form.value.buildingId })
    rooms.value = res.data || []
    form.value.roomId = selectedRoomId ?? form.value.roomId
  } catch (error) {
    console.error(error)
  }
}

const syncFiles = (file, fileList) => {
  uploadFileList.value = fileList
  files.value = fileList.map(item => item.raw).filter(Boolean)
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async valid => {
    if (!valid) return

    loading.value = true
    try {
      const formData = new FormData()
      formData.append('order', new Blob([JSON.stringify(form.value)], { type: 'application/json' }))
      files.value.forEach(file => formData.append('images', file))

      if (isResubmit.value) {
        await orderApi.resubmit(route.params.id, formData)
      } else {
        await orderApi.submit(formData)
      }

      ElMessage.success(isResubmit.value ? '重新提交成功' : '提交成功')
      router.push('/student/list')
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.repair-submit h2 {
  margin-bottom: 20px;
}

.existing-image-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.existing-image {
  width: 120px;
  height: 120px;
  border-radius: 6px;
}

.image-tip {
  width: 100%;
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
}
</style>
