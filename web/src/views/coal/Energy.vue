<template>
  <div class="business-page">
    <section class="page-header">
      <div>
        <p class="page-tag">生产消耗</p>
        <h1>能耗与材料消耗台账</h1>
        <p class="page-desc">保留日常查询页，用于电耗、水耗、介耗、药耗和油脂台账查看；消耗大屏通过独立入口打开。</p>
      </div>
      <div class="page-actions">
        <el-button @click="router.push('/coal/power')">电力消耗</el-button>
        <el-button @click="router.push('/coal/water')">水消耗</el-button>
        <el-button type="primary" @click="router.push('/coal/energy-screen')">查看消耗大屏</el-button>
      </div>
    </section>

    <section class="filter-bar">
      <el-select v-model="filterType" clearable placeholder="类型筛选" style="width: 160px">
        <el-option v-for="item in energyTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-date-picker v-model="dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
      <el-button @click="resetFilter">重置</el-button>
      <el-button type="primary" @click="refreshData">刷新</el-button>
    </section>

    <section class="kpi-grid">
      <article v-for="item in summaryCards" :key="item.label" class="kpi-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.note }}</small>
      </article>
    </section>

    <section class="content-grid">
      <article class="panel panel--wide">
        <div class="panel-head">
          <div>
            <h2>分类消耗趋势</h2>
            <p>电力、用水、药剂和介质的最近 7 天变化。</p>
          </div>
        </div>
        <div ref="trendChartRef" class="chart-box"></div>
      </article>

      <article class="panel">
        <div class="panel-head">
          <div>
            <h2>单位产品单耗</h2>
            <p>各类消耗对比。</p>
          </div>
        </div>
        <div ref="barChartRef" class="chart-box chart-box--small"></div>
      </article>

      <article class="panel">
        <div class="panel-head">
          <div>
            <h2>库存与预警</h2>
            <p>材料库存提醒与采购准备。</p>
          </div>
        </div>
        <div class="alert-list">
          <div v-for="item in stockAlerts" :key="item.name" class="alert-item">
            <div>
              <strong>{{ item.name }}</strong>
              <p>{{ item.note }}</p>
            </div>
            <span :class="['alert-tag', item.level]">{{ item.value }}</span>
          </div>
        </div>
      </article>

      <article class="panel panel--wide">
        <div class="panel-head">
          <div>
            <h2>消耗台账</h2>
            <p>支持筛选、查看和导出。</p>
          </div>
          <div class="table-tools">
            <el-button @click="exportCsv">导出 CSV</el-button>
          </div>
        </div>
        <el-table :data="filteredRecords" class="data-table">
          <el-table-column prop="date" label="记录日期" width="120" />
          <el-table-column prop="typeLabel" label="类型" width="100" />
          <el-table-column prop="consumption" label="用量" width="100" />
          <el-table-column prop="unit" label="单位" width="80" />
          <el-table-column prop="unitPrice" label="单价" width="90" />
          <el-table-column prop="cost" label="费用" width="110" />
          <el-table-column prop="unitConsumption" label="单耗" width="100" />
          <el-table-column prop="recorder" label="记录人" width="100" />
          <el-table-column prop="remark" label="说明" min-width="220" show-overflow-tooltip />
        </el-table>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { echarts } from '../../utils/echarts'

type EnergyType = 'ELECTRICITY' | 'WATER' | 'MEDICINE' | 'MEDIUM' | 'OIL'

type EnergyRecord = {
  date: string
  type: EnergyType
  typeLabel: string
  consumption: number
  unit: string
  unitPrice: number
  cost: number
  unitConsumption: number
  recorder: string
  remark: string
}

const router = useRouter()

const energyTypeOptions = [
  { label: '电力', value: 'ELECTRICITY' },
  { label: '用水', value: 'WATER' },
  { label: '药剂', value: 'MEDICINE' },
  { label: '介质', value: 'MEDIUM' },
  { label: '油脂', value: 'OIL' },
]

