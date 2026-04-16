<template>
  <div class="report">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报表管理</span>
          <el-button type="primary" size="small">生成报表</el-button>
        </div>
      </template>

      <div class="report-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="生产报表" name="production" />
          <el-tab-pane label="煤质报表" name="coal" />
          <el-tab-pane label="设备报表" name="device" />
          <el-tab-pane label="能耗报表" name="energy" />
        </el-tabs>
      </div>

      <div class="report-content" style="margin-top: 20px;">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="margin-bottom: 20px;"
        />
        <el-button type="primary" size="small">查询</el-button>

        <el-table :data="reportData" style="width: 100%; margin-top: 20px;">
          <el-table-column prop="date" label="日期" width="180" />
          <el-table-column prop="value1" label="指标 1" />
          <el-table-column prop="value2" label="指标 2" />
          <el-table-column prop="value3" label="指标 3" />
          <el-table-column label="操作" width="150">
            <template #default>
              <el-button size="small">查看详情</el-button>
              <el-button size="small" type="success">导出</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeTab = ref('production')
const dateRange = ref([])

const reportData = ref([
  { date: '2026-03-21', value1: 1200, value2: 850, value3: 920 },
  { date: '2026-03-20', value1: 1150, value2: 820, value3: 890 },
  { date: '2026-03-19', value1: 1250, value2: 880, value3: 950 },
  { date: '2026-03-18', value1: 1180, value2: 840, value3: 910 },
  { date: '2026-03-17', value1: 1220, value2: 860, value3: 930 },
])
</script>

<style scoped>
.report {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-tabs {
  margin-top: 20px;
}
</style>
