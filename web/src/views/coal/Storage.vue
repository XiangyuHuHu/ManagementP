<template>
  <div class="storage-v2">
    <section class="hero">
      <div>
        <p class="eyebrow">coal / storage</p>
        <h1>储装运销协调面板</h1>
        <p class="hero-text">围绕原煤入库、产品外运和装车记录，统一查看储装周转节奏和出入库结构。</p>
      </div>
      <el-button type="primary" @click="openCreateDialog">
        <el-icon><Plus /></el-icon>
        新增记录
      </el-button>
    </section>

    <section class="stats-grid">
      <article class="stat-card"><span>原煤入库</span><strong>{{ statistics.rawCoalStock }}</strong><small>当前筛选累计</small></article>
      <article class="stat-card"><span>产品外运</span><strong>{{ statistics.productSales }}</strong><small>累计外运吨数</small></article>
      <article class="stat-card"><span>装车次数</span><strong>{{ statistics.loadingCount }}</strong><small>装车作业频次</small></article>
      <article class="stat-card"><span>记录总数</span><strong>{{ filteredList.length }}</strong><small>当前筛选结果</small></article>
    </section>

    <section class="panel table-panel">
      <div class="panel-head table-head">
        <div>
          <p class="eyebrow">storage ledger</p>
          <h2>储装记录</h2>
        </div>
        <div class="tools">
          <el-select v-model="filterType" placeholder="记录类型" clearable>
            <el-option label="原煤入库" value="RAW_IN" />
            <el-option label="产品外运" value="PRODUCT_OUT" />
            <el-option label="装车记录" value="LOADING" />
          </el-select>
          <el-date-picker v-model="filterDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
          <el-button @click="exportCsv">导出 CSV</el-button>
        </div>
      </div>

      <el-alert
        :title="mockMode ? '当前展示演示储装数据，后端业务接口未完全接入。' : '当前已接入储装业务数据。'"
        :type="mockMode ? 'warning' : 'success'"
        :closable="false"
        class="mode-alert"
      />

      <el-table :data="pagedList" class="records-table">
        <el-table-column prop="recordNo" label="记录编号" min-width="160" />
        <el-table-column prop="recordType" label="记录类型" width="120">
          <template #default="{ row }"><el-tag :type="getTypeTag(row.recordType)" effect="dark">{{ getTypeText(row.recordType) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="coalType" label="煤种" min-width="120" />
        <el-table-column prop="quantity" label="数量(吨)" width="110" />
        <el-table-column prop="transportMode" label="运输方式" width="110">
          <template #default="{ row }">{{ getTransportText(row.transportMode) }}</template>
        </el-table-column>
        <el-table-column prop="vehicleNo" label="车号" min-width="120" />
        <el-table-column prop="customerName" label="客户名称" min-width="150" />
        <el-table-column prop="recordTime" label="记录时间" min-width="170" />
        <el-table-column prop="remark" label="说明" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="removeItem(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="filteredList.length" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" />
      </div>
    </section>

    <el-dialog v-model="showDialog" :title="editingId ? '编辑记录' : '新增记录'" width="620px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="记录类型">
          <el-select v-model="form.recordType" style="width: 100%">
            <el-option label="原煤入库" value="RAW_IN" />
            <el-option label="产品外运" value="PRODUCT_OUT" />
            <el-option label="装车记录" value="LOADING" />
          </el-select>
        </el-form-item>
        <el-form-item label="煤种"><el-input v-model="form.coalType" /></el-form-item>
        <el-form-item label="数量(吨)"><el-input-number v-model="form.quantity" :min="0" :precision="2" style="width: 100%" /></el-form-item>
        <el-form-item label="运输方式">
          <el-select v-model="form.transportMode" style="width: 100%">
            <el-option label="汽车" value="TRUCK" />
            <el-option label="火车" value="TRAIN" />
            <el-option label="皮带" value="BELT" />
          </el-select>
        </el-form-item>
        <el-form-item label="车号"><el-input v-model="form.vehicleNo" /></el-form-item>
        <el-form-item label="客户名称"><el-input v-model="form.customerName" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="showDialog = false">取消</el-button><el-button type="primary" @click="saveItem">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createStorageRecords } from '../../mocks/coal'
