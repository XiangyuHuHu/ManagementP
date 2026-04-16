<template>
  <div class="shift-page">
    <CoalQuickBar title="排班管理" subtitle="对应最新需求中的班组管理、班次管理、班组关联、基础排班和我的排班，支持生产班配置。" />

    <section class="page-shell">
      <div class="page-head">
        <div>
          <h1>班组与班次配置</h1>
          <p>支持倒班和值班制度设置，指定生产班，作为生产计划、报表和统计数据的统一班制依据。</p>
        </div>
        <div class="page-actions">
          <el-date-picker v-model="query.effectiveDate" type="date" value-format="YYYY-MM-DD" placeholder="生效日期" />
          <el-button @click="loadRows">查询</el-button>
        </div>
      </div>

      <div class="kpi-grid">
        <article class="kpi-card">
          <span>生产班数量</span>
          <strong>{{ summary.productionTeams }}</strong>
          <small>按当前生效班制</small>
        </article>
        <article class="kpi-card">
          <span>值守班数量</span>
          <strong>{{ summary.supportTeams }}</strong>
          <small>机电与后勤保障</small>
        </article>
        <article class="kpi-card">
          <span>生效排班</span>
          <strong>{{ summary.activeCount }}</strong>
          <small>当前状态为生效中</small>
        </article>
        <article class="kpi-card">
          <span>生产班占比</span>
          <strong>{{ summary.productionRatio }}%</strong>
          <small>用于生产数据归属</small>
        </article>
      </div>

      <div class="panel-grid">
        <article class="panel">
          <div class="panel-head">
            <h2>倒班结构</h2>
          </div>
          <div ref="shiftChartRef" class="chart-box"></div>
        </article>

        <article class="panel">
          <div class="panel-head">
            <h2>排班明细</h2>
          </div>
          <el-table :data="rows" class="dark-table">
            <el-table-column prop="teamName" label="班组" width="110" />
            <el-table-column prop="shiftName" label="班次" width="100" />
            <el-table-column prop="shiftType" label="类型" width="100" />
            <el-table-column prop="startTime" label="开始" width="90" />
            <el-table-column prop="endTime" label="结束" width="90" />
            <el-table-column prop="productionShift" label="生产班" width="90">
              <template #default="{ row }">{{ row.productionShift ? '是' : '否' }}</template>
            </el-table-column>
            <el-table-column prop="effectiveDate" label="生效日期" width="120" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column prop="remark" label="说明" min-width="180" />
          </el-table>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import CoalQuickBar from '../../components/coal/CoalQuickBar.vue'
import { listShiftSchedules, type ShiftScheduleDto } from '../../api/coal-business'
import { echarts } from '../../utils/echarts'

const query = reactive({
  effectiveDate: '2026-04-01',
})

const rows = ref<ShiftScheduleDto[]>([])
const shiftChartRef = ref<HTMLElement | null>(null)
let shiftChart: any = null

const fallbackRows: ShiftScheduleDto[] = [
  { id: 1, teamName: '一班', shiftName: '早班', shiftType: '生产班', startTime: '06:00', endTime: '14:00', productionShift: true, effectiveDate: '2026-04-01', status: '生效中', remark: '主洗系统生产班' },
  { id: 2, teamName: '二班', shiftName: '中班', shiftType: '生产班', startTime: '14:00', endTime: '22:00', productionShift: true, effectiveDate: '2026-04-01', status: '生效中', remark: '筛分与压滤联动' },
  { id: 3, teamName: '三班', shiftName: '夜班', shiftType: '生产班', startTime: '22:00', endTime: '06:00', productionShift: true, effectiveDate: '2026-04-01', status: '生效中', remark: '夜间巡检加强' },
  { id: 4, teamName: '机电班', shiftName: '白班', shiftType: '值守班', startTime: '08:00', endTime: '17:30', productionShift: false, effectiveDate: '2026-04-01', status: '生效中', remark: '设备保障与检修' },
]

const summary = computed(() => {
  const data = rows.value.length ? rows.value : fallbackRows
  const productionTeams = data.filter((item) => item.productionShift).length
  const supportTeams = data.length - productionTeams
  const activeCount = data.filter((item) => item.status === '生效中').length
  return {
    productionTeams,
    supportTeams,
    activeCount,
    productionRatio: ((productionTeams / data.length) * 100).toFixed(1),
  }
})

const renderChart = () => {
  const data = rows.value.length ? rows.value : fallbackRows
  if (shiftChartRef.value) {
    shiftChart ??= echarts.init(shiftChartRef.value)
    shiftChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0, textStyle: { color: '#dbe7ff' } },
      series: [
        {
          type: 'pie',
          radius: ['48%', '70%'],
          data: [
            { value: data.filter((item) => item.productionShift).length, name: '生产班', itemStyle: { color: '#57d8ff' } },
            { value: data.filter((item) => !item.productionShift).length, name: '值守班', itemStyle: { color: '#25d39c' } },
          ],
          label: { color: '#eef6ff' },
        },
      ],
    })
  }
}

const loadRows = async () => {
  try {
    rows.value = await listShiftSchedules({ effectiveDate: query.effectiveDate })
  } catch {
    rows.value = fallbackRows
  }
  await nextTick()
  renderChart()
}

onMounted(() => {
  loadRows()
  window.addEventListener('resize', renderChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', renderChart)
  shiftChart?.dispose()
})
</script>

<style scoped>
.shift-page {
  min-height: 100vh;
  padding: 0 20px 24px;
  background: #091019;
  color: #eef6ff;
}

.page-shell {
  width: min(100%, 1680px);
  margin: 0 auto;
}

.page-head,
.kpi-grid,
.panel-grid {
  margin-bottom: 16px;
}

.page-head {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 28px 30px;
  border: 1px solid rgba(96, 183, 255, 0.12);
  border-radius: 20px;
  background: rgba(8, 19, 30, 0.92);
}

.page-head h1,
.panel-head h2 {
  margin: 0;
}

.page-head p {
  margin: 10px 0 0;
  color: rgba(227, 239, 250, 0.68);
}

.page-actions {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.kpi-card,
.panel {
  border: 1px solid rgba(96, 183, 255, 0.12);
  border-radius: 18px;
  background: rgba(8, 19, 30, 0.92);
}

.kpi-card {
  padding: 20px;
}

.kpi-card span {
  display: block;
  color: rgba(227, 239, 250, 0.62);
}

.kpi-card strong {
  display: block;
  margin-top: 14px;
  font-size: 34px;
}

.kpi-card small {
  display: block;
  margin-top: 10px;
  color: #67d8ff;
}

.panel-grid {
  display: grid;
  grid-template-columns: 0.7fr 1.3fr;
  gap: 16px;
}

.panel {
  padding: 24px;
}

.panel-head {
  margin-bottom: 18px;
}

.chart-box {
  height: 420px;
}

.dark-table :deep(.el-table),
.dark-table :deep(.el-table__inner-wrapper),
.dark-table :deep(.el-table tr),
.dark-table :deep(.el-table th.el-table__cell),
.dark-table :deep(.el-table td.el-table__cell) {
  background: transparent;
  color: #eef6ff;
}

.dark-table :deep(.el-table__header th.el-table__cell) {
  color: #7ecfff;
}

@media (max-width: 1200px) {
  .kpi-grid,
  .panel-grid {
    grid-template-columns: 1fr;
  }
}
</style>
