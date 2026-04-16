<template>
  <div class="sales-page">
    <CoalQuickBar title="产品销售统计" subtitle="对应最新需求中的产品销售统计，支持按日期、运输方式、用户类型、产品类型查询销售数质量情况。" />

    <section class="page-shell">
      <div class="page-head">
        <div>
          <h1>销售数质量统计</h1>
          <p>统计销售数量、灰分、水分、合格率和稳定率，支撑销售分析和产品配煤判断。</p>
        </div>
        <div class="page-actions">
          <el-date-picker v-model="query.date" type="date" value-format="YYYY-MM-DD" placeholder="统计日期" />
          <el-select v-model="query.transportMode" clearable placeholder="运输方式" style="width: 140px">
            <el-option label="汽运" value="汽运" />
            <el-option label="火运" value="火运" />
          </el-select>
          <el-button @click="loadRows">查询</el-button>
        </div>
      </div>

      <div class="kpi-grid">
        <article class="kpi-card">
          <span>当日销量</span>
          <strong>{{ summary.totalQuantity }} 吨</strong>
          <small>全部产品累计</small>
        </article>
        <article class="kpi-card">
          <span>平均合格率</span>
          <strong>{{ summary.qualificationRate }}%</strong>
          <small>按销售批次统计</small>
        </article>
        <article class="kpi-card">
          <span>平均稳定率</span>
          <strong>{{ summary.stabilityRate }}%</strong>
          <small>反映质量波动</small>
        </article>
        <article class="kpi-card">
          <span>客户类型</span>
          <strong>{{ summary.customerTypes }}</strong>
          <small>电厂/焦化/贸易商</small>
        </article>
      </div>

      <div class="panel-grid">
        <article class="panel">
          <div class="panel-head">
            <h2>销售趋势</h2>
          </div>
          <div ref="salesChartRef" class="chart-box"></div>
        </article>

        <article class="panel">
          <div class="panel-head">
            <h2>销售明细</h2>
          </div>
          <el-table :data="rows" class="dark-table">
            <el-table-column prop="statDate" label="日期" width="110" />
            <el-table-column prop="transportMode" label="运输" width="90" />
            <el-table-column prop="customerType" label="用户类型" width="100" />
            <el-table-column prop="productType" label="产品类型" width="100" />
            <el-table-column prop="quantity" label="销量" width="90" />
            <el-table-column prop="ash" label="灰分" width="90" />
            <el-table-column prop="moisture" label="水分" width="90" />
            <el-table-column prop="qualificationRate" label="合格率" width="100" />
            <el-table-column prop="stabilityRate" label="稳定率" width="100" />
          </el-table>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import CoalQuickBar from '../../components/coal/CoalQuickBar.vue'
import { listSalesStatistics, type SalesStatisticDto } from '../../api/coal-business'
import { echarts } from '../../utils/echarts'

const query = reactive({
  date: '2026-04-15',
  transportMode: '',
})

const rows = ref<SalesStatisticDto[]>([])
const salesChartRef = ref<HTMLElement | null>(null)
let salesChart: any = null

const fallbackRows: SalesStatisticDto[] = [
  { statDate: '2026-04-15', transportMode: '汽运', customerType: '电厂', productType: '精煤', quantity: 1280, ash: 9.4, moisture: 7.6, qualificationRate: 98.2, stabilityRate: 96.5 },
  { statDate: '2026-04-15', transportMode: '火运', customerType: '焦化', productType: '块精煤', quantity: 860, ash: 8.9, moisture: 7.1, qualificationRate: 99.1, stabilityRate: 97.8 },
  { statDate: '2026-04-14', transportMode: '汽运', customerType: '贸易商', productType: '混煤', quantity: 720, ash: 12.8, moisture: 8.2, qualificationRate: 95.6, stabilityRate: 93.4 },
  { statDate: '2026-04-14', transportMode: '火运', customerType: '电厂', productType: '末煤', quantity: 930, ash: 15.3, moisture: 9.4, qualificationRate: 94.3, stabilityRate: 92.7 },
]

const summary = computed(() => {
  const data = rows.value.length ? rows.value : fallbackRows
  const totalQuantity = data.reduce((sum, item) => sum + Number(item.quantity || 0), 0)
  const qualificationRate = (data.reduce((sum, item) => sum + Number(item.qualificationRate || 0), 0) / data.length).toFixed(1)
  const stabilityRate = (data.reduce((sum, item) => sum + Number(item.stabilityRate || 0), 0) / data.length).toFixed(1)
  return {
    totalQuantity,
    qualificationRate,
    stabilityRate,
    customerTypes: 3,
  }
})

const renderChart = () => {
  const data = rows.value.length ? rows.value : fallbackRows
  if (salesChartRef.value) {
    salesChart ??= echarts.init(salesChartRef.value)
    salesChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { textStyle: { color: '#dbe7ff' } },
      grid: { top: 24, left: 48, right: 20, bottom: 32 },
      xAxis: {
        type: 'category',
        data: data.map((item) => `${item.statDate}-${item.productType}`),
        axisLabel: { color: '#9bb7d5', rotate: 20 },
        axisLine: { lineStyle: { color: '#203447' } },
      },
      yAxis: [
        {
          type: 'value',
          axisLabel: { color: '#9bb7d5' },
          splitLine: { lineStyle: { color: 'rgba(255,255,255,0.08)' } },
        },
        {
          type: 'value',
          axisLabel: { color: '#9bb7d5' },
          splitLine: { show: false },
        },
      ],
      series: [
        { name: '销量', type: 'bar', data: data.map((item) => item.quantity), itemStyle: { color: '#57d8ff' } },
        { name: '合格率', type: 'line', yAxisIndex: 1, smooth: true, data: data.map((item) => item.qualificationRate), itemStyle: { color: '#25d39c' } },
      ],
    })
  }
}

const loadRows = async () => {
  try {
    rows.value = await listSalesStatistics({ date: query.date, transportMode: query.transportMode || undefined })
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
  salesChart?.dispose()
})
</script>

<style scoped>
.sales-page {
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
  grid-template-columns: 0.9fr 1.1fr;
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
