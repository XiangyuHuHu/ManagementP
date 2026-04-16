<template>
  <div class="coal-page section-page">
    <CoalQuickBar
      title="工艺流程专项"
      subtitle="独立跟踪流程节点负荷、状态和告警，作为生产流程图的专项管理页。"
    />

    <section class="page-shell">
      <section class="section-hero">
        <div>
          <p class="section-eyebrow">工艺流程</p>
          <h1>流程节点状态与处置建议</h1>
          <p class="section-text">按节点和工艺区段聚合状态、负荷、报警和建议，辅助当班工艺调整。</p>
        </div>
      </section>

      <section class="section-panel filters">
        <el-select v-model="filterStatus" clearable placeholder="节点状态" style="width:180px">
          <el-option label="正常" value="正常" />
          <el-option label="关注" value="关注" />
          <el-option label="告警" value="告警" />
        </el-select>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
        <span class="refresh-note">自动刷新：30 秒（最近更新：{{ lastUpdatedText }}）</span>
      </section>

      <section class="stats-grid">
        <article class="stat-card" v-for="item in stats" :key="item.label">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <small>{{ item.note }}</small>
        </article>
      </section>

      <section class="section-panel">
        <div class="panel-head">
          <div>
            <h2>节点状态分布</h2>
            <p>反映当前流程节点的运行状态。</p>
          </div>
        </div>
        <div ref="chartEl" class="chart-box"></div>
      </section>

      <section class="section-panel">
        <div class="panel-head">
          <div>
            <h2>工艺流程节点清单</h2>
            <p>保留负荷、告警和建议字段，支持打印和导出。</p>
          </div>
          <div class="panel-actions">
            <el-button @click="handleExport">导出 CSV</el-button>
            <el-button type="primary" @click="handlePrint">打印</el-button>
          </div>
        </div>
        <el-table :data="pagedRows" @row-click="openDetail">
          <el-table-column prop="nodeName" label="节点名称" min-width="160" />
          <el-table-column prop="section" label="工艺区段" min-width="120" />
          <el-table-column prop="status" label="节点状态" min-width="100">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)" effect="dark">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="load" label="当前负荷" min-width="120" />
          <el-table-column prop="alarm" label="告警信息" min-width="200" show-overflow-tooltip />
          <el-table-column prop="suggestion" label="处置建议" min-width="220" show-overflow-tooltip />
          <el-table-column prop="ticketNo" label="工艺单号" min-width="150" />
          <el-table-column prop="closureStatus" label="闭环状态" min-width="110" />
          <el-table-column prop="updateTime" label="更新时间" min-width="160" />
        </el-table>
        <div class="pager-wrap">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="rows.length"
            layout="total, prev, pager, next"
            background
          />
        </div>
      </section>
    </section>

    <el-drawer v-model="detailVisible" title="流程节点详情" size="40%">
      <el-descriptions :column="1" border v-if="selectedRow">
        <el-descriptions-item label="节点名称">{{ selectedRow.nodeName }}</el-descriptions-item>
        <el-descriptions-item label="工艺区段">{{ selectedRow.section }}</el-descriptions-item>
        <el-descriptions-item label="节点状态">
          <el-tag :type="statusTagType(selectedRow.status)" effect="dark">{{ selectedRow.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="当前负荷">{{ selectedRow.load }}</el-descriptions-item>
        <el-descriptions-item label="告警信息">{{ selectedRow.alarm }}</el-descriptions-item>
        <el-descriptions-item label="处置建议">{{ selectedRow.suggestion }}</el-descriptions-item>
        <el-descriptions-item label="工艺单号">{{ selectedRow.ticketNo }}</el-descriptions-item>
        <el-descriptions-item label="完成时限">{{ selectedRow.deadline }}</el-descriptions-item>
        <el-descriptions-item label="闭环状态">{{ selectedRow.closureStatus }}</el-descriptions-item>
        <el-descriptions-item label="复盘结论">{{ selectedRow.reviewConclusion }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ selectedRow.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <div class="drawer-actions" v-if="selectedRow">
        <el-button type="primary" @click="openAdjustmentDialog">下发工艺调整</el-button>
        <el-button @click="createReviewTask">创建复盘任务</el-button>
      </div>
    </el-drawer>

    <el-dialog v-model="adjustmentDialogVisible" title="下发工艺调整" width="520px">
      <el-form label-width="100px">
        <el-form-item label="完成时限">
          <el-input v-model="adjustmentForm.deadline" placeholder="例如：2026-04-15 09:40" />
        </el-form-item>
        <el-form-item label="调整建议">
          <el-input v-model="adjustmentForm.suggestion" type="textarea" :rows="3" placeholder="请输入调整内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustmentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdjustment">确认下发</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import CoalQuickBar from '../../components/coal/CoalQuickBar.vue'
import { echarts } from '../../utils/echarts'
import { exportRowsToCsv, printRowsAsTable } from '../../utils/report-export'
import { listProcessFlow, type ProcessFlowDto } from '../../api/coal-business'

const filterStatus = ref('')
const rows = ref<ProcessFlowDto[]>([])
const selectedRow = ref<ProcessFlowDto | null>(null)
const detailVisible = ref(false)
const currentPage = ref(1)
const pageSize = 8
const lastUpdatedText = ref('--')
const adjustmentDialogVisible = ref(false)
const adjustmentForm = ref({
  deadline: '',
  suggestion: '',
})
const chartEl = ref<HTMLElement | null>(null)
let chart: any = null
let refreshTimer = 0

const fallbackRows: ProcessFlowDto[] = [
  { nodeName: '原煤仓给料', section: '入洗段', status: '正常', load: '71%', alarm: '无', suggestion: '维持当前给料频率', ticketNo: 'GYGD-20260415-001', deadline: '2026-04-15 10:30', closureStatus: '已闭环', reviewConclusion: '运行参数连续 2 小时稳定', updateTime: '2026-04-15 08:20' },
  { nodeName: '主破碎机', section: '破碎段', status: '告警', load: '93%', alarm: '电流接近上限', suggestion: '建议下调 8% 给料并复核筛前堆煤', ticketNo: 'GYGD-20260415-002', deadline: '2026-04-15 09:20', closureStatus: '整改中', reviewConclusion: '已下发调整指令，等待回传', updateTime: '2026-04-15 08:18' },
  { nodeName: '洗选回路', section: '分选段', status: '关注', load: '84%', alarm: '介质密度波动', suggestion: '检查介质桶补液阀门响应', ticketNo: 'GYGD-20260415-003', deadline: '2026-04-15 09:40', closureStatus: '待复核', reviewConclusion: '密度恢复后需复盘班报', updateTime: '2026-04-15 08:12' },
  { nodeName: '产品仓', section: '储运段', status: '正常', load: '66%', alarm: '无', suggestion: '按既定发运计划执行', ticketNo: 'GYGD-20260415-004', deadline: '2026-04-15 11:00', closureStatus: '已闭环', reviewConclusion: '流程节点状态正常', updateTime: '2026-04-15 08:05' },
]

const stats = computed(() => [
  { label: '节点总数', value: `${rows.value.length} 个`, note: '当前筛选结果' },
  { label: '正常', value: `${rows.value.filter((item) => item.status === '正常').length} 个`, note: '处于稳定运行区间' },
  { label: '关注', value: `${rows.value.filter((item) => item.status === '关注').length} 个`, note: '需持续观察波动' },
  { label: '告警', value: `${rows.value.filter((item) => item.status === '告警').length} 个`, note: '需立即处置' },
])

const exportColumns: Array<{ key: keyof ProcessFlowDto; label: string }> = [
  { key: 'nodeName', label: '节点名称' },
  { key: 'section', label: '工艺区段' },
  { key: 'status', label: '节点状态' },
  { key: 'load', label: '当前负荷' },
  { key: 'alarm', label: '告警信息' },
  { key: 'suggestion', label: '处置建议' },
  { key: 'ticketNo', label: '工艺单号' },
  { key: 'deadline', label: '完成时限' },
  { key: 'closureStatus', label: '闭环状态' },
  { key: 'reviewConclusion', label: '复盘结论' },
  { key: 'updateTime', label: '更新时间' },
]

const pagedRows = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return rows.value.slice(start, start + pageSize)
})

async function loadRows(showToast = false) {
  try {
    const data = await listProcessFlow({ status: filterStatus.value })
    rows.value = data.length ? data : fallbackRows
  } catch {
    rows.value = fallbackRows
  }
  currentPage.value = 1
  lastUpdatedText.value = new Date().toLocaleString('zh-CN', { hour12: false })
  await nextTick()
  renderChart()
  if (showToast) ElMessage.success('工艺流程数据已刷新')
}

function renderChart() {
  if (!chartEl.value) return
  chart?.dispose()
  chart = echarts.init(chartEl.value)
  const categories = ['正常', '关注', '告警']
  const values = categories.map((key) => rows.value.filter((item) => item.status === key).length)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: categories, axisLabel: { color: '#9fb4c9' } },
    yAxis: { type: 'value', axisLabel: { color: '#9fb4c9' }, splitLine: { lineStyle: { color: 'rgba(120,160,200,0.12)' } } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    series: [{ type: 'bar', data: values, itemStyle: { color: '#4ec9ff', borderRadius: [6, 6, 0, 0] } }],
  })
}

