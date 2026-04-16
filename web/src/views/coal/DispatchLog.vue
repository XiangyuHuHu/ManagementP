<template>
  <div class="coal-page section-page">
    <CoalQuickBar
      title="调度日志细分"
      subtitle="对应最新需求中的启停车记录、停送电记录、当班记录和调度日志，保留原调度页，细分台账单独落页。"
    />

    <section class="page-shell">
      <section class="section-hero">
        <div>
          <p class="section-eyebrow">调度管理</p>
          <h1>调度日志细分台账</h1>
          <p class="section-text">以分类方式管理启停车、停送电、当班记录和调度日志，便于检索、录入和交接班追踪。</p>
        </div>
        <div class="hero-actions">
          <el-button @click="loadRows">刷新</el-button>
          <el-button type="primary" @click="openCreateDialog">新增日志</el-button>
        </div>
      </section>

      <section class="stats-grid">
        <article class="stat-card" v-for="item in stats" :key="item.label">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <small>{{ item.note }}</small>
        </article>
      </section>

      <section class="section-panel filters">
        <el-select v-model="filterCategory" clearable placeholder="日志分类" style="width: 180px">
          <el-option label="启停车记录" value="启停车记录" />
          <el-option label="停送电记录" value="停送电记录" />
          <el-option label="当班记录" value="当班记录" />
          <el-option label="调度日志" value="调度日志" />
        </el-select>
        <el-select v-model="filterShift" clearable placeholder="班次" style="width: 140px">
          <el-option label="白班" value="白班" />
          <el-option label="夜班" value="夜班" />
        </el-select>
        <el-button type="primary" @click="loadRows">查询</el-button>
      </section>

      <section class="section-panel">
        <div class="panel-head">
          <div>
            <h2>日志分类分布</h2>
            <p>用于调度复盘和交接班统计。</p>
          </div>
        </div>
        <div ref="chartEl" class="chart-box"></div>
      </section>

      <section class="section-panel">
        <div class="panel-head">
          <div>
            <h2>日志明细列表</h2>
            <p>统一覆盖启停车、停送电、当班记录和调度日志。</p>
          </div>
        </div>
        <el-table :data="rows" class="records-table">
          <el-table-column prop="category" label="分类" min-width="130" />
          <el-table-column prop="logTime" label="记录时间" min-width="160" />
          <el-table-column prop="shift" label="班次" width="100" />
          <el-table-column prop="content" label="日志内容" min-width="320" show-overflow-tooltip />
          <el-table-column prop="operator" label="记录人" min-width="100" />
          <el-table-column prop="status" label="状态" min-width="100" />
          <el-table-column label="操作" width="170" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
              <el-button type="danger" link @click="removeRow(row.id!)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>
    </section>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑调度日志' : '新增调度日志'" width="720px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="日志分类">
          <el-select v-model="form.category" style="width:100%">
            <el-option label="启停车记录" value="启停车记录" />
            <el-option label="停送电记录" value="停送电记录" />
            <el-option label="当班记录" value="当班记录" />
            <el-option label="调度日志" value="调度日志" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录时间"><el-input v-model="form.logTime" /></el-form-item>
        <el-form-item label="班次">
          <el-select v-model="form.shift" style="width:100%">
            <el-option label="白班" value="白班" />
            <el-option label="夜班" value="夜班" />
          </el-select>
        </el-form-item>
        <el-form-item label="日志内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="记录人"><el-input v-model="form.operator" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="已完成" value="已完成" />
            <el-option label="处理中" value="处理中" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRow">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import CoalQuickBar from '../../components/coal/CoalQuickBar.vue'
import { echarts } from '../../utils/echarts'
import {
  createDispatchLog,
  deleteDispatchLog,
  listDispatchLogs,
  updateDispatchLog,
  type DispatchLogDto,
} from '../../api/coal-business'

const chartEl = ref<HTMLElement | null>(null)
let chart: any = null
const filterCategory = ref('')
const filterShift = ref('')
const rows = ref<DispatchLogDto[]>([])
const showDialog = ref(false)
const editingId = ref<number | null>(null)

const createDefaultForm = (): DispatchLogDto => ({
  category: '启停车记录',
  logTime: '',
  shift: '白班',
  content: '',
  operator: '',
  status: '处理中',
})

const form = ref<DispatchLogDto>(createDefaultForm())

const fallbackRows: DispatchLogDto[] = [
  { id: 1, category: '启停车记录', logTime: '2026-04-14 07:55:00', shift: '白班', content: '主洗系统开车', operator: '张伟', status: '已完成' },
  { id: 2, category: '停送电记录', logTime: '2026-04-14 09:40:00', shift: '白班', content: '浮选机检修停电后恢复送电', operator: '李超', status: '已完成' },
  { id: 3, category: '当班记录', logTime: '2026-04-14 10:25:00', shift: '白班', content: '精煤灰分波动，已通知化验复核', operator: '王敏', status: '处理中' },
  { id: 4, category: '调度日志', logTime: '2026-04-13 21:10:00', shift: '夜班', content: '原煤仓上给料量调整至 430 吨/小时', operator: '赵凯', status: '已完成' },
]

