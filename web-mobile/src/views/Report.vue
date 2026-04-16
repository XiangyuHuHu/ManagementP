<template>
  <div class="report">
    <el-header class="header">
      <el-button type="primary" plain @click="goBack" icon="el-icon-arrow-left">返回</el-button>
      <h1>生产报表</h1>
    </el-header>
    <el-main class="main">
      <el-loading v-loading="loading" element-loading-text="加载中...">
        <el-card class="report-card">
          <template #header>
            <div class="card-header">
              <span>生产数据</span>
            </div>
          </template>
          <el-grid :cols="2" :gutter="10">
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ productionStats.todayProduction || 0 }}</div>
                <div class="stat-label">今日产量</div>
              </div>
            </el-grid-item>
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ productionStats.weekProduction || 0 }}</div>
                <div class="stat-label">本周产量</div>
              </div>
            </el-grid-item>
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ productionStats.monthProduction || 0 }}</div>
                <div class="stat-label">本月产量</div>
              </div>
            </el-grid-item>
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ energyStats.todayEnergy || 0 }}</div>
                <div class="stat-label">今日能耗</div>
              </div>
            </el-grid-item>
          </el-grid>
        </el-card>

        <el-card class="report-card" style="margin-top: 1rem;">
          <template #header>
            <div class="card-header">
              <span>趋势数据</span>
            </div>
          </template>
          <el-select v-model="timeRange" @change="fetchTrendData" style="width: 100%; margin-bottom: 1rem;">
            <el-option label="今日" value="day"></el-option>
            <el-option label="本周" value="week"></el-option>
            <el-option label="本月" value="month"></el-option>
          </el-select>
          <div class="trend-chart">
            <div v-if="trendData.data && trendData.data.length > 0">
              <el-list>
                <el-list-item v-for="(value, index) in trendData.data" :key="index">
                  <template #default>
                    <div class="trend-item">
                      <div class="trend-label">{{ trendData.labels[index] }}</div>
                      <div class="trend-value">{{ value }}</div>
                    </div>
                  </template>
                </el-list-item>
              </el-list>
            </div>
            <div v-else class="no-data">暂无数据</div>
          </div>
        </el-card>
      </el-loading>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const loading = ref(true)
const productionStats = ref({})
const energyStats = ref({})
const trendData = ref({ data: [], labels: [] })
const timeRange = ref('day')

const goBack = () => {
  router.push('/')
}

const fetchProductionData = async () => {
  try {
    const [productionRes, energyRes] = await Promise.all([
      axios.get('/api/dashboard/production-stats'),
      axios.get('/api/dashboard/energy-stats')
    ])
    productionStats.value = productionRes.data
    energyStats.value = energyRes.data
  } catch (error) {
    console.error('获取生产数据失败:', error)
  }
}

const fetchTrendData = async () => {
  try {
    const res = await axios.get(`/api/dashboard/trend-data?type=production&timeRange=${timeRange.value}`)
    trendData.value = res.data.trend
  } catch (error) {
    console.error('获取趋势数据失败:', error)
  }
}

onMounted(async () => {
  loading.value = true
  await fetchProductionData()
  await fetchTrendData()
  loading.value = false
})
</script>

<style scoped>
.report {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  padding: 0.5rem 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header h1 {
  margin: 0;
  font-size: 1.2rem;
  flex: 1;
  text-align: center;
}

.main {
  flex: 1;
  padding: 1rem;
  background-color: #f5f7fa;
}

.report-card {
  margin-bottom: 1rem;
}

.card-header {
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
}

.stat-item {
  background-color: #f0f9ff;
  padding: 1rem;
  border-radius: 8px;
  text-align: center;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 0.9rem;
  color: #606266;
}

.trend-chart {
  min-height: 200px;
}

.trend-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.trend-label {
  font-weight: bold;
}

.trend-value {
  font-size: 1.1rem;
  color: #409EFF;
  font-weight: bold;
}

.no-data {
  text-align: center;
  padding: 2rem;
  color: #909399;
}
</style>
