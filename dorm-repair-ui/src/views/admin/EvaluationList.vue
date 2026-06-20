<template>
  <div class="evaluation-list">
    <h2>评价查看</h2>
    <el-card>
      <el-form inline>
        <el-form-item label="评分">
          <el-select
            v-model="queryParams.rating"
            placeholder="全部"
            clearable
            style="width: 100px"
            @change="loadData"
          >
            <el-option v-for="i in 5" :key="i" :label="`${i}星`" :value="i" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe>
        <el-table-column prop="orderId" label="工单ID" width="100" />
        <el-table-column prop="studentName" label="评价学生" />
        <el-table-column prop="repairUserName" label="维修人员" />
        <el-table-column prop="rating" label="评分" width="170" class-name="rating-column">
          <template #default="{ row }">
            <div class="rating-cell">
              <el-rate :model-value="row.rating" :max="5" disabled />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评价内容" min-width="180" />
        <el-table-column prop="createTime" label="评价时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
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
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { evaluationApi } from '../../api'
import { formatDateTime } from '../../utils/format'

const tableData = ref([])
const total = ref(0)
const queryParams = ref({ pageNum: 1, pageSize: 10, rating: null })

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    const res = await evaluationApi.page(queryParams.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped>
.rating-cell {
  display: inline-flex;
  align-items: center;
  min-width: 120px;
}

.evaluation-list :deep(.rating-column .cell) {
  overflow: visible;
  text-overflow: clip;
  white-space: nowrap;
}
</style>