const stats = computed(() => [
  { label: '日志总数', value: `${rows.value.length} 条`, note: '当前筛选结果' },
  { label: '启停车记录', value: `${rows.value.filter(item => item.category === '启停车记录').length} 条`, note: '用于设备启停追踪' },
  { label: '停送电记录', value: `${rows.value.filter(item => item.category === '停送电记录').length} 条`, note: '用于电气闭环管理' },
  { label: '处理中', value: `${rows.value.filter(item => item.status === '处理中').length} 条`, note: '待继续跟踪事项' },
])

async function loadRows() {
  try {
    const data = await listDispatchLogs({ category: filterCategory.value, shift: filterShift.value })
    rows.value = data.length ? data : fallbackRows
  } catch {
    rows.value = fallbackRows
  }
  await nextTick()
  renderChart()
}

function renderChart() {
  if (!chartEl.value) return
  chart?.dispose()
  chart = echarts.init(chartEl.value)
  const categories = ['启停车记录', '停送电记录', '当班记录', '调度日志']
  const seriesData = categories.map(name => rows.value.filter(item => item.category === name).length)
  chart.setOption({
    tooltip: { trigger: 'item' },
    xAxis: { type: 'category', data: categories, axisLabel: { color: '#9fb4c9' } },
    yAxis: { type: 'value', axisLabel: { color: '#9fb4c9' }, splitLine: { lineStyle: { color: 'rgba(120,160,200,0.12)' } } },
    series: [{ type: 'bar', data: seriesData, itemStyle: { color: '#4ec9ff', borderRadius: [6, 6, 0, 0] } }],
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  })
}

function openCreateDialog() {
  editingId.value = null
  form.value = createDefaultForm()
  showDialog.value = true
}

function openEditDialog(row: DispatchLogDto) {
  editingId.value = row.id || null
  form.value = { ...row }
  showDialog.value = true
}

async function saveRow() {
  try {
    if (editingId.value) {
      await updateDispatchLog({ ...form.value, id: editingId.value })
      ElMessage.success('调度日志已更新')
    } else {
      await createDispatchLog(form.value)
      ElMessage.success('调度日志已新增')
    }
  } catch {
    ElMessage.warning('接口未完全接入，已保留前端演示流程')
  }
  showDialog.value = false
  await loadRows()
}

async function removeRow(id: number) {
  try {
    await deleteDispatchLog(id)
    ElMessage.success('调度日志已删除')
  } catch {
    rows.value = rows.value.filter(item => item.id !== id)
    ElMessage.warning('接口未完全接入，已从当前列表移除')
  }
  await loadRows()
}

const handleResize = () => chart?.resize()

onMounted(async () => {
  await loadRows()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  chart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.section-page{min-height:100vh;padding:0 20px 24px;background:#091019;color:#eef6ff}
.page-shell{width:min(100%,1680px);margin:0 auto}
.section-hero{display:flex;justify-content:space-between;gap:24px;align-items:flex-start;margin-bottom:20px}
.section-eyebrow{margin:0 0 10px;color:#72d8ff;font-size:12px;letter-spacing:.2em;text-transform:uppercase}
.section-hero h1{margin:0;font-size:38px}
.section-text{max-width:820px;margin:12px 0 0;color:#96aabc;line-height:1.7}
.hero-actions{display:flex;gap:12px}
.section-panel{padding:22px;border-radius:20px;border:1px solid rgba(122,190,255,.12);background:rgba(12,20,31,.92);box-shadow:0 18px 40px rgba(0,0,0,.16);margin-bottom:20px}
.filters{display:flex;gap:12px;align-items:center;flex-wrap:wrap}
.stats-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:16px;margin-bottom:20px}
.stat-card{padding:18px;border-radius:18px;border:1px solid rgba(122,190,255,.12);background:rgba(12,20,31,.92)}
.stat-card span{display:block;color:#97aabc}
.stat-card strong{display:block;margin-top:14px;font-size:30px}
.stat-card small{display:block;margin-top:10px;color:#6ec8ff}
.panel-head{display:flex;justify-content:space-between;align-items:flex-start;gap:20px;margin-bottom:16px}
.panel-head h2{margin:0;font-size:24px}
.panel-head p{margin:8px 0 0;color:#8fa8bc}
.chart-box{height:320px}
@media (max-width: 1200px){.stats-grid{grid-template-columns:repeat(2,1fr)}}
@media (max-width: 768px){.stats-grid{grid-template-columns:1fr}}
</style>
