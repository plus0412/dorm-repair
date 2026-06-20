<template>
  <div class="dashboard">
    <h2>数据统计</h2>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <template #header>报修总数</template>
          <div class="stat-number">{{ overview.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>待审核</template>
          <div class="stat-number warning">{{ overview.pendingAudit }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>维修中</template>
          <div class="stat-number primary">{{ overview.repairing }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>已完成</template>
          <div class="stat-number success">{{ overview.completed }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>每月报修数量</template>
          <div ref="monthlyChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>报修类型分布</template>
          <div ref="typeChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>维修人员排行</template>
          <div ref="repairUserChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>满意度分布</template>
          <div ref="satisfactionChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi } from '../../api'

const overview = ref({ total: 0, pendingAudit: 0, repairing: 0, completed: 0 })
const monthlyData = ref([])
const typeData = ref([])
const repairUserData = ref([])
const satisfactionData = ref([])

const monthlyChartRef = ref()
const typeChartRef = ref()
const repairUserChartRef = ref()
const satisfactionChartRef = ref()

let monthlyChart, typeChart, repairUserChart, satisfactionChart

onMounted(async () => {
  try {
    const [overviewRes, monthlyRes, typeRes, repairUserRes, satisfactionRes] = await Promise.all([
      statisticsApi.overview(),
      statisticsApi.monthly(),
      statisticsApi.type(),
      statisticsApi.repairUser(),
      statisticsApi.satisfaction()
    ])

    overview.value = overviewRes.data
    monthlyData.value = monthlyRes.data || []
    typeData.value = typeRes.data || []
    repairUserData.value = repairUserRes.data || []
    satisfactionData.value = satisfactionRes.data || []

    initCharts()
  } catch (error) {
    console.error(error)
  }
})

onUnmounted(() => {
  monthlyChart?.dispose()
  typeChart?.dispose()
  repairUserChart?.dispose()
  satisfactionChart?.dispose()
})

const initCharts = () => {
  // 每月报修数量
  monthlyChart = echarts.init(monthlyChartRef.value)
  monthlyChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: monthlyData.value.map(d => d.month) },
    yAxis: { type: 'value' },
    series: [{ data: monthlyData.value.map(d => d.count), type: 'line', smooth: true, areaStyle: {} }]
  })

  // 报修类型分布
  typeChart = echarts.init(typeChartRef.value)
  typeChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: typeData.value.map(d => ({ name: d.typeName, value: d.count }))
    }]
  })

  // 维修人员排行
  repairUserChart = echarts.init(repairUserChartRef.value)
  repairUserChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: repairUserData.value.map(d => d.repairUserName).reverse() },
    series: [{
      type: 'bar',
      data: repairUserData.value.map(d => d.count).reverse()
    }]
  })

  // 满意度分布
  satisfactionChart = echarts.init(satisfactionChartRef.value)
  satisfactionChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: satisfactionData.value.map(d => `${d.rating}星`), axisLabel: { rotate: 0 } },
    yAxis: { type: 'value' },
    series: [{ data: satisfactionData.value.map(d => d.count), type: 'bar', itemStyle: { color: '#67c23a' } }]
  })
}
</script>

<style scoped>
.dashboard h2 {
  margin-bottom: 20px;
}
.stat-number {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
}
.stat-number.warning { color: #e6a23c; }
.stat-number.primary { color: #409eff; }
.stat-number.success { color: #67c23a; }
</style>
