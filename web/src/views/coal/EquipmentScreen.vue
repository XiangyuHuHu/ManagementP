<template>
  <div class="equipment-screen">
    <section class="screen-header">
      <div class="header-side">
        <span>{{ weather.city }}</span>
        <strong>{{ weather.temp }}</strong>
        <span>{{ weather.text }}</span>
      </div>
      <div class="header-title">
        <h1>设备管理</h1>
      </div>
      <div class="header-side header-side--right">
        <span>设备总数</span>
        <strong>209</strong>
        <span>在线监控 71 台</span>
      </div>
    </section>

    <section class="screen-grid">
      <article class="panel">
        <div class="panel-title">告警历史</div>
        <table class="mini-table">
          <thead>
            <tr>
              <th>设备名称</th>
              <th>检测点</th>
              <th>当前状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in alarmHistory" :key="item.device">
              <td>{{ item.device }}</td>
              <td>{{ item.point }}</td>
              <td>{{ item.status }}</td>
            </tr>
          </tbody>
        </table>
      </article>

      <article class="panel panel--main">
        <div class="panel-title">设备信息</div>
        <div class="equipment-main">
          <div ref="devicePieRef" class="chart donut"></div>
          <div class="monitor-block">
            <div class="monitor-title">温度振动监控设备</div>
            <div class="monitor-progress">
              <div class="monitor-bars">
                <span v-for="index in 9" :key="index"></span>
              </div>
              <strong>16%</strong>
            </div>
            <div class="equipment-kpis">
              <div class="kpi">
                <span>在用</span>
                <strong class="gold">209</strong>
              </div>
              <div class="kpi">
                <span>维修</span>
                <strong class="orange">0</strong>
              </div>
              <div class="kpi">
                <span>备用</span>
                <strong class="cyan">0</strong>
              </div>
              <div class="kpi">
                <span>报废</span>
                <strong class="red">0</strong>
              </div>
              <div class="kpi">
                <span>在线监控设备</span>
                <strong class="gold">71</strong>
              </div>
              <div class="kpi">
                <span>运行设备</span>
                <strong class="orange">12</strong>
              </div>
              <div class="kpi">
                <span>停机设备</span>
                <strong class="cyan">10</strong>
              </div>
            </div>
          </div>
        </div>
      </article>

      <article class="panel">
        <div class="panel-title">告警高发设备</div>
        <div class="rank-list">
          <div v-for="item in alarmRanking" :key="item.name" class="rank-item">
            <div class="rank-top">
              <span>No.{{ item.rank }}</span>
              <strong>{{ item.name }}</strong>
              <b>{{ item.count }}</b>
            </div>
            <div class="rank-bar">
              <div :style="{ width: `${item.percent}%` }"></div>
            </div>
          </div>
        </div>
      </article>

      <article class="panel">
        <div class="panel-title panel-title--action">
          <span>润滑维修记录</span>
          <button type="button" class="action-btn" @click="showLedger = true">查看台账树</button>
        </div>
        <table class="mini-table">
          <thead>
            <tr>
              <th>时间</th>
              <th>设备名称</th>
              <th>地点</th>
              <th>类型</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in maintainRows" :key="item.time + item.device">
              <td>{{ item.time }}</td>
              <td>{{ item.device }}</td>
              <td>{{ item.location }}</td>
              <td>{{ item.type }}</td>
            </tr>
          </tbody>
        </table>
      </article>

      <article class="panel panel--wide">
        <div class="panel-split">
          <div class="split-block">
            <div class="panel-title">在线监控</div>
            <div ref="onlinePieRef" class="chart"></div>
          </div>
          <div class="split-block">
            <div class="panel-title">告警等级占比</div>
            <div ref="alarmPieRef" class="chart"></div>
          </div>
          <div class="split-block runtime-block">
            <div class="panel-title">运行时间</div>
            <div class="runtime-number">3793 min</div>
          </div>
          <div class="split-block">
            <div class="panel-title">设备巡检</div>
            <div ref="inspectBarRef" class="chart"></div>
          </div>
        </div>
      </article>
    </section>

    <el-dialog v-model="showLedger" title="台账树" width="88%" class="blue-dialog">
      <div class="ledger-layout">
        <aside class="ledger-tree">
          <div class="ledger-toolbar">
            <el-select v-model="ledgerMode" size="large">
              <el-option label="按系统" value="system" />
              <el-option label="按车间" value="workshop" />
            </el-select>
            <el-input v-model="ledgerKeyword" placeholder="输入关键字进行过滤" />
          </div>
          <el-tree
            :data="filteredLedgerTree"
            node-key="id"
            default-expand-all
            :expand-on-click-node="false"
            :current-node-key="selectedLedgerId"
            @node-click="handleLedgerSelect"
          />
        </aside>

        <section class="ledger-detail">
          <div class="ledger-tabs">
            <button
              v-for="tab in ledgerTabs"
              :key="tab"
              type="button"
              :class="['ledger-tab', { active: activeLedgerTab === tab }]"
              @click="activeLedgerTab = tab"
            >
              {{ tab }}
            </button>
          </div>

          <div class="ledger-card" v-if="selectedDevice">
            <div class="device-hero">
              <div class="device-image"></div>
              <div class="device-meta">
                <div class="meta-row"><span>设备名称</span><strong>{{ selectedDevice.name }}</strong></div>
                <div class="meta-row"><span>规格型号</span><strong>{{ selectedDevice.model }}</strong></div>
                <div class="meta-row"><span>设备类型</span><strong>{{ selectedDevice.type }}</strong></div>
                <div class="meta-row"><span>当前状态</span><strong>{{ selectedDevice.status }}</strong></div>
                <div class="meta-row"><span>使用部门</span><strong>{{ selectedDevice.department }}</strong></div>
              </div>
            </div>

            <div v-if="activeLedgerTab === '设备属性'" class="detail-grid">
              <div class="detail-cell"><span>安装位置</span><strong>{{ selectedDevice.location }}</strong></div>
              <div class="detail-cell"><span>启用日期</span><strong>{{ selectedDevice.startDate }}</strong></div>
              <div class="detail-cell"><span>管理编码</span><strong>{{ selectedDevice.code }}</strong></div>
              <div class="detail-cell"><span>责任人</span><strong>{{ selectedDevice.owner }}</strong></div>
            </div>

            <div v-else-if="activeLedgerTab === '技术参数'" class="detail-grid">
              <div v-for="item in selectedDevice.params" :key="item.label" class="detail-cell">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }}</strong>
              </div>
            </div>

            <table v-else-if="activeLedgerTab === '维修台账'" class="detail-table">
              <thead>
                <tr>
                  <th>日期</th>
                  <th>类型</th>
                  <th>内容</th>
                  <th>执行人</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in selectedDevice.repairs" :key="item.date + item.type">
                  <td>{{ item.date }}</td>
                  <td>{{ item.type }}</td>
                  <td>{{ item.content }}</td>
                  <td>{{ item.user }}</td>
                </tr>
              </tbody>
            </table>

            <table v-else-if="activeLedgerTab === '设备部件'" class="detail-table">
              <thead>
                <tr>
                  <th>部件</th>
                  <th>状态</th>
                  <th>寿命</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in selectedDevice.parts" :key="item.name">
                  <td>{{ item.name }}</td>
                  <td>{{ item.status }}</td>
                  <td>{{ item.life }}</td>
                </tr>
              </tbody>
            </table>

            <div v-else class="detail-grid">
              <div class="detail-cell"><span>近 24 小时运行时长</span><strong>{{ selectedDevice.runtime }}</strong></div>
              <div class="detail-cell"><span>平均温度</span><strong>{{ selectedDevice.avgTemp }}</strong></div>
              <div class="detail-cell"><span>平均振动</span><strong>{{ selectedDevice.avgVibration }}</strong></div>
              <div class="detail-cell"><span>综合健康评分</span><strong>{{ selectedDevice.health }}</strong></div>
            </div>
          </div>
        </section>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { echarts } from '../../utils/echarts'

