<template>
  <div class="planning-page">
    <CoalQuickBar title="生产计划统计" subtitle="对应最新需求中的年度、月度、日计划和班计划管理，支持按计划口径跟踪主要生产指标。" />

    <section class="page-shell">
      <div class="page-head">
        <div>
          <h1>生产计划与完成率</h1>
          <p>按计划拆解班次目标，并跟踪入洗原煤、精煤、中煤、煤泥、矸石及质量能耗指标。</p>
        </div>
        <div class="page-actions">
          <el-select v-model="query.year" style="width: 120px">
            <el-option :value="2026" label="2026" />
            <el-option :value="2025" label="2025" />
          </el-select>
          <el-select v-model="query.month" style="width: 120px">
            <el-option v-for="item in 12" :key="item" :value="item" :label="`${item}月`" />
          </el-select>
          <el-button @click="loadPlans">刷新</el-button>
        </div>
      </div>

      <div class="kpi-grid">
        <article class="kpi-card">
          <span>当日入洗计划</span>
          <strong>{{ daySummary.rawCoalTarget }} 吨</strong>
          <small>班计划合计</small>
        </article>
        <article class="kpi-card">
          <span>当日精煤计划</span>
          <strong>{{ daySummary.cleanCoalTarget }} 吨</strong>
          <small>当前完成率 {{ daySummary.completionRate }}%</small>
        </article>
        <article class="kpi-card">
          <span>计划灰分目标</span>
          <strong>{{ daySummary.ashTarget }}%</strong>
          <small>水分目标 {{ daySummary.moistureTarget }}%</small>
        </article>
        <article class="kpi-card">
          <span>计划能耗目标</span>
          <strong>{{ daySummary.energyTarget }}</strong>
          <small>按吨煤折算</small>
        </article>
      </div>

      <div class="panel-grid">
        <article class="panel">
          <div class="panel-head">
            <h2>班计划拆解</h2>
          </div>
          <el-table :data="plans" class="dark-table">
            <el-table-column prop="planDate" label="计划日期" width="120" />
            <el-table-column prop="shift" label="班次" width="100" />
            <el-table-column prop="rawCoalTarget" label="入洗原煤" width="110" />
            <el-table-column prop="cleanCoalTarget" label="精煤" width="90" />
            <el-table-column prop="middlingTarget" label="中煤/混煤" width="110" />
            <el-table-column prop="slimeTarget" label="煤泥" width="90" />
            <el-table-column prop="gangueTarget" label="矸石" width="90" />
            <el-table-column prop="ashTarget" label="灰分目标" width="100" />
            <el-table-column prop="moistureTarget" label="水分目标" width="100" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column prop="remark" label="说明" min-width="200" />
          </el-table>
        </article>

        <article class="panel">
          <div class="panel-head">
            <h2>计划结构对比</h2>
          </div>
          <div ref="planChartRef" class="chart-box"></div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import CoalQuickBar from '../../components/coal/CoalQuickBar.vue'
import { listProductionPlans, type ProductionPlanDto } from '../../api/coal-business'
import { echarts } from '../../utils/echarts'

const query = reactive({
  year: 2026,
  month: 4,
})

const plans = ref<ProductionPlanDto[]>([])
const planChartRef = ref<HTMLElement | null>(null)
let planChart: any = null

const fallbackPlans: ProductionPlanDto[] = [
  { id: 1, planYear: 2026, planMonth: 4, planDate: '2026-04-15', shift: '早班', rawCoalTarget: 2900, cleanCoalTarget: 2180, middlingTarget: 320, slimeTarget: 180, gangueTarget: 220, ashTarget: 9.5, moistureTarget: 7.8, energyTarget: 12.1, status: '执行中', remark: '与月计划一致' },
  { id: 2, planYear: 2026, planMonth: 4, planDate: '2026-04-15', shift: '中班', rawCoalTarget: 3000, cleanCoalTarget: 2250, middlingTarget: 330, slimeTarget: 175, gangueTarget: 245, ashTarget: 9.6, moistureTarget: 7.7, energyTarget: 12.0, status: '待执行', remark: '根据原煤波动可调整' },
  { id: 3, planYear: 2026, planMonth: 4, planDate: '2026-04-15', shift: '夜班', rawCoalTarget: 2800, cleanCoalTarget: 2100, middlingTarget: 300, slimeTarget: 190, gangueTarget: 210, ashTarget: 9.7, moistureTarget: 7.9, energyTarget: 12.3, status: '待执行', remark: '注意矸石产量控制' },
]

const daySummary = computed(() => {
  const rows = plans.value.length ? plans.value : fallbackPlans
  const rawCoalTarget = rows.reduce((sum, item) => sum + Number(item.rawCoalTarget || 0), 0)
  const cleanCoalTarget = rows.reduce((sum, item) => sum + Number(item.cleanCoalTarget || 0), 0)
  return {
    rawCoalTarget,
    cleanCoalTarget,
    ashTarget: rows[0]?.ashTarget ?? 9.5,
    moistureTarget: rows[0]?.moistureTarget ?? 7.8,
    energyTarget: rows[0]?.energyTarget ?? 12.1,
    completionRate: 78.6,
  }
})

const renderChart = () => {
  const rows = plans.value.length ? plans.value : fallbackPlans
  if (planChartRef.value) {
    planChart ??= echarts.init(planChartRef.value)
    planChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { textStyle: { color: '#dbe7ff' } },
      grid: { top: 24, left: 48, right: 20, bottom: 32 },
      xAxis: {
        type: 'category',
        data: rows.map((item) => item.shift || ''),
        axisLabel: { color: '#9bb7d5' },
        axisLine: { lineStyle: { color: '#203447' } },
      },
      yAxis: {
        type: 'value',
        axisLabel: { color: '#9bb7d5' },
        splitLine: { lineStyle: { color: 'rgba(255,255,255,0.08)' } },
      },
      series: [
        { name: '入洗原煤', type: 'bar', data: rows.map((item) => item.rawCoalTarget), itemStyle: { color: '#57d8ff' } },
        { name: '精煤', type: 'bar', data: rows.map((item) => item.cleanCoalTarget), itemStyle: { color: '#25d39c' } },
        { name: '矸石', type: 'line', smooth: true, data: rows.map((item) => item.gangueTarget), itemStyle: { color: '#ffc857' } },
      ],
    })
  }
}

const loadPlans = async () => {
  try {
    plans.value = await listProductionPlans({ year: query.year, month: query.month })
  } catch {
    plans.value = fallbackPlans
  }
  await nextTick()
  renderChart()
}

onMounted(() => {
  loadPlans()
  window.addEventListener('resize', renderChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', renderChart)
  planChart?.dispose()
})
</script>

<style scoped>
.planning-page {
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
  grid-template-columns: 1.35fr 0.65fr;
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