import { createStorageTransport, deleteStorageTransport, listStorageTransports, updateStorageTransport, type StorageTransportDto } from '../../api/coal-business'

type StorageRow = { id?: number; recordType: string; recordNo?: string; coalType: string; quantity: number; recordTime?: string; transportMode: string; vehicleNo?: string; customerName?: string; status?: string; remark?: string }
type StorageRecordType = 'RAW_IN' | 'PRODUCT_OUT' | 'LOADING'
type StorageTransportMode = 'TRUCK' | 'TRAIN' | 'BELT'
const createLocalRecords = (): StorageRow[] => createStorageRecords() as StorageRow[]
const list = ref<StorageRow[]>(createLocalRecords()); const page = ref(1); const pageSize = ref(10); const filterType = ref(''); const filterDate = ref('')
const showDialog = ref(false); const editingId = ref<number | null>(null); const mockMode = ref(true)
const createDefaultForm = (): StorageRow => ({ recordType: 'RAW_IN', coalType: '', quantity: 0, transportMode: 'TRUCK', vehicleNo: '', customerName: '', status: 'COMPLETED', remark: '' })
const form = ref<StorageRow>(createDefaultForm())
const getTypeText = (type: StorageRecordType | string) => ({ RAW_IN: '原煤入库', PRODUCT_OUT: '产品外运', LOADING: '装车记录' }[type] || type)
const storageTypeTagMap: Record<StorageRecordType, 'info' | 'success' | 'warning'> = { RAW_IN: 'info', PRODUCT_OUT: 'success', LOADING: 'warning' }
const getTypeTag = (type: StorageRecordType | string): 'info' | 'success' | 'warning' => storageTypeTagMap[type as StorageRecordType] || 'info'
const getTransportText = (mode: StorageTransportMode | string) => ({ TRUCK: '汽车', TRAIN: '火车', BELT: '皮带' }[mode] || mode)
const filteredList = computed(() => list.value.filter((item) => (!filterType.value || item.recordType === filterType.value) && (!filterDate.value || item.recordTime?.startsWith(filterDate.value))))
const pagedList = computed(() => filteredList.value.slice((page.value - 1) * pageSize.value, page.value * pageSize.value))
const statistics = computed(() => ({
  rawCoalStock: filteredList.value.filter((item) => item.recordType === 'RAW_IN').reduce((sum, item) => sum + Number(item.quantity || 0), 0),
  productSales: filteredList.value.filter((item) => item.recordType === 'PRODUCT_OUT').reduce((sum, item) => sum + Number(item.quantity || 0), 0),
  loadingCount: filteredList.value.filter((item) => item.recordType === 'LOADING').length,
}))
const mapStorageResponse = (item: StorageTransportDto): StorageRow => ({ id: item.id, recordType: item.recordType, recordNo: item.recordNo, coalType: item.coalType, quantity: Number(item.quantity || 0), recordTime: item.recordTime?.replace('T',' ').slice(0,19), transportMode: item.transportMode, vehicleNo: item.vehicleNo || '', customerName: item.customerName || '', status: item.status || 'COMPLETED', remark: item.remark || '' })
const buildStoragePayload = (row: StorageRow): StorageTransportDto => ({ id: row.id, recordType: row.recordType, recordNo: row.recordNo || `SZ${new Date().toISOString().slice(0,10).replace(/-/g,'')}${String(list.value.length + 1).padStart(3,'0')}`, coalType: row.coalType, quantity: Number(row.quantity || 0), recordTime: (row.recordTime || new Date().toISOString().slice(0,19)).replace(' ','T'), transportMode: row.transportMode, vehicleNo: row.vehicleNo || '', customerName: row.customerName || '', status: row.status || 'COMPLETED', remark: row.remark || '' })
const loadStorageData = async () => { try { const response = await listStorageTransports(); list.value = response.map(mapStorageResponse); mockMode.value = false } catch { list.value = createLocalRecords(); mockMode.value = true } }
const openCreateDialog = () => { editingId.value = null; form.value = createDefaultForm(); showDialog.value = true }
const openEditDialog = (row: StorageRow) => { editingId.value = row.id ?? null; form.value = { ...row }; showDialog.value = true }
const saveItem = async () => {
  const payload = buildStoragePayload(form.value)
  try {
    if (mockMode.value) {
      const next = mapStorageResponse({ ...payload, id: editingId.value || Date.now() }); if (editingId.value) { const index = list.value.findIndex((item) => item.id === editingId.value); if (index !== -1) list.value[index] = next } else list.value.unshift(next)
    } else {
      const saved = editingId.value ? await updateStorageTransport({ ...payload, id: editingId.value }) : await createStorageTransport(payload)
      const next = mapStorageResponse(saved); if (editingId.value) { const index = list.value.findIndex((item) => item.id === editingId.value); if (index !== -1) list.value[index] = next } else list.value.unshift(next)
    }
    ElMessage.success(editingId.value ? '记录已更新' : '记录已新增'); showDialog.value = false
  } catch { ElMessage.error('记录保存失败') }
}
const removeItem = async (id?: number) => { if (!id) return; try { await ElMessageBox.confirm('确认删除该记录？', '提示', { type: 'warning' }); if (!mockMode.value) await deleteStorageTransport(id); list.value = list.value.filter((item) => item.id !== id); ElMessage.success('已删除') } catch (error) { if (error !== 'cancel') ElMessage.error('删除失败') } }