type LedgerNode = {
  id: string
  label: string
  children?: LedgerNode[]
  deviceKey?: string
}

type DeviceLedger = {
  key: string
  name: string
  model: string
  type: string
  status: string
  department: string
  location: string
  startDate: string
  code: string
  owner: string
  runtime: string
  avgTemp: string
  avgVibration: string
  health: string
  params: Array<{ label: string; value: string }>
  repairs: Array<{ date: string; type: string; content: string; user: string }>
  parts: Array<{ name: string; status: string; life: string }>
}

const weather = { city: '鄂尔多斯市', temp: '-7°', text: '晴' }

const alarmHistory = [
  { device: '8532皮带机', point: '超高压二楼', status: '运行' },
  { device: '8531皮带机', point: '超高压二楼', status: '运行' },
  { device: '8530皮带机', point: '超高压二楼', status: '运行' },
  { device: '8551皮带机', point: '8551皮带机头', status: '运行' },
  { device: '8553皮带机', point: '8553皮带机头', status: '运行' },
]

const alarmRanking = [
  { rank: 1, name: '101原煤皮带机', count: 6, percent: 100 },
  { rank: 2, name: '301皮带机', count: 4, percent: 72 },
  { rank: 3, name: '311离心机', count: 4, percent: 72 },
  { rank: 4, name: '103刮板机', count: 2, percent: 46 },
  { rank: 5, name: '315矸石脱介筛', count: 2, percent: 46 },
]

