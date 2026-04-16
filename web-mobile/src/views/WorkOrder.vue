<template>
  <div class="workorder">
    <el-header class="header">
      <el-button type="primary" plain @click="goBack" icon="el-icon-arrow-left">返回</el-button>
      <h1>工单管理</h1>
    </el-header>
    <el-main class="main">
      <el-loading v-loading="loading" element-loading-text="加载中...">
        <el-card class="workorder-card">
          <template #header>
            <div class="card-header">
              <span>最近工单</span>
            </div>
          </template>
          <el-list>
            <el-list-item v-for="order in recentWorkOrders.workOrders" :key="order.id">
              <template #default>
                <div class="order-item">
                  <div class="order-info">
                    <div class="order-title">{{ order.title }}</div>
                    <div class="order-no">工单编号: {{ order.orderNo }}</div>
                    <div class="order-time">{{ formatTime(order.createdAt) }}</div>
                  </div>
                  <div class="order-status" :class="getStatusClass(order.status)">{{ order.status }}</div>
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
const recentWorkOrders = ref({ workOrders: [] })

const goBack = () => {
  router.push('/')
}

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString()
}

const getStatusClass = (status: string) => {
  switch (status) {
    case '待处理': return 'status-pending'
    case '处理中': return 'status-processing'
    case '已完成': return 'status-completed'
    case '已关闭': return 'status-closed'
    default: return ''
  }
}

const fetchData = async () => {
  try {
    loading.value = true
    const res = await axios.get('/api/dashboard/recent-work-orders')
    recentWorkOrders.value = res.data
  } catch (error) {
    console.error('获取工单数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.workorder {
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

.workorder-card {
  margin-bottom: 1rem;
}

.card-header {
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.order-info {
  flex: 1;
  margin-right: 1rem;
}

.order-title {
  font-weight: bold;
  margin-bottom: 0.25rem;
}

.order-no {
  font-size: 0.9rem;
  color: #606266;
  margin-bottom: 0.25rem;
}

.order-time {
  font-size: 0.8rem;
  color: #909399;
}

.order-status {
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
  white-space: nowrap;
}

.status-pending {
  background-color: #fef0f0;
  color: #f56c6c;
}

.status-processing {
  background-color: #ecf5ff;
  color: #409EFF;
}

.status-completed {
  background-color: #f0f9eb;
  color: #67c23a;
}

.status-closed {
  background-color: #f5f7fa;
  color: #909399;
}
</style>
