<template>
  <div class="coal-page-v2 settings-page">
    <div class="settings-header">
      <p class="eyebrow">system settings</p>
      <h1>系统设置中心</h1>
      <p class="header-text">用户管理、角色权限、IOT点位台账、点位映射、系统参数与审计日志。</p>
    </div>

    <div class="settings-body">
      <div class="stat-row">
        <div class="stat-card" v-for="s in statCards" :key="s.label">
          <div class="stat-value">{{ s.value }}</div>
          <div class="stat-label">{{ s.label }}</div>
        </div>
      </div>

      <div class="card">
        <div class="tab-row">
          <button v-for="tab in tabList" :key="tab.key" type="button" :class="['tab-btn', { active: activeTab === tab.key }]" @click="activeTab = tab.key">
            {{ tab.label }}
          </button>
          <div class="tab-spacer"></div>
          <button class="btn-primary" @click="handlePrimaryAction">{{ primaryButtonText }}</button>
        </div>

        <div class="alert info">
          当前使用本地演示数据，IOT接口已预留，认证通过后将自动切换为持久化模式。
        </div>

        <!-- 点位台账 -->
        <template v-if="activeTab === 'tags'">
          <div class="filter-row">
            <input v-model="tagKeyword" class="filter-input" placeholder="点位编码 / 点位名称" />
            <select v-model="tagStatus" class="filter-select">
              <option value="">全部状态</option>
              <option value="enabled">启用</option>
              <option value="disabled">停用</option>
            </select>
          </div>
          <div class="table-wrap">
            <table>
              <thead>
                <tr><th>点位编码</th><th>点位名称</th><th>来源</th><th>设备编码</th><th>源路径</th><th>扫描周期</th><th>状态</th><th>操作</th></tr>
              </thead>
              <tbody>
                <tr v-for="row in filteredTags" :key="row.tagCode">
                  <td>{{ row.tagCode }}</td>
                  <td>{{ row.tagName }}</td>
                  <td>{{ row.sourceType }}</td>
                  <td>{{ row.deviceCode }}</td>
                  <td class="ellipsis">{{ row.sourcePath }}</td>
                  <td>{{ row.scanRate }}ms</td>
                  <td><span class="badge" :class="row.enabled ? 'good' : 'off'">{{ row.enabled ? '启用' : '停用' }}</span></td>
                  <td class="action-cell">
                    <button class="link-btn" @click="openTagDialog(row)">编辑</button>
                    <button class="link-btn" @click="toggleTag(row)">{{ row.enabled ? '停用' : '启用' }}</button>
                    <button class="link-btn danger" @click="removeTag(row)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </template>

        <!-- 点位映射 -->
        <template v-if="activeTab === 'mappings'">
          <div class="filter-row">
            <input v-model="mappingKeyword" class="filter-input" placeholder="业务编码 / 点位编码" />
          </div>
          <div class="table-wrap">
            <table>
              <thead>
                <tr><th>映射编号</th><th>业务编码</th><th>业务名称</th><th>点位编码</th><th>状态</th><th>操作</th></tr>
              </thead>
              <tbody>
                <tr v-for="row in filteredMappings" :key="row.mappingId">
                  <td>{{ row.mappingId }}</td>
                  <td>{{ row.businessCode }}</td>
                  <td>{{ row.businessName }}</td>
                  <td>{{ row.tagCode }}</td>
                  <td><span class="badge" :class="row.enabled ? 'good' : 'off'">{{ row.enabled ? '启用' : '停用' }}</span></td>
                  <td class="action-cell">
                    <button class="link-btn" @click="openMappingDialog(row)">编辑</button>
                    <button class="link-btn" @click="toggleMapping(row)">{{ row.enabled ? '停用' : '启用' }}</button>
                    <button class="link-btn danger" @click="removeMapping(row)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </template>

        <!-- 用户与参数 -->
        <template v-if="activeTab === 'users'">
          <h3 class="section-title">用户列表</h3>
          <div class="table-wrap">
            <table>
              <thead>
                <tr><th>用户名</th><th>姓名</th><th>角色</th><th>部门</th><th>联系电话</th></tr>
              </thead>
              <tbody>
                <tr v-for="u in users" :key="u.username">
                  <td>{{ u.username }}</td>
                  <td>{{ u.realName }}</td>
                  <td>{{ u.role }}</td>
                  <td>{{ u.department }}</td>
                  <td>{{ u.phone }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <h3 class="section-title" style="margin-top:24px">系统参数</h3>
          <div class="params-grid">
            <div class="param-item">
              <label>日产量基线 (吨)</label>
              <input type="number" v-model.number="systemParams.dailyOutput" />
            </div>
            <div class="param-item">
              <label>煤质合格率目标 (%)</label>
              <input type="number" v-model.number="systemParams.qualityStandard" step="0.1" />
            </div>
            <div class="param-item">
              <label>设备温度报警上限 (℃)</label>
              <input type="number" v-model.number="systemParams.tempLimit" />
            </div>
            <div class="param-item">
              <label>报警推送方式</label>
              <select v-model="systemParams.alarmMethod">
                <option value="message">站内消息</option>
                <option value="sms">短信通知</option>
                <option value="both">全部</option>
              </select>
            </div>
          </div>
          <div style="margin-top:16px">
            <button class="btn-primary" @click="saveParams">保存参数</button>
          </div>
        </template>

        <!-- 审计日志 -->
        <template v-if="activeTab === 'audit'">
          <div class="table-wrap">
            <table>
              <thead>
                <tr><th>时间</th><th>操作人</th><th>模块</th><th>操作</th><th>详情</th></tr>
              </thead>
              <tbody>
                <tr v-for="(log, idx) in auditLogs" :key="idx">
                  <td>{{ log.time }}</td>
                  <td>{{ log.user }}</td>
                  <td>{{ log.module }}</td>
                  <td>{{ log.action }}</td>
                  <td class="ellipsis">{{ log.detail }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </template>
      </div>

      <!-- 点位编辑弹窗 -->
      <div v-if="showTagDialog" class="modal-overlay" @click.self="showTagDialog = false">
        <div class="modal">
          <h3>{{ editingTagCode ? '编辑点位' : '新增点位' }}</h3>
          <div class="form-grid">
            <div class="form-field"><label>点位编码</label><input v-model="tagForm.tagCode" :disabled="Boolean(editingTagCode)" /></div>
            <div class="form-field"><label>点位名称</label><input v-model="tagForm.tagName" /></div>
            <div class="form-field"><label>来源类型</label>
              <select v-model="tagForm.sourceType"><option value="MOCK">MOCK</option><option value="KEPSERVER">KEPSERVER</option></select>
            </div>
            <div class="form-field"><label>源路径</label><input v-model="tagForm.sourcePath" /></div>
            <div class="form-field"><label>设备编码</label><input v-model="tagForm.deviceCode" /></div>
            <div class="form-field"><label>设备名称</label><input v-model="tagForm.deviceName" /></div>
            <div class="form-field"><label>单位</label><input v-model="tagForm.unit" /></div>
            <div class="form-field"><label>扫描周期(ms)</label><input type="number" v-model.number="tagForm.scanRate" /></div>
          </div>
          <div class="modal-footer">
            <button class="btn-secondary" @click="showTagDialog = false">取消</button>
            <button class="btn-primary" @click="saveTag">保存</button>
          </div>
        </div>
      </div>

      <!-- 映射编辑弹窗 -->
      <div v-if="showMappingDialog" class="modal-overlay" @click.self="showMappingDialog = false">
        <div class="modal">
          <h3>{{ editingMappingId ? '编辑映射' : '新增映射' }}</h3>
          <div class="form-grid">
            <div class="form-field"><label>映射编号</label><input v-model="mappingForm.mappingId" :disabled="Boolean(editingMappingId)" /></div>
            <div class="form-field"><label>业务编码</label><input v-model="mappingForm.businessCode" /></div>
            <div class="form-field"><label>业务名称</label><input v-model="mappingForm.businessName" /></div>
            <div class="form-field"><label>点位编码</label><input v-model="mappingForm.tagCode" /></div>
            <div class="form-field"><label>源路径</label><input v-model="mappingForm.sourcePath" /></div>
            <div class="form-field"><label>转换规则</label><input v-model="mappingForm.transformRule" /></div>
          </div>
          <div class="modal-footer">
            <button class="btn-secondary" @click="showMappingDialog = false">取消</button>
            <button class="btn-primary" @click="saveMapping">保存</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { IotTagDefinition, IotTagMapping } from '../../api/iot'
import {
  createIotTag,
  createIotTagMapping,
  deleteIotTag,
  deleteIotTagMapping,
  disableIotTag,
  disableIotTagMapping,
  enableIotTag,
  enableIotTagMapping,
  getIotTagMappings,
  getIotTags,
  updateIotTag,
  updateIotTagMapping,
} from '../../api/iot'

type TagRow = { tagCode: string; tagName: string; sourceType: string; sourcePath: string; deviceCode: string; deviceName: string; unit: string; scanRate: number; enabled: boolean }
type MappingRow = { mappingId: string; tagCode: string; businessCode: string; businessName: string; sourcePath: string; transformRule: string; enabled: boolean }

const activeTab = ref('tags')
const tagKeyword = ref('')
const tagStatus = ref('')
const mappingKeyword = ref('')

const tabList = [
  { key: 'tags', label: '点位台账' },
  { key: 'mappings', label: '点位映射' },
  { key: 'users', label: '用户与参数' },
  { key: 'audit', label: '审计日志' },
]

const tags = ref<TagRow[]>([
  { tagCode: 'coal.feed.rate', tagName: '原煤给料量', sourceType: 'MOCK', sourcePath: 'mock.feed.rate', deviceCode: 'FEEDER-01', deviceName: '1号给料机', unit: 't/h', scanRate: 1000, enabled: true },
  { tagCode: 'crusher.current', tagName: '破碎机电流', sourceType: 'MOCK', sourcePath: 'mock.crusher.current', deviceCode: 'CRUSHER-01', deviceName: '主破碎机', unit: 'A', scanRate: 1000, enabled: true },
  { tagCode: 'screen.vibration', tagName: '振动筛振幅', sourceType: 'MOCK', sourcePath: 'mock.screen.vibration', deviceCode: 'SCREEN-01', deviceName: '1号振动筛', unit: 'mm', scanRate: 500, enabled: true },
  { tagCode: 'belt.speed', tagName: '皮带速度', sourceType: 'MOCK', sourcePath: 'mock.belt.speed', deviceCode: 'BELT-01', deviceName: '主运输皮带', unit: 'm/s', scanRate: 1000, enabled: false },
])

const mappings = ref<MappingRow[]>([
  { mappingId: 'MAP-001', tagCode: 'coal.feed.rate', businessCode: 'dashboard.feedRate', businessName: '大屏给料量', sourcePath: 'mock.feed.rate', transformRule: 'identity', enabled: true },
  { mappingId: 'MAP-002', tagCode: 'crusher.current', businessCode: 'equipment.crusherCurrent', businessName: '破碎机电流', sourcePath: 'mock.crusher.current', transformRule: 'identity', enabled: true },
  { mappingId: 'MAP-003', tagCode: 'screen.vibration', businessCode: 'equipment.screenVibration', businessName: '振动筛振幅', sourcePath: 'mock.screen.vibration', transformRule: 'identity', enabled: true },
])

const users = ref([
  { username: 'admin', realName: '系统管理员', role: '系统管理员', department: '信息中心', phone: '13800138000' },
  { username: 'dispatch01', realName: '张磊', role: '生产调度员', department: '生产调度', phone: '13800138011' },
  { username: 'quality01', realName: '李明', role: '质检主任', department: '质检中心', phone: '13800138022' },
  { username: 'maint01', realName: '王工', role: '设备主管', department: '机电动力部', phone: '13800138033' },
])

const systemParams = ref({ dailyOutput: 12580, qualityStandard: 96.5, tempLimit: 80, alarmMethod: 'message' })

const auditLogs = ref([
  { time: '2026-04-09 10:32', user: 'admin', module: '点位台账', action: '新增', detail: '新增点位 coal.feed.rate' },
  { time: '2026-04-09 09:15', user: 'admin', module: '系统参数', action: '修改', detail: '日产量基线从12000调整为12580' },
  { time: '2026-04-08 16:42', user: 'dispatch01', module: '调度记录', action: '新增', detail: '新增白班调度记录' },
  { time: '2026-04-08 14:20', user: 'quality01', module: '煤质检测', action: '录入', detail: '录入精煤样品B检测数据' },
  { time: '2026-04-07 11:03', user: 'admin', module: '用户管理', action: '新增', detail: '新增用户 maint01' },
])

const statCards = computed(() => [
  { value: tags.value.length, label: '点位台账数' },
  { value: tags.value.filter(t => t.enabled).length, label: '启用点位' },
  { value: mappings.value.length, label: '点位映射数' },
  { value: users.value.length, label: '平台用户' },
])

const primaryButtonText = computed(() => {
  if (activeTab.value === 'tags') return '新增点位'
  if (activeTab.value === 'mappings') return '新增映射'
  if (activeTab.value === 'users') return '新增用户'
  return '导出日志'
})

const filteredTags = computed(() => tags.value.filter(item => {
  const kw = !tagKeyword.value || item.tagCode.includes(tagKeyword.value) || item.tagName.includes(tagKeyword.value)
  const st = !tagStatus.value || (tagStatus.value === 'enabled' && item.enabled) || (tagStatus.value === 'disabled' && !item.enabled)
  return kw && st
}))

const filteredMappings = computed(() => mappings.value.filter(item =>
  !mappingKeyword.value || item.mappingId.includes(mappingKeyword.value) || item.tagCode.includes(mappingKeyword.value) || item.businessCode.includes(mappingKeyword.value)
))

const showTagDialog = ref(false)
const editingTagCode = ref('')
const tagForm = ref<Partial<TagRow>>({})

const showMappingDialog = ref(false)
const editingMappingId = ref('')
const mappingForm = ref<Partial<MappingRow>>({})

function addAuditLog(module: string, action: string, detail: string) {
  const now = new Date()
  const time = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')} ${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`
  auditLogs.value.unshift({ time, user: 'admin', module, action, detail })
}

function persistLocal() {
  try {
    localStorage.setItem('coal_settings_tags', JSON.stringify(tags.value))
    localStorage.setItem('coal_settings_mappings', JSON.stringify(mappings.value))
    localStorage.setItem('coal_settings_params', JSON.stringify(systemParams.value))
  } catch { /* quota exceeded */ }
}

function loadLocal() {
  try {
    const t = localStorage.getItem('coal_settings_tags')
    if (t) tags.value = JSON.parse(t)
    const m = localStorage.getItem('coal_settings_mappings')
    if (m) mappings.value = JSON.parse(m)
    const p = localStorage.getItem('coal_settings_params')
    if (p) systemParams.value = JSON.parse(p)
  } catch { /* ignore */ }
}
loadLocal()

function mapApiTag(t: IotTagDefinition): TagRow {
  return {
    tagCode: t.tagCode,
    tagName: t.tagName,
    sourceType: t.sourceType,
    sourcePath: t.sourcePath,
    deviceCode: t.deviceCode || '',
    deviceName: t.deviceName || '',
    unit: t.unit || '',
    scanRate: t.scanRate || 1000,
    enabled: t.enabled,
  }
}

function mapApiMapping(m: IotTagMapping): MappingRow {
  return {
    mappingId: m.mappingId,
    tagCode: m.tagCode,
    businessCode: m.businessCode,
    businessName: m.businessName,
    sourcePath: m.sourcePath,
    transformRule: m.transformRule || 'identity',
    enabled: m.enabled,
  }
}

async function loadIotFromServer() {
  try {
    const tr = await getIotTags({ pageNum: 1, pageSize: 500 })
    const records = tr.data?.records
    if (records && records.length > 0) {
      tags.value = records.map(mapApiTag)
      persistLocal()
    }
  } catch {
    /* 后端未启动 */
  }
  try {
    const mr = await getIotTagMappings()
    const list = mr.data
    if (Array.isArray(list) && list.length > 0) {
      mappings.value = list.map(mapApiMapping)
      persistLocal()
    }
  } catch {
    /* 忽略 */
  }
}

onMounted(() => {
  void loadIotFromServer()
})

const handlePrimaryAction = () => {
  if (activeTab.value === 'tags') openTagDialog()
  else if (activeTab.value === 'mappings') openMappingDialog()
  else if (activeTab.value === 'users') ElMessage.info('用户新增入口已预留')
  else exportAuditLog()
}

function exportAuditLog() {
  const csv = ['时间,操作人,模块,操作,详情', ...auditLogs.value.map(l => `${l.time},${l.user},${l.module},${l.action},${l.detail}`)].join('\n')
  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = `审计日志_${new Date().toISOString().slice(0, 10)}.csv`; a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('审计日志 CSV 已下载')
}

const openTagDialog = (row?: TagRow) => {
  editingTagCode.value = row?.tagCode || ''
  tagForm.value = row ? { ...row } : { tagCode: '', tagName: '', sourceType: 'MOCK', sourcePath: '', deviceCode: '', deviceName: '', unit: '', scanRate: 1000, enabled: true }
  showTagDialog.value = true
}

const tagPayload = (row: TagRow) => ({
  tagName: row.tagName,
  sourceType: row.sourceType,
  sourcePath: row.sourcePath,
  deviceCode: row.deviceCode || '',
  deviceName: row.deviceName || '',
  areaCode: '',
  dataType: 'DOUBLE',
  unit: row.unit || '',
  scanRate: row.scanRate || 1000,
  deadband: 0,
  qualityRule: '',
  enabled: row.enabled,
  remark: '',
})

const saveTag = async () => {
  if (!tagForm.value.tagCode || !tagForm.value.tagName) { ElMessage.warning('请填写完整的点位信息'); return }
  const next = { ...tagForm.value, enabled: tagForm.value.enabled ?? true } as TagRow
  const idx = tags.value.findIndex(t => t.tagCode === editingTagCode.value)
  if (idx !== -1) tags.value[idx] = next
  else tags.value.unshift(next)
  const act = editingTagCode.value ? '编辑' : '新增'
  addAuditLog('点位台账', act, `${act}点位 ${next.tagCode}`)
  persistLocal()
  try {
    if (editingTagCode.value) await updateIotTag(editingTagCode.value, { ...tagPayload(next), tagCode: editingTagCode.value })
    else await createIotTag({ ...tagPayload(next), tagCode: next.tagCode })
  } catch {
    ElMessage.warning('IoT 接口未保存成功，已保留本地与页面数据')
  }
  ElMessage.success(`点位已${act}`)
  showTagDialog.value = false
}

const toggleTag = async (row: TagRow) => {
  row.enabled = !row.enabled
  try {
    if (row.enabled) await enableIotTag(row.tagCode)
    else await disableIotTag(row.tagCode)
  } catch { /* 仅本地 */ }
  addAuditLog('点位台账', row.enabled ? '启用' : '停用', `${row.tagCode}`)
  persistLocal()
  ElMessage.success(row.enabled ? '点位已启用' : '点位已停用')
}
const removeTag = async (row: TagRow) => {
  try { await deleteIotTag(row.tagCode) } catch { /* 仅本地 */ }
  tags.value = tags.value.filter(t => t.tagCode !== row.tagCode)
  addAuditLog('点位台账', '删除', `删除点位 ${row.tagCode}`)
  persistLocal()
  ElMessage.success('点位已删除')
}

const openMappingDialog = (row?: MappingRow) => {
  editingMappingId.value = row?.mappingId || ''
  mappingForm.value = row ? { ...row } : { mappingId: '', tagCode: '', businessCode: '', businessName: '', sourcePath: '', transformRule: 'identity', enabled: true }
  showMappingDialog.value = true
}

const saveMapping = async () => {
  if (!mappingForm.value.tagCode || !mappingForm.value.businessCode) { ElMessage.warning('请填写完整的映射信息'); return }
  const next = { ...mappingForm.value, mappingId: mappingForm.value.mappingId || `MAP-${Date.now()}`, enabled: mappingForm.value.enabled ?? true } as MappingRow
  const idx = mappings.value.findIndex(m => m.mappingId === editingMappingId.value)
  if (idx !== -1) mappings.value[idx] = next
  else mappings.value.unshift(next)
  const act = editingMappingId.value ? '编辑' : '新增'
  addAuditLog('点位映射', act, `${act}映射 ${next.mappingId}`)
  persistLocal()
  try {
    const body = {
      tagCode: next.tagCode,
      businessCode: next.businessCode,
      businessName: next.businessName,
      sourcePath: next.sourcePath,
      transformRule: next.transformRule || 'identity',
      enabled: next.enabled,
      remark: '',
    }
    if (editingMappingId.value) await updateIotTagMapping(editingMappingId.value, { ...body, mappingId: editingMappingId.value })
    else await createIotTagMapping({ ...body, mappingId: next.mappingId })
  } catch {
    ElMessage.warning('IoT 映射接口未保存成功，已保留本地数据')
  }
  ElMessage.success(`映射已${act}`)
  showMappingDialog.value = false
}

const toggleMapping = async (row: MappingRow) => {
  row.enabled = !row.enabled
  try {
    if (row.enabled) await enableIotTagMapping(row.mappingId)
    else await disableIotTagMapping(row.mappingId)
  } catch { /* 仅本地 */ }
  addAuditLog('点位映射', row.enabled ? '启用' : '停用', `${row.mappingId}`)
  persistLocal()
  ElMessage.success(row.enabled ? '映射已启用' : '映射已停用')
}
const removeMapping = async (row: MappingRow) => {
  try { await deleteIotTagMapping(row.mappingId) } catch { /* 仅本地 */ }
  mappings.value = mappings.value.filter(m => m.mappingId !== row.mappingId)
  addAuditLog('点位映射', '删除', `删除映射 ${row.mappingId}`)
  persistLocal()
  ElMessage.success('映射已删除')
}

const saveParams = () => { addAuditLog('系统参数', '修改', `日产量=${systemParams.value.dailyOutput}, 合格率=${systemParams.value.qualityStandard}%, 温度上限=${systemParams.value.tempLimit}℃`); persistLocal(); ElMessage.success('系统参数已保存') }
</script>

<style scoped>
.coal-page-v2{min-height:100vh;background:#091019;color:#eef6ff}
.settings-header{width:min(100%,1800px);margin:0 auto;padding:20px 20px 0}
.eyebrow{margin:0 0 10px;color:#78cfff;font-size:12px;letter-spacing:.2em;text-transform:uppercase}
.settings-header h1{margin:0;font-size:36px}
.header-text{max-width:760px;margin:10px 0 16px;color:#97aac0;font-size:15px;line-height:1.7}
.settings-body{width:min(100%,1800px);margin:0 auto;padding:0 20px 20px}
.stat-row{display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin-bottom:16px}
.stat-card{padding:18px;border:1px solid rgba(96,183,255,.12);border-radius:16px;background:rgba(8,19,30,.9);text-align:center}
.stat-value{font-size:28px;font-weight:700;color:#67d8ff}
.stat-label{margin-top:6px;color:rgba(227,239,250,.6);font-size:13px}
.card{padding:22px;border:1px solid rgba(96,183,255,.12);border-radius:18px;background:rgba(8,19,30,.9)}
.tab-row{display:flex;align-items:center;gap:8px;margin-bottom:16px;flex-wrap:wrap}
.tab-btn{padding:10px 18px;border:1px solid rgba(96,183,255,.12);border-radius:999px;background:rgba(14,27,39,.92);color:rgba(226,241,255,.82);cursor:pointer;transition:.2s}
.tab-btn.active,.tab-btn:hover{border-color:rgba(87,216,255,.34);background:linear-gradient(135deg,rgba(72,176,255,.2),rgba(30,216,166,.14));color:#fff}
.tab-spacer{flex:1}
.btn-primary{padding:10px 20px;border:0;border-radius:12px;background:linear-gradient(135deg,#2cd1ff,#21b7ff);color:#082338;font-weight:600;cursor:pointer}
.btn-secondary{padding:10px 20px;border:1px solid rgba(96,183,255,.2);border-radius:12px;background:transparent;color:rgba(226,241,255,.8);cursor:pointer}
.alert{padding:14px 18px;border-radius:12px;margin-bottom:16px;font-size:13px;line-height:1.6}
.alert.info{background:rgba(87,216,255,.08);border:1px solid rgba(87,216,255,.2);color:rgba(227,239,250,.75)}
.filter-row{display:flex;gap:10px;margin-bottom:14px}
.filter-input,.filter-select{padding:10px 14px;border:1px solid rgba(96,183,255,.18);border-radius:10px;background:rgba(14,27,39,.9);color:#f3faff;font-size:14px}
.filter-input{width:260px}
.filter-select{width:140px}
.table-wrap{overflow-x:auto}
.table-wrap table{width:100%;border-collapse:collapse}
.table-wrap th,.table-wrap td{padding:12px 14px;text-align:left;border-bottom:1px solid rgba(96,183,255,.08)}
.table-wrap th{color:#7bc8ff;font-size:12px;font-weight:600;text-transform:uppercase;letter-spacing:.06em}
.table-wrap td{color:rgba(227,239,250,.82);font-size:14px}
.ellipsis{max-width:220px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.badge{display:inline-block;padding:3px 10px;border-radius:8px;font-size:12px;font-weight:600}
.badge.good{background:rgba(24,240,191,.15);color:#18f0bf}
.badge.off{background:rgba(255,255,255,.08);color:rgba(227,239,250,.5)}
.action-cell{display:flex;gap:6px}
.link-btn{padding:4px 10px;border:0;border-radius:6px;background:transparent;color:#67d8ff;cursor:pointer;font-size:13px}
.link-btn:hover{background:rgba(87,216,255,.1)}
.link-btn.danger{color:#ff6f73}
.link-btn.danger:hover{background:rgba(255,111,115,.1)}
.section-title{margin:0 0 12px;font-size:16px;color:#f3faff}
.params-grid{display:grid;grid-template-columns:repeat(2,1fr);gap:16px}
.param-item{display:flex;flex-direction:column;gap:6px}
.param-item label{color:#7bc8ff;font-size:12px;font-weight:600}
.param-item input,.param-item select{padding:10px 14px;border:1px solid rgba(96,183,255,.18);border-radius:10px;background:rgba(14,27,39,.9);color:#f3faff;font-size:14px}
.modal-overlay{position:fixed;inset:0;z-index:5000;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,.6);backdrop-filter:blur(4px)}
.modal{width:min(90%,680px);max-height:80vh;overflow-y:auto;padding:28px;border:1px solid rgba(96,183,255,.2);border-radius:20px;background:#0b1926}
.modal h3{margin:0 0 20px;font-size:18px;color:#f3faff}
.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:14px}
.form-field{display:flex;flex-direction:column;gap:6px}
.form-field label{color:#7bc8ff;font-size:12px;font-weight:600}
.form-field input,.form-field select{padding:10px 14px;border:1px solid rgba(96,183,255,.18);border-radius:10px;background:rgba(14,27,39,.9);color:#f3faff;font-size:14px}
.form-field input:disabled{opacity:.5}
.modal-footer{display:flex;justify-content:flex-end;gap:10px;margin-top:20px}
@media(max-width:900px){.stat-row{grid-template-columns:repeat(2,1fr)}.params-grid,.form-grid{grid-template-columns:1fr}}
</style>