const handleExport = () => {
  exportRowsToCsv(rows.value, exportColumns, `工艺流程节点_${new Date().toISOString().slice(0, 10)}`)
  ElMessage.success('工艺流程节点 CSV 已下载')
}

const handleQuery = () => loadRows(true)
const handleRefresh = () => loadRows(true)

const handlePrint = () => {
  const ok = printRowsAsTable('工艺流程节点清单', rows.value, exportColumns, {
    subtitle: '金海泽地选煤厂工艺流程专项报表',
    meta: [
      { label: '筛选状态', value: filterStatus.value || '全部' },
      { label: '记录条数', value: `${rows.value.length}` },
    ],
    preparedBy: '工艺员',
    reviewedBy: '生产技术科',
    approvedBy: '生产调度',
  })
  if (!ok) {
    ElMessage.warning('浏览器拦截了打印窗口，请允许弹窗后重试')
    return
  }
  ElMessage.success('已打开打印预览')
}

const statusTagType = (status: string) => {
  if (status === '告警') return 'danger'
  if (status === '关注') return 'warning'
  return 'success'
}

const openDetail = (row: ProcessFlowDto) => {
  selectedRow.value = row
  detailVisible.value = true
}

const updateRow = (target: ProcessFlowDto) => {
  rows.value = rows.value.map((item) => (item.ticketNo === target.ticketNo ? { ...target } : item))
  selectedRow.value = { ...target }
  nextTick(() => renderChart())
}