const maintainRows = [
  { time: '2026-04-12', device: '602精煤压滤机入料泵', location: '主洗车间', type: '润滑' },
  { time: '2026-04-11', device: '311精煤卧式离心机', location: '离心机组', type: '维护' },
  { time: '2026-04-10', device: '101带式输送机', location: '原煤仓上', type: '检修' },
]

const ledgerTabs = ['设备属性', '技术参数', '维修台账', '设备部件', '设备运行']
const activeLedgerTab = ref('设备属性')
const showLedger = ref(false)
const ledgerMode = ref('system')
const ledgerKeyword = ref('')
const selectedLedgerId = ref('100-pump')

const deviceLedgers: DeviceLedger[] = [
  {
    key: '100-pump',
    name: '100扫地泵',
    model: 'Y225M-6',
    type: '离心泵',
    status: '在用',
    department: '滨海金地',
    location: '原煤仓上',
    startDate: '2021-06-18',
    code: 'EQ-100-001',
    owner: '机电车间',
    runtime: '21.6 h',
    avgTemp: '48.2°C',
    avgVibration: '1.8 mm/s',
    health: '92 分',
    params: [
      { label: '额定功率', value: '37 kW' },
      { label: '额定流量', value: '260 m³/h' },
      { label: '额定扬程', value: '28 m' },
      { label: '额定电压', value: '380 V' },
    ],
    repairs: [
      { date: '2026-04-10', type: '定期保养', content: '更换润滑油并检查联轴器', user: '周洋' },
      { date: '2026-03-18', type: '检修', content: '处理入口法兰轻微渗漏', user: '王强' },
    ],
    parts: [
      { name: '叶轮', status: '正常', life: '72%' },
      { name: '轴承', status: '关注', life: '58%' },
      { name: '机械密封', status: '正常', life: '81%' },
    ],
  },
  {
    key: '101-belt',
    name: '101带式输送机',
    model: 'DTII-101',
    type: '输送设备',
    status: '在用',
    department: '生产运行部',
    location: '原煤仓上',
    startDate: '2020-11-03',
    code: 'EQ-101-001',
    owner: '生产一班',
    runtime: '22.8 h',
    avgTemp: '42.6°C',
    avgVibration: '2.3 mm/s',
    health: '88 分',
    params: [
      { label: '带宽', value: '1000 mm' },
      { label: '带速', value: '2.5 m/s' },
      { label: '输送能力', value: '450 t/h' },
      { label: '功率', value: '45 kW' },
    ],
    repairs: [
      { date: '2026-04-09', type: '巡检', content: '清理滚筒粘煤', user: '李超' },
      { date: '2026-03-22', type: '维修', content: '更换一组托辊', user: '赵凯' },
    ],
    parts: [
      { name: '驱动滚筒', status: '正常', life: '76%' },
      { name: '托辊', status: '关注', life: '61%' },
      { name: '清扫器', status: '正常', life: '80%' },
    ],
  },
]

const ledgerTree: LedgerNode[] = [
  {
    id: 'root-system',
    label: '设备树',
    children: [
      {
        id: 'raw-up',
        label: '原煤仓上',
        children: [
          { id: '100-pump', label: '100扫地泵', deviceKey: '100-pump' },
          { id: '101-belt', label: '101带式输送机', deviceKey: '101-belt' },
          { id: '102-remove', label: '102除铁器' },
          { id: '103-scraper', label: '103刮板输送机' },
        ],
      },
      { id: 'raw-down', label: '原煤仓下' },
      { id: 'screen-workshop', label: '筛分车间' },
      { id: 'block-workshop', label: '块煤车间' },
      { id: 'slurry-room', label: '末煤泵房' },
      { id: 'filter-room', label: '压滤车间' },
      { id: 'high-pressure', label: '超高压车间' },
    ],
  },
]

