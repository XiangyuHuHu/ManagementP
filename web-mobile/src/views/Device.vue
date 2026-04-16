<template>
  <div class="device">
    <el-header class="header">
      <el-button type="primary" plain @click="goBack" icon="el-icon-arrow-left">返回</el-button>
      <h1>设备管理</h1>
    </el-header>
    <el-main class="main">
      <el-loading v-loading="loading" element-loading-text="加载中...">
        <el-card class="device-card">
          <template #header>
            <div class="card-header">
              <span>设备统计</span>
            </div>
          </template>
          <el-grid :cols="2" :gutter="10">
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ deviceStats.statusDistribution?.['运行中'] || 0 }}</div>
                <div class="stat-label">运行中</div>
              </div>
            </el-grid-item>
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ deviceStats.statusDistribution?.['待机'] || 0 }}</div>
                <div class="stat-label">待机</div>
              </div>
            </el-grid-item>
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ deviceStats.statusDistribution?.['故障'] || 0 }}</div>
                <div class="stat-label">故障</div>
              </div>
            </el-grid-item>
            <el-grid-item>
              <div class="stat-item">
                <div class="stat-value">{{ deviceStats.statusDistribution?.['维护'] || 0 }}</div>
                <div class="stat-label">维护</div>
              </div>
            </el-grid-item>
          </el-grid>
        </el-card>

        <el-card class="device-card" style="margin-top: 1rem;">
          <template #header>
            <div class="card-header">
              <span>设备类型分布</span>
            </div>
          </template>
          <el-list>
            <el-list-item v-for="(count, type) in deviceStats.typeDistribution" :key="type">
              <template #default>
                <div class="type-item">
                  <div class="type-name">{{ type }}</div>
                  <div class="type-count">{{ count }}</div>
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
const deviceStats = ref({ statusDistribution: {}, typeDistribution: {} })

const goBack = () => {
  router.push('/')
}

const fetchData = async () => {
  try {
    loading.value = true
    const res = await axios.get('/api/dashboard/device-stats')
    deviceStats.value = res.data
  } catch (error) {
    console.error('获取设备数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.device {
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

.device-card {
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

.type-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.type-name {
  font-weight: bold;
}

.type-count {
  font-size: 1.1rem;
  color: #409EFF;
  font-weight: bold;
}
</style>