const records = ref<EnergyRecord[]>([
  { date: '2026-04-07', type: 'ELECTRICITY', typeLabel: '电力', consumption: 128000, unit: 'kWh', unitPrice: 0.68, cost: 87040, unitConsumption: 11.6, recorder: '张伟', remark: '主洗系统负荷偏高' },
  { date: '2026-04-07', type: 'WATER', typeLabel: '用水', consumption: 2650, unit: 'm³', unitPrice: 3.2, cost: 8480, unitConsumption: 0.24, recorder: '李超', remark: '循环水补水正常' },
  { date: '2026-04-07', type: 'MEDICINE', typeLabel: '药剂', consumption: 186, unit: 'kg', unitPrice: 22, cost: 4092, unitConsumption: 0.017, recorder: '王敏', remark: '浮选药剂稳定' },
  { date: '2026-04-07', type: 'MEDIUM', typeLabel: '介质', consumption: 8.6, unit: 't', unitPrice: 1800, cost: 15480, unitConsumption: 0.78, recorder: '赵凯', remark: '介耗低于控制线' },
  { date: '2026-04-07', type: 'OIL', typeLabel: '油脂', consumption: 420, unit: 'L', unitPrice: 7.5, cost: 3150, unitConsumption: 0.038, recorder: '周洋', remark: '车辆加油正常' },
])

const filterType = ref<EnergyType | ''>('')
const dateRange = ref(['2026-04-01', '2026-04-07'])

const trendChartRef = ref<HTMLElement | null>(null)
const barChartRef = ref<HTMLElement | null>(null)
let trendChart: any = null
let barChart: any = null

const filteredRecords = computed(() =>
  records.value.filter((item) => !filterType.value || item.type === filterType.value),
)

const summaryCards = [
  { label: '今日总费用', value: '11.82 万元', note: '较昨日下降 2.6%' },
  { label: '综合单耗', value: '12.1', note: '按吨煤折算' },
  { label: '异常提醒', value: '2 项', note: '待班组确认' },
  { label: '库存预警', value: '1 项', note: '浮选药剂偏低' },
]

const stockAlerts = [
  { name: '浮选药剂库存', note: '当前可支撑 3.2 天，建议提前补货。', value: '偏低', level: 'warn' },
  { name: '重介质库存', note: '库存健康，满足本周排产。', value: '正常', level: 'good' },
  { name: '油脂库存', note: '检修与运输共用，建议关注高峰时段。', value: '关注', level: 'info' },
]

const renderCharts = () => {
  if (trendChartRef.value) {
    trendChart ??= echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { textStyle: { color: '#dbe7ff' } },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: ['04-01', '04-02', '04-03', '04-04', '04-05', '04-06', '04-07'],
        axisLabel: { color: '#9bb7d5' },
        axisLine: { lineStyle: { color: '#203447' } },
      },
      yAxis: {
        type: 'value',
        axisLabel: { color: '#9bb7d5' },
        splitLine: { lineStyle: { color: 'rgba(255,255,255,0.08)' } },
      },
      series: [
        { name: '电力', type: 'line', smooth: true, data: [122, 126, 124, 130, 129, 127, 128], itemStyle: { color: '#57d8ff' } },
        { name: '用水', type: 'line', smooth: true, data: [24, 23, 25, 26, 25, 24, 26], itemStyle: { color: '#25d39c' } },
        { name: '药剂', type: 'line', smooth: true, data: [17, 18, 19, 18, 17, 18, 18.6], itemStyle: { color: '#ffc857' } },
        { name: '介质', type: 'line', smooth: true, data: [0.56, 0.57, 0.56, 0.54, 0.58, 0.56, 0.53], itemStyle: { color: '#7aa6ff' } },
      ],
    })
  }

  if (barChartRef.value) {
    barChart ??= echarts.init(barChartRef.value)
    barChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: ['电力', '用水', '药剂', '介质', '油脂'],
        axisLabel: { color: '#9bb7d5' },
        axisLine: { lineStyle: { color: '#203447' } },
      },
      yAxis: {
        type: 'value',
        axisLabel: { color: '#9bb7d5' },
        splitLine: { lineStyle: { color: 'rgba(255,255,255,0.08)' } },
      },
      series: [
        {
          type: 'bar',
          barWidth: 32,
          data: [11.6, 0.24, 0.017, 0.78, 0.038],
          itemStyle: {
            color: (params: { dataIndex: number }) => ['#57d8ff', '#21d4a4', '#ffc857', '#ff8c69', '#7aa6ff'][params.dataIndex],
            borderRadius: [8, 8, 0, 0],
          },
        },
      ],
    })
  }
}