const filteredLedgerTree = computed(() => {
  const keyword = ledgerKeyword.value.trim()
  if (!keyword) return ledgerTree

  const filterNodes = (nodes: LedgerNode[]): LedgerNode[] =>
    nodes
      .map((node) => {
        const children = node.children ? filterNodes(node.children) : []
        const matched = node.label.includes(keyword)
        if (matched || children.length > 0) return { ...node, children }
        return null
      })
      .filter(Boolean) as LedgerNode[]

  return filterNodes(ledgerTree)
})

const selectedDevice = computed(() => deviceLedgers.find((item) => item.key === selectedLedgerId.value) || deviceLedgers[0])

const handleLedgerSelect = (node: LedgerNode) => {
  if (node.deviceKey) selectedLedgerId.value = node.deviceKey
}

const devicePieRef = ref<HTMLElement | null>(null)
const onlinePieRef = ref<HTMLElement | null>(null)
const alarmPieRef = ref<HTMLElement | null>(null)
const inspectBarRef = ref<HTMLElement | null>(null)
let devicePie: any = null
let onlinePie: any = null
let alarmPie: any = null
let inspectBar: any = null

const renderCharts = () => {
  if (devicePieRef.value) {
    devicePie ??= echarts.init(devicePieRef.value)
    devicePie.setOption({
      tooltip: { trigger: 'item' },
      legend: { right: 0, top: 'center', orient: 'vertical', textStyle: { color: '#dbe7ff' } },
      series: [
        {
          type: 'pie',
          radius: ['52%', '74%'],
          center: ['35%', '46%'],
          data: [
            { value: 209, name: '在用', itemStyle: { color: '#7087e6' } },
            { value: 0, name: '备用', itemStyle: { color: '#92d166' } },
            { value: 0, name: '报废', itemStyle: { color: '#f5c45d' } },
            { value: 0, name: '维修', itemStyle: { color: '#f36d6d' } },
          ],
          label: { color: '#dbe7ff' },
        },
      ],
    })
  }

  if (onlinePieRef.value) {
    onlinePie ??= echarts.init(onlinePieRef.value)
    onlinePie.setOption({
      series: [
        {
          type: 'pie',
          radius: ['58%', '78%'],
          center: ['50%', '50%'],
          data: [
            { value: 355, name: '忽略', itemStyle: { color: '#45cbff' } },
            { value: 18, name: '关注', itemStyle: { color: '#6883d2' } },
          ],
          label: {
            color: '#eaf6ff',
            formatter: '{c}\n{name}',
            fontSize: 14,
          },
        },
      ],
    })
  }

  if (alarmPieRef.value) {
    alarmPie ??= echarts.init(alarmPieRef.value)
    alarmPie.setOption({
      series: [
        {
          type: 'pie',
          radius: ['58%', '78%'],
          center: ['50%', '50%'],
          data: [
            { value: 2, name: '一般', itemStyle: { color: '#45cbff' } },
            { value: 1, name: '严重', itemStyle: { color: '#6883d2' } },
          ],
          label: {
            color: '#eaf6ff',
            formatter: '{c}\n{name}',
            fontSize: 14,
          },
        },
      ],
    })
  }

  if (inspectBarRef.value) {
    inspectBar ??= echarts.init(inspectBarRef.value)
    inspectBar.setOption({
      grid: { top: 24, left: 48, right: 20, bottom: 70 },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['101带式输送机', '100扫地泵', '102除铁器', '311矸石脱介筛', '103刮板机', '503块精煤皮带', '506刮板机'],
        axisLabel: { color: '#dbe7ff', rotate: 90 },
        axisLine: { lineStyle: { color: '#37516d' } },
      },
      yAxis: {
        type: 'value',
        axisLabel: { color: '#dbe7ff' },
        splitLine: { lineStyle: { color: 'rgba(255,255,255,0.08)' } },
      },
      series: [
        {
          type: 'bar',
          barWidth: 18,
          data: [1, 1, 2, 8, 3, 5, 5],
          itemStyle: { color: '#35c1ff', borderRadius: [4, 4, 0, 0] },
        },
      ],
    })
  }
}

watch(showLedger, async (value) => {
  if (value) await nextTick()
})

