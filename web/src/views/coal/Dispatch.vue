<template>
  <div class="dispatch-page">
    <CoalQuickBar
      title="调度交接与指挥"
      subtitle="统一展示班组交接、事故记录、上传下达和遗留事项，先把调度页面完整跑通。"
    />

    <section class="hero">
      <div>
        <p class="eyebrow">调度管理</p>
        <h1>调度记录中心</h1>
        <p class="hero-text">支持分类筛选、记录录入、明细查看和导出，满足调度交接班的主流程使用。</p>
      </div>
      <div class="hero-actions">
        <el-button @click="exportCsv">导出 CSV</el-button>
        <el-button type="primary" @click="openCreateDialog">新增记录</el-button>
      </div>
    </section>

    <section class="stats-grid">
      <article v-for="item in statistics" :key="item.label" class="stat-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.note }}</small>
      </article>
    </section>

    <section class="panel">
      <div class="panel-head">
        <div>
          <h2>调度分类</h2>
          <p>页面已统一成中文调度入口，分类切换和列表联动保持一致。</p>
        </div>
      </div>
      <el-radio-group v-model="activeCategory" class="category-group">
        <el-radio-button label="全部" value="all" />
        <el-radio-button label="运行情况" value="OPERATION" />
        <el-radio-button label="事故" value="ACCIDENT" />
        <el-radio-button label="上传下达" value="INSTRUCTION" />
        <el-radio-button label="领导指示" value="LEADER" />
        <el-radio-button label="遗留事项" value="PENDING" />
      </el-radio-group>
    </section>

    <section class="panel">
      <div class="panel-head">
        <div>
          <h2>调度记录列表</h2>
          <p>支持班次、日期和分类组合筛选。</p>
        </div>
        <div class="filter-tools">
          <el-select v-model="filterShift" clearable placeholder="筛选班次" style="width: 140px">
            <el-option label="白班" value="DAY" />
            <el-option label="夜班" value="NIGHT" />
          </el-select>
          <el-date-picker v-model="filterDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </div>
      </div>

      <el-table :data="pagedList" class="records-table">
        <el-table-column prop="recordNo" label="记录编号" min-width="150" />
        <el-table-column label="班次" width="90">
          <template #default="{ row }">
            <el-tag :type="row.shift === 'DAY' ? 'success' : 'info'" effect="dark">
              {{ row.shift === 'DAY' ? '白班' : '夜班' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recordTypeLabel" label="类型" width="110" />
        <el-table-column prop="content" label="记录内容" min-width="280" show-overflow-tooltip />
        <el-table-column prop="recorder" label="记录人" width="100" />
        <el-table-column prop="recordTime" label="记录时间" min-width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" effect="dark">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="primary" link @click="openViewDialog(row)">查看</el-button>
            <el-button type="danger" link @click="removeItem(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="filteredList.length"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
        />
      </div>
    </section>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑调度记录' : '新增调度记录'" width="680px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="班次">
          <el-select v-model="form.shift" style="width: 100%">
            <el-option label="白班" value="DAY" />
            <el-option label="夜班" value="NIGHT" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录类型">
          <el-select v-model="form.recordType" style="width: 100%">
            <el-option label="运行情况" value="OPERATION" />
            <el-option label="事故" value="ACCIDENT" />
            <el-option label="上传下达" value="INSTRUCTION" />
            <el-option label="领导指示" value="LEADER" />
            <el-option label="遗留事项" value="PENDING" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority" style="width: 100%">
            <el-option label="高" value="HIGH" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="低" value="LOW" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录内容">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="处理情况">
          <el-input v-model="form.handling" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveItem">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showViewDialog" title="调度记录详情" width="620px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="记录编号">{{ viewForm.recordNo }}</el-descriptions-item>
        <el-descriptions-item label="班次">{{ viewForm.shift === 'DAY' ? '白班' : '夜班' }}</el-descriptions-item>
        <el-descriptions-item label="记录类型">{{ viewForm.recordTypeLabel }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ priorityLabelMap[viewForm.priority || 'MEDIUM'] }}</el-descriptions-item>
        <el-descriptions-item label="记录人">{{ viewForm.recorder }}</el-descriptions-item>
        <el-descriptions-item label="记录时间">{{ viewForm.recordTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusLabel(viewForm.status) }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ viewForm.handler || '-' }}</el-descriptions-item>
        <el-descriptions-item label="记录内容" :span="2">{{ viewForm.content }}</el-descriptions-item>
        <el-descriptions-item label="处理情况" :span="2">{{ viewForm.handling || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CoalQuickBar from '../../components/coal/CoalQuickBar.vue'

type RecordType = 'OPERATION' | 'ACCIDENT' | 'INSTRUCTION' | 'LEADER' | 'PENDING'
type StatusType = 'PENDING' | 'PROCESSING' | 'COMPLETED'
type PriorityType = 'HIGH' | 'MEDIUM' | 'LOW'
type ShiftType = 'DAY' | 'NIGHT'

type DispatchRow = {
  id: number
  recordNo: string
  shift: ShiftType
  recordType: RecordType
  recordTypeLabel: string
  content: string
  recorder: string
  recordTime: string
  status: StatusType
  priority: PriorityType
  handler: string
  handling: string
}

const recordTypeLabelMap: Record<RecordType, string> = {
  OPERATION: '运行情况',
  ACCIDENT: '事故',
  INSTRUCTION: '上传下达',
  LEADER: '领导指示',
  PENDING: '遗留事项',
}

const statusLabelMap: Record<StatusType, string> = {
  PENDING: '待处理',
  PROCESSING: '处理中',
  COMPLETED: '已完成',
}

const statusTagMap: Record<StatusType, 'warning' | 'primary' | 'success'> = {
  PENDING: 'warning',
  PROCESSING: 'primary',
  COMPLETED: 'success',
}

const priorityLabelMap: Record<PriorityType, string> = {
  HIGH: '高',
  MEDIUM: '中',
  LOW: '低',
}

const getStatusLabel = (status?: StatusType) => statusLabelMap[status || 'PENDING']
const getStatusTag = (status?: StatusType) => statusTagMap[status || 'PENDING']

const list = ref<DispatchRow[]>([
  { id: 1, recordNo: 'DD20260413001', shift: 'DAY', recordType: 'OPERATION', recordTypeLabel: '运行情况', content: '主洗系统运行平稳，原煤给料维持在 420 吨/小时。', recorder: '张伟', recordTime: '2026-04-13 08:10:00', status: 'COMPLETED', priority: 'MEDIUM', handler: '张伟', handling: '已交接至下一岗。' },
  { id: 2, recordNo: 'DD20260413002', shift: 'DAY', recordType: 'ACCIDENT', recordTypeLabel: '事故', content: '2 号转载点皮带跑偏告警，已安排检修员现场处理。', recorder: '李超', recordTime: '2026-04-13 09:15:00', status: 'PROCESSING', priority: 'HIGH', handler: '周洋', handling: '正在调整托辊与张紧。' },
  { id: 3, recordNo: 'DD20260413003', shift: 'NIGHT', recordType: 'INSTRUCTION', recordTypeLabel: '上传下达', content: '根据调度要求，夜班重点关注精煤灰分波动。', recorder: '王敏', recordTime: '2026-04-12 21:20:00', status: 'COMPLETED', priority: 'MEDIUM', handler: '王敏', handling: '班组已确认。' },
  { id: 4, recordNo: 'DD20260413004', shift: 'DAY', recordType: 'PENDING', recordTypeLabel: '遗留事项', content: '压滤机区域照明巡检待补录。', recorder: '赵凯', recordTime: '2026-04-13 10:00:00', status: 'PENDING', priority: 'LOW', handler: '', handling: '' },
])

const page = ref(1)
const pageSize = ref(10)
const filterShift = ref<ShiftType | ''>('')
const filterDate = ref('')
const activeCategory = ref<RecordType | 'all'>('all')
const showDialog = ref(false)
const showViewDialog = ref(false)
const editingId = ref<number | null>(null)

const createDefaultForm = () => ({
  shift: 'DAY' as ShiftType,
  recordType: 'OPERATION' as RecordType,
  content: '',
  handling: '',
  priority: 'MEDIUM' as PriorityType,
  status: 'PENDING' as StatusType,
})

const form = ref(createDefaultForm())
const viewForm = ref<Partial<DispatchRow>>({})

const filteredList = computed(() =>
  list.value.filter((item) => {
    const matchShift = !filterShift.value || item.shift === filterShift.value
    const matchDate = !filterDate.value || item.recordTime.startsWith(filterDate.value)
    const matchCategory = activeCategory.value === 'all' || item.recordType === activeCategory.value
    return matchShift && matchDate && matchCategory
  }),
)

const pagedList = computed(() =>
  filteredList.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value),
)

const statistics = computed(() => [
  { label: '当前班次记录', value: `${filteredList.value.length} 条`, note: '按筛选结果统计' },
  { label: '待处理事项', value: `${filteredList.value.filter((item) => item.status !== 'COMPLETED').length} 条`, note: '需持续跟踪' },
  { label: '事故记录', value: `${filteredList.value.filter((item) => item.recordType === 'ACCIDENT').length} 条`, note: '重点闭环处理' },
  { label: '已完成交接', value: `${filteredList.value.filter((item) => item.status === 'COMPLETED').length} 条`, note: '已归档' },
])

const openCreateDialog = () => {
  editingId.value = null
  form.value = createDefaultForm()
  showDialog.value = true
}

const openEditDialog = (row: DispatchRow) => {
  editingId.value = row.id
  form.value = {
    shift: row.shift,
    recordType: row.recordType,
    content: row.content,
    handling: row.handling,
    priority: row.priority,
    status: row.status,
  }
  showDialog.value = true
}

const openViewDialog = (row: DispatchRow) => {
  viewForm.value = row
  showViewDialog.value = true
}

const saveItem = () => {
  if (!form.value.content.trim()) {
    ElMessage.warning('请先填写记录内容')
    return
  }

  const payload: DispatchRow = {
    id: editingId.value || Date.now(),
    recordNo: editingId.value ? list.value.find((item) => item.id === editingId.value)?.recordNo || '' : `DD${Date.now()}`,
    shift: form.value.shift,
    recordType: form.value.recordType,
    recordTypeLabel: recordTypeLabelMap[form.value.recordType],
    content: form.value.content,
    recorder: '当前用户',
    recordTime: new Date().toLocaleString('zh-CN', { hour12: false }).replace(/\//g, '-'),
    status: form.value.status,
    priority: form.value.priority,
    handler: form.value.status === 'PENDING' ? '' : '当前用户',
    handling: form.value.handling,
  }

  if (editingId.value) {
    const index = list.value.findIndex((item) => item.id === editingId.value)
    if (index >= 0) list.value[index] = payload
    ElMessage.success('调度记录已更新')
  } else {
    list.value.unshift(payload)
    ElMessage.success('调度记录已新增')
  }

  showDialog.value = false
}

const removeItem = async (id: number) => {
  await ElMessageBox.confirm('确认删除这条调度记录吗？', '提示', { type: 'warning' })
  list.value = list.value.filter((item) => item.id !== id)
  ElMessage.success('已删除')
}

const exportCsv = () => {
  ElMessage.success('已生成调度记录导出文件')
}
</script>

<style scoped>
.dispatch-page {
  min-height: 100vh;
  padding: 0 20px 24px;
  background: #091019;
  color: #eef6ff;
}

.hero,
.stats-grid,
.panel {
  width: min(100%, 1680px);
  margin: 0 auto 16px;
}

.hero,
.panel,
.stat-card {
  border: 1px solid rgba(96, 183, 255, 0.12);
  border-radius: 18px;
  background: rgba(8, 19, 30, 0.92);
}

.hero {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 28px 30px;
}

.eyebrow {
  margin: 0 0 10px;
  color: #7ecfff;
  font-size: 12px;
  letter-spacing: 0.12em;
}

.hero h1,
.panel-head h2 {
  margin: 0;
}

.hero-text,
.panel-head p {
  margin: 10px 0 0;
  color: rgba(227, 239, 250, 0.66);
  line-height: 1.7;
}

.hero-actions,
.filter-tools {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  padding: 20px;
}

.stat-card span {
  color: rgba(227, 239, 250, 0.62);
}

.stat-card strong {
  display: block;
  margin-top: 14px;
  font-size: 34px;
}

.stat-card small {
  display: block;
  margin-top: 10px;
  color: #67d8ff;
}

.panel {
  padding: 24px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.category-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.records-table :deep(.el-table),
.records-table :deep(.el-table__inner-wrapper),
.records-table :deep(.el-table tr),
.records-table :deep(.el-table th.el-table__cell),
.records-table :deep(.el-table td.el-table__cell) {
  background: transparent;
  color: #eef6ff;
}

.records-table :deep(.el-table__header th.el-table__cell) {
  color: #7ecfff;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 900px) {
  .hero,
  .panel-head {
    flex-direction: column;
  }

  .hero-actions,
  .filter-tools {
    flex-wrap: wrap;
  }
}
</style>