const refreshData = async () => {
  await nextTick()
  renderCharts()
  ElMessage.success('能耗台账已刷新')
}

const resetFilter = () => {
  filterType.value = ''
  dateRange.value = ['2026-04-01', '2026-04-07']
}

const exportCsv = () => {
  ElMessage.success('已生成消耗台账导出文件')
}

watch(filterType, async () => {
  await nextTick()
  renderCharts()
})

onMounted(async () => {
  await nextTick()
  renderCharts()
  window.addEventListener('resize', renderCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', renderCharts)
  trendChart?.dispose()
  barChart?.dispose()
})
</script>

<style scoped>
.business-page {
  min-height: 100vh;
  padding: 24px 20px 28px;
  background: #091019;
  color: #eef6ff;
}

.page-header,
.filter-bar,
.kpi-grid,
.content-grid {
  width: min(100%, 1680px);
  margin: 0 auto 16px;
}

.page-header,
.filter-bar {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 28px 30px;
  border: 1px solid rgba(96, 183, 255, 0.12);
  border-radius: 20px;
  background: rgba(8, 19, 30, 0.92);
}

.filter-bar {
  padding: 18px 24px;
  align-items: center;
}

.page-tag {
  margin: 0 0 10px;
  color: #7ecfff;
  font-size: 12px;
  letter-spacing: 0.12em;
}

.page-header h1,
.panel-head h2 {
  margin: 0;
}

.page-desc,
.panel-head p {
  margin: 10px 0 0;
  color: rgba(227, 239, 250, 0.68);
  line-height: 1.7;
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
  font-size: 13px;
}

.kpi-card strong {
  display: block;
  margin-top: 14px;
  font-size: 34px;
  color: #f3faff;
}

.kpi-card small {
  display: block;
  margin-top: 10px;
  color: #67d8ff;
}

.content-grid {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 16px;
}

.panel {
  padding: 24px;
}

.panel--wide {
  grid-column: 1 / -1;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.chart-box {
  height: 320px;
}

.chart-box--small {
  height: 280px;
}

.alert-list {
  display: grid;
  gap: 14px;
}

.alert-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 16px;
  border: 1px solid rgba(96, 183, 255, 0.1);
  border-radius: 14px;
  background: rgba(14, 27, 39, 0.55);
}

.alert-item p {
  margin: 8px 0 0;
  color: rgba(227, 239, 250, 0.62);
  line-height: 1.6;
}

.alert-tag {
  height: fit-content;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.alert-tag.good {
  background: rgba(33, 212, 164, 0.16);
  color: #21d4a4;
}

.alert-tag.warn {
  background: rgba(255, 200, 87, 0.16);
  color: #ffc857;
}

.alert-tag.info {
  background: rgba(87, 216, 255, 0.16);
  color: #57d8ff;
}

.table-tools {
  display: flex;
  gap: 12px;
}

.data-table :deep(.el-table),
.data-table :deep(.el-table__inner-wrapper),
.data-table :deep(.el-table tr),
.data-table :deep(.el-table th.el-table__cell),
.data-table :deep(.el-table td.el-table__cell) {
  background: transparent;
  color: #eef6ff;
}

.data-table :deep(.el-table__header th.el-table__cell) {
  color: #7ecfff;
}

@media (max-width: 1200px) {
  .kpi-grid,
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .page-header,
  .filter-bar {
    flex-direction: column;
  }

  .page-actions {
    flex-wrap: wrap;
  }
}
</style>