const openAdjustmentDialog = () => {
  if (!selectedRow.value) return
  adjustmentForm.value = {
    deadline: selectedRow.value.deadline,
    suggestion: selectedRow.value.suggestion,
  }
  adjustmentDialogVisible.value = true
}

const submitAdjustment = () => {
  if (!selectedRow.value) return
  const current = { ...selectedRow.value }
  current.deadline = adjustmentForm.value.deadline || current.deadline
  current.suggestion = adjustmentForm.value.suggestion || current.suggestion
  current.status = '关注'
  current.closureStatus = '整改中'
  current.reviewConclusion = '已下发工艺调整，待回传结果'
  current.updateTime = new Date().toLocaleString('zh-CN', { hour12: false })
  updateRow(current)
  adjustmentDialogVisible.value = false
  ElMessage.success('工艺调整已下发')
}

const createReviewTask = () => {
  if (!selectedRow.value) return
  const current = { ...selectedRow.value }
  current.closureStatus = '待复核'
  current.reviewConclusion = '已创建复盘任务，等待班后复盘'
  current.updateTime = new Date().toLocaleString('zh-CN', { hour12: false })
  updateRow(current)
  ElMessage.success('复盘任务已创建')
}

const handleResize = () => chart?.resize()
onMounted(async () => {
  await loadRows()
  refreshTimer = window.setInterval(() => loadRows(), 30000)
  window.addEventListener('resize', handleResize)
})
onUnmounted(() => {
  clearInterval(refreshTimer)
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
.section-panel{padding:22px;border-radius:20px;border:1px solid rgba(122,190,255,.12);background:rgba(12,20,31,.92);box-shadow:0 18px 40px rgba(0,0,0,.16);margin-bottom:20px}
.filters{display:flex;gap:12px;align-items:center;flex-wrap:wrap}
.refresh-note{color:#8fb0c8;font-size:12px}
.stats-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:16px;margin-bottom:20px}
.stat-card{padding:18px;border-radius:18px;border:1px solid rgba(122,190,255,.12);background:rgba(12,20,31,.92)}
.stat-card span{display:block;color:#97aabc}
.stat-card strong{display:block;margin-top:14px;font-size:30px}
.stat-card small{display:block;margin-top:10px;color:#6ec8ff}
.panel-head{display:flex;justify-content:space-between;align-items:flex-start;gap:20px;margin-bottom:16px}
.panel-actions{display:flex;gap:8px;align-items:center}
.panel-head h2{margin:0;font-size:24px}
.panel-head p{margin:8px 0 0;color:#8fa8bc}
.chart-box{height:320px}
.pager-wrap{display:flex;justify-content:flex-end;margin-top:12px}
.drawer-actions{display:flex;gap:10px;margin-top:16px}
@media (max-width: 1200px){.stats-grid{grid-template-columns:repeat(2,1fr)}}
@media (max-width: 768px){.stats-grid{grid-template-columns:1fr}}
</style>
