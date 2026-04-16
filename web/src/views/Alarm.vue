<template>
  <div class="alarm">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>告警中心</span>
        </div>
      </template>

      <el-table :data="alarmList" style="width: 100%">
        <el-table-column prop="id" label="告警 ID" width="120" />
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="alarmType" label="告警类型" width="160" />
        <el-table-column prop="alarmLevel" label="告警级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getAlarmLevelType(row.alarmLevel)">
              {{ row.alarmLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alarmTime" label="告警时间" min-width="180" />
        <el-table-column prop="status" label="处理状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === '已处理' ? 'success' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small">查看</el-button>
            <el-button v-if="row.status === '未处理'" size="small" type="primary">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const alarmList = ref([
  { id: 'AL001', deviceName: '1 号皮带机', alarmType: '温度异常', alarmLevel: '严重', alarmTime: '2026-03-21 10:00:00', status: '未处理' },
  { id: 'AL002', deviceName: '主破碎机', alarmType: '振动异常', alarmLevel: '一般', alarmTime: '2026-03-21 09:30:00', status: '已处理' },
  { id: 'AL003', deviceName: '振动筛', alarmType: '电流异常', alarmLevel: '严重', alarmTime: '2026-03-21 08:15:00', status: '已处理' },
  { id: 'AL004', deviceName: '循环水泵', alarmType: '压力异常', alarmLevel: '一般', alarmTime: '2026-03-21 07:45:00', status: '未处理' },
])

const getAlarmLevelType = (level: string) => {
  switch (level) {
    case '严重':
      return 'danger'
    case '一般':
      return 'warning'
    case '提示':
      return 'info'
    default:
      return 'info'
  }
}
</script>

<style scoped>
.alarm {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
