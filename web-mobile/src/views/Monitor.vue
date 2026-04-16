<template>
  <div class="monitor">
    <el-header class="header">
      <el-button type="primary" plain @click="goBack" icon="el-icon-arrow-left">返回</el-button>
      <h1>实时监控</h1>
    </el-header>
    <el-main class="main">
      <el-loading v-loading="loading" element-loading-text="加载中...">
        <el-card class="monitor-card">
          <template #header>
            <div class="card-header">
              <span>实时数据</span>
            </div>
          </template>
          <el-list>
            <el-list-item v-for="data in realTimeData.deviceData" :key="data.id">
              <template #default>
                <div class="data-item">
                  <div class="data-info">
                    <div class="data-name">{{ data.pointName || '测点' }}</div>
                    <div class="data-value">{{ data.value }}</div>
                  </div>
                  <div class="data-time">{{ formatTime(data.collectionTime) }}</div>
                </div>
              </template>
            </el-list-item>
          </el-list>
        </el-card>
      </el-loading>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const loading = ref(true)
const realTimeData = ref({ deviceData: [] })
let interval: number | null = null

const goBack = () => {
  router.push('/')
}

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString()
}

const fetchRealTimeData = async () => {
  try {
    const res = await axios.get('/api/dashboard/real-time-data')
    realTimeData.value = res.data
  } catch (error) {
    console.error('获取实时数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRealTimeData()
  // 每5秒刷新一次数据
  interval = window.setInterval(fetchRealTimeData, 5000)
})

onUnmounted(() => {
  if (interval) {
    clearInterval(interval)
  }
})
</script>

<style scoped>
.monitor {
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

.monitor-card {
  margin-bottom: 1rem;
}

.card-header {
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.data-info {
  flex: 1;
}

.data-name {
  font-size: 0.9rem;
  color: #606266;
  margin-bottom: 0.25rem;
}

.data-value {
  font-size: 1.2rem;
  font-weight: bold;
  color: #409EFF;
}

.data-time {
  font-size: 0.8rem;
  color: #909399;
}
</style>
