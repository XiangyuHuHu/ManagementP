<template>
  <div class="monitor">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>实时监测</span>
        </div>
      </template>

      <div class="monitor-content">
        <div v-for="item in monitorList" :key="item.id" class="monitor-item">
          <el-card :body-style="{ padding: '15px' }">
            <div class="item-header">
              <span class="item-name">{{ item.name }}</span>
              <el-tag :type="item.status === '正常' ? 'success' : 'danger'">
                {{ item.status }}
              </el-tag>
            </div>
            <div class="item-body">
              <div v-for="data in item.data" :key="data.name" class="item-data">
                <span class="data-label">{{ data.name }}:</span>
                <span class="data-value">{{ data.value }} {{ data.unit }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const monitorList = ref([
  {
    id: 'DEV001',
    name: '主破碎机',
    status: '正常',
    data: [
      { name: '温度', value: 45, unit: '°C' },
      { name: '振动', value: 2.5, unit: 'mm/s' },
      { name: '电流', value: 120, unit: 'A' },
    ],
  },
  {
    id: 'DEV002',
    name: '振动筛',
    status: '正常',
    data: [
      { name: '温度', value: 42, unit: '°C' },
      { name: '振动', value: 1.8, unit: 'mm/s' },
      { name: '电流', value: 80, unit: 'A' },
    ],
  },
  {
    id: 'DEV003',
    name: '1 号皮带机',
    status: '异常',
    data: [
      { name: '温度', value: 55, unit: '°C' },
      { name: '振动', value: 3.2, unit: 'mm/s' },
      { name: '电流', value: 150, unit: 'A' },
    ],
  },
  {
    id: 'DEV004',
    name: '循环水泵',
    status: '正常',
    data: [
      { name: '温度', value: 40, unit: '°C' },
      { name: '压力', value: 1.2, unit: 'MPa' },
      { name: '电流', value: 60, unit: 'A' },
    ],
  },
])
</script>

<style scoped>
.monitor {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.monitor-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.item-name {
  font-size: 16px;
  font-weight: bold;
}

.item-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.item-data {
  display: flex;
  justify-content: space-between;
}

.data-label {
  color: #e7f0f9;
}

.data-value {
  font-weight: bold;
  color: #ffffff;
}
</style>