onMounted(() => {
  renderCharts()
  window.addEventListener('resize', renderCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', renderCharts)
  devicePie?.dispose()
  onlinePie?.dispose()
  alarmPie?.dispose()
  inspectBar?.dispose()
})
</script>

<style scoped>
.equipment-screen {
  min-height: 100vh;
  padding: 24px 0 32px;
  background:
    radial-gradient(circle at center, rgba(32, 88, 144, 0.18), transparent 26%),
    radial-gradient(circle at top, rgba(39, 182, 255, 0.1), transparent 20%),
    #040914;
  color: #eaf6ff;
}

.screen-header,
.screen-grid {
  width: min(100%, 1920px);
  margin: 0 auto;
}

.screen-header {
  display: grid;
  grid-template-columns: 1fr 1.4fr 1fr;
  align-items: center;
  margin-bottom: 18px;
}

.header-side {
  display: flex;
  gap: 16px;
  align-items: center;
  font-size: 16px;
  color: rgba(234, 246, 255, 0.88);
}

.header-side strong {
  font-size: 28px;
}

.header-side--right {
  justify-content: flex-end;
}

.header-title {
  text-align: center;
}

.header-title h1 {
  margin: 0;
  font-size: 54px;
  letter-spacing: 0.08em;
  color: #14dfff;
  text-shadow: 0 0 24px rgba(20, 223, 255, 0.35);
}

.screen-grid {
  display: grid;
  grid-template-columns: 0.72fr 1.44fr 0.72fr;
  grid-template-rows: 458px 460px;
  gap: 10px;
}

.panel {
  position: relative;
  border: 1px solid rgba(40, 184, 255, 0.58);
  background:
    linear-gradient(180deg, rgba(8, 18, 41, 0.94), rgba(3, 8, 20, 0.92)),
    rgba(4, 9, 20, 0.94);
  box-shadow: inset 0 0 28px rgba(0, 180, 255, 0.08);
  overflow: hidden;
}

.panel::before,
.panel::after {
  content: '';
  position: absolute;
  width: 70px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #31e7ff);
  top: 10px;
}

.panel::before {
  left: 12px;
}

.panel::after {
  right: 12px;
}

.panel-title {
  padding: 16px 18px 8px;
  color: #12e3ff;
  font-size: 18px;
  font-weight: 700;
}

.panel-title--action {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-btn,
.ledger-tab {
  height: 34px;
  padding: 0 14px;
  border: 1px solid rgba(74, 215, 255, 0.34);
  background: rgba(8, 28, 50, 0.88);
  color: #8fe9ff;
  cursor: pointer;
}

.mini-table {
  width: calc(100% - 28px);
  margin: 0 14px 14px;
  border-collapse: collapse;
}

.mini-table th,
.mini-table td {
  padding: 16px 12px;
  text-align: left;
}

.mini-table thead th {
  background: rgba(112, 138, 230, 0.9);
  color: #fff;
  font-size: 14px;
}

.mini-table tbody tr:nth-child(odd) {
  background: rgba(17, 37, 83, 0.88);
}

.mini-table tbody tr:nth-child(even) {
  background: rgba(10, 27, 63, 0.88);
}

.panel--main {
  padding-bottom: 8px;
}

.equipment-main {
  display: grid;
  grid-template-columns: 0.95fr 1.05fr;
  gap: 10px;
  padding: 4px 18px 0;
  align-items: center;
}

.chart {
  min-height: 240px;
}

.donut {
  height: 290px;
}

.monitor-block {
  display: grid;
  gap: 20px;
}

.monitor-title {
  color: #19deff;
  font-size: 18px;
  font-weight: 700;
}

.monitor-progress {
  display: grid;
  grid-template-columns: 70px 1fr;
  align-items: center;
  gap: 16px;
  padding: 18px;
  border: 6px solid #1ad9ff;
  border-radius: 16px;
  min-height: 130px;
}

.monitor-progress strong {
  font-size: 54px;
  text-align: center;
}

.monitor-bars {
  display: grid;
  grid-template-columns: repeat(9, 1fr);
  gap: 4px;
  height: 84px;
}

.monitor-bars span {
  background: linear-gradient(180deg, #26e9ff, #1ec1d8);
}

.equipment-kpis {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
}

.kpi span {
  display: block;
  color: #49ecff;
  font-size: 15px;
}

.kpi strong {
  display: block;
  margin-top: 10px;
  font-size: 32px;
}

.kpi .gold {
  color: #ffd15d;
}

.kpi .orange {
  color: #ff8f3d;
}

.kpi .cyan {
  color: #3cecff;
}

.kpi .red {
  color: #ff2e2e;
}

.rank-list {
  display: grid;
  gap: 26px;
  padding: 16px 12px 14px;
}

.rank-top {
  display: grid;
  grid-template-columns: 70px 1fr 24px;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.rank-top span {
  color: #246dff;
  font-size: 18px;
}

.rank-top strong,
.rank-top b {
  font-size: 18px;
}

.rank-bar {
  height: 8px;
  background: rgba(19, 54, 116, 0.8);
}

.rank-bar div {
  height: 100%;
  background: linear-gradient(90deg, #1264ff, #34a2ff);
}

.panel--wide {
  grid-column: 2 / 4;
}

.panel-split {
  display: grid;
  grid-template-columns: 1fr 1fr 0.5fr 1.2fr;
  height: 100%;
}

.split-block {
  border-left: 1px solid rgba(52, 177, 255, 0.18);
  padding: 0 10px;
}

.split-block:first-child {
  border-left: 0;
}

.runtime-block {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.runtime-number {
  margin-top: 110px;
  width: 120px;
  height: 238px;
  border: 4px solid #2ce6ff;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #2ce6ff;
  background: rgba(31, 217, 255, 0.16);
}

.blue-dialog :deep(.el-dialog) {
  background: rgba(6, 20, 52, 0.96);
  border: 3px solid rgba(60, 243, 255, 0.8);
  box-shadow: 0 0 30px rgba(60, 243, 255, 0.18);
}

.blue-dialog :deep(.el-dialog__title),
.blue-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #fff;
}

.ledger-layout {
  display: grid;
  grid-template-columns: 430px 1fr;
  gap: 16px;
  min-height: 620px;
}

.ledger-tree,
.ledger-detail {
  border: 1px solid rgba(93, 177, 255, 0.32);
  background: rgba(10, 30, 70, 0.88);
}

.ledger-tree {
  padding: 16px;
}

.ledger-toolbar {
  display: grid;
  grid-template-columns: 140px 1fr;
  gap: 10px;
  margin-bottom: 16px;
}

.ledger-tree :deep(.el-tree) {
  background: transparent;
  color: #d9efff;
}

.ledger-tree :deep(.el-tree-node__content) {
  height: 34px;
}

.ledger-tree :deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
  background: rgba(90, 144, 221, 0.65);
  color: #fff;
}

.ledger-detail {
  padding: 0 0 18px;
}

.ledger-tabs {
  display: flex;
  border-bottom: 1px solid rgba(93, 177, 255, 0.32);
}

.ledger-tab {
  border-top: 0;
  border-left: 0;
  border-bottom: 0;
  border-right: 1px solid rgba(93, 177, 255, 0.18);
  background: transparent;
  color: rgba(217, 239, 255, 0.72);
}

.ledger-tab.active {
  color: #51d9ff;
  background: rgba(38, 79, 154, 0.28);
}

.ledger-card {
  padding: 18px;
}

.device-hero {
  display: grid;
  grid-template-columns: 180px 1fr;
  gap: 18px;
  margin-bottom: 18px;
}

.device-image {
  width: 160px;
  height: 120px;
  border: 1px solid rgba(95, 180, 255, 0.3);
  background: linear-gradient(135deg, rgba(71, 139, 255, 0.45), rgba(14, 33, 76, 0.92));
}

.device-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  border-top: 1px solid rgba(95, 180, 255, 0.24);
  border-left: 1px solid rgba(95, 180, 255, 0.24);
}

.meta-row,
.detail-cell {
  display: grid;
  gap: 8px;
  padding: 14px 16px;
  border-right: 1px solid rgba(95, 180, 255, 0.24);
  border-bottom: 1px solid rgba(95, 180, 255, 0.24);
}

.meta-row span,
.detail-cell span {
  color: rgba(217, 239, 255, 0.72);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
}

.detail-table {
  width: 100%;
  border-collapse: collapse;
}

.detail-table th,
.detail-table td {
  padding: 14px 12px;
  border: 1px solid rgba(95, 180, 255, 0.24);
}

@media (max-width: 1500px) {
  .screen-grid {
    grid-template-columns: 1fr;
    grid-template-rows: auto;
  }

  .panel--wide {
    grid-column: auto;
  }

  .panel-split,
  .equipment-main,
  .equipment-kpis,
  .ledger-layout,
  .device-hero,
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