const exportCsv = () => {
  const header = '记录编号,记录类型,煤种,数量(吨),运输方式,车号,客户名称,记录时间,说明'
  const rows = filteredList.value.map((r: any) => `${r.recordNo},${getTypeText(r.recordType)},${r.coalType},${r.quantity},${getTransportText(r.transportMode)},${r.vehicleNo || ''},${r.customerName || ''},${r.recordTime},${r.remark || ''}`)
  const csv = [header, ...rows].join('\n')
  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = `储装记录_${new Date().toISOString().slice(0, 10)}.csv`; a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('储装记录 CSV 已下载')
}

onMounted(() => { loadStorageData() })
</script>

<style scoped>
.storage-v2{min-height:100%;width:min(100%,1800px);margin:0 auto;padding:20px 20px 28px;background:radial-gradient(circle at top right,rgba(73,173,255,.16),transparent 24%),radial-gradient(circle at left center,rgba(0,255,191,.08),transparent 22%),#06131f;color:#ecf6ff}
.hero,.panel,.stat-card{border:1px solid rgba(106,188,255,.14);background:rgba(7,22,36,.88);box-shadow:0 20px 50px rgba(0,0,0,.24),inset 0 1px 0 rgba(140,205,255,.08)}
.hero{display:grid;grid-template-columns:1.6fr auto;gap:24px;padding:24px 26px;border-radius:24px}.eyebrow{margin:0 0 8px;color:#7bc8ff;font-size:12px;letter-spacing:.22em;text-transform:uppercase}.hero h1,.panel-head h2{margin:0}.hero h1{font-size:32px;line-height:1.08}.hero-text{max-width:760px;color:rgba(229,243,255,.72);line-height:1.7}
.stats-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:18px;margin-top:18px}.stat-card{padding:18px;border-radius:20px;background:rgba(11,32,49,.88)}.stat-card span{color:rgba(230,243,255,.58);font-size:12px}.stat-card strong{display:block;margin-top:8px;font-size:28px;color:#8bd8ff}
.panel{padding:22px;border-radius:24px;margin-top:18px}.panel-head,.table-head{display:flex;justify-content:space-between;align-items:center;gap:16px;margin-bottom:18px}
.tools{display:flex;gap:12px}.tools :deep(.el-select),.tools :deep(.el-date-editor){width:180px}.mode-alert{margin-bottom:18px}
.records-table{--el-table-bg-color:transparent;--el-table-tr-bg-color:rgba(11,32,49,.72);--el-table-header-bg-color:rgba(20,52,78,.92);--el-table-border-color:rgba(108,189,255,.12);--el-table-text-color:#e8f4ff;--el-table-header-text-color:#8dd6ff}
.pagination-wrap{display:flex;justify-content:flex-end;margin-top:18px}
@media (max-width:1200px){.hero,.stats-grid{grid-template-columns:1fr}}
@media (max-width:768px){.storage-v2{padding:16px}.hero{padding:20px}.hero h1{font-size:28px}.tools,.table-head{flex-direction:column;align-items:stretch}.tools :deep(.el-select),.tools :deep(.el-date-editor){width:100%}}
</style>
