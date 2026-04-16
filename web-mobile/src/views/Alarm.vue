<template>
  <div class="alarm">
    <el-header class="header">
      <el-button type="primary" plain @click="goBack" icon="el-icon-arrow-left">返回</el-button>
      <h1>报警中心</h1>
    </el-header>
    <el-main class="main">
      <el-loading v-loading="loading" element-loading-text="加载中...">
        <el-card class="alarm-card">
          <template #header>
            <div class="card-header">
              <span>报警统计</span>
            </div>
          </template>
          <el-grid :cols="2" :gutter="10">
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ alarmStats.levelDistribution?.['紧急'] || 0 }}</div>
                <div class="stat-label">紧急</div>
              </div>
            </el-grid-item>
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ alarmStats.levelDistribution?.['重要'] || 0 }}</div>
                <div class="stat-label">重要</div>
              </div>
            </el-grid-item>
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ alarmStats.levelDistribution?.['一般'] || 0 }}</div>
                <div class="stat-label">一般</div>
              </div>
            </el-grid-item>
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ alarmStats.statusDistribution?.['未处理'] || 0 }}</div>
                <div class="stat-label">未处理</div>
              </div>
            </el-grid-item>
          </el-grid>
        </el-card>

        <el-card class="alarm-card" style="margin-top: 1rem;">
          <template #header>
            <div class="card-header">
              <span>最近报警</span>
            </div>
          </template>
          <el-list>
            <el-list-item v-for="alarm in recentAlarms.alarms" :key="alarm.id">
              <template #default>
                <div class="alarm-item">
                  <div class="alarm-info">
                    <div class="alarm-name">{{ alarm.alarmName }}</div>
                    <div class="alarm-device">{{ alarm.deviceName || '设备' }}</div>
                    <div class="alarm-time">{{ formatTime(alarm.createdAt) }}</div>
                  </div>
                  <div class="alarm-level" :class="getLevelClass(alarm.level)">{{ alarm.level }}</div>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const loading = ref(true)
const alarmStats = ref({ levelDistribution: {}, statusDistribution: {} })
const recentAlarms = ref({ alarms: [] })

const goBack = () => {
  router.push('/')
}

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString()
}

const getLevelClass = (level: string) => {
  switch (level) {
    case '紧急': return 'level-emergency'
    case '重要': return 'level-important'
    case '一般': return 'level-normal'
    default: return ''
  }
}

const fetchData = async () => {
  try {
    loading.value = true
    const [statsRes, alarmsRes] = await Promise.all([
      axios.get('/api/dashboard/alarm-stats'),
      axios.get('/api/dashboard/recent-alarms')
    ])
    alarmStats.value = statsRes.data
    recentAlarms.value = alarmsRes.data
  } catch (error) {
    console.error('获取报警数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.alarm {
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

.alarm-card {
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

.alarm-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.alarm-info {
  flex: 1;
  margin-right: 1rem;
}

.alarm-name {
  font-weight: bold;
  margin-bottom: 0.25rem;
}

.alarm-device {
  font-size: 0.9rem;
  color: #606266;
  margin-bottom: 0.25rem;
}

.alarm-time {
  font-size: 0.8rem;
  color: #909399;
}

.alarm-level {
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
  white-space: nowrap;
}

.level-emergency {
  background-color: #fef0f0;
  color: #f56c6c;
}

.level-important {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.level-normal {
  background-color: #f0f9eb;
  color: #67c23a;
}
</style>
