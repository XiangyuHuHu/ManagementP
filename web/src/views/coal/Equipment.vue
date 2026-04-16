<template>
  <div class="business-page">
    <section class="page-header">
      <div>
        <p class="page-tag">设备管理</p>
        <h1>设备资产与维保台账</h1>
        <p class="page-desc">保留日常业务页，用于设备台账、维保记录、寿命预测和工单处理；设备大屏通过独立入口打开。</p>
      </div>
      <div class="page-actions">
        <el-button @click="router.push('/coal/monitor')">巡检监测</el-button>
        <el-button @click="router.push('/coal/mechanical')">机电设备</el-button>
        <el-button type="primary" @click="router.push('/coal/equipment-screen')">查看设备大屏</el-button>
      </div>
    </section>

    <section class="kpi-grid">
      <article v-for="item in kpis" :key="item.label" class="kpi-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.note }}</small>
      </article>
    </section>

    <section class="content-grid">
      <article class="panel">
        <div class="panel-head">
          <div>
            <h2>关键设备健康度</h2>
            <p>关注核心设备的温度、振动和负载状态。</p>
          </div>
        </div>
        <div class="device-cards">
          <div v-for="item in devices" :key="item.name" class="device-card">
            <div class="device-card-head">
              <strong>{{ item.name }}</strong>
              <span :class="['status-tag', item.level]">{{ item.status }}</span>
            </div>
            <p>{{ item.location }}</p>
            <div class="device-metrics">
              <div><span>温度</span><strong>{{ item.temp }}</strong></div>
              <div><span>振动</span><strong>{{ item.vibration }}</strong></div>
              <div><span>负载</span><strong>{{ item.load }}</strong></div>
              <div><span>健康度</span><strong>{{ item.health }}</strong></div>
            </div>
            <div class="life-row">
              <span>剩余寿命</span>
              <el-progress :percentage="item.life" :stroke-width="10" :show-text="false" :color="item.color" />
              <strong>{{ item.life }}%</strong>
            </div>
          </div>
        </div>
      </article>

      <article class="panel">
        <div class="panel-head">
          <div>
            <h2>告警与维护提醒</h2>
            <p>高风险设备、保养周期和检修建议。</p>
          </div>
        </div>
        <div class="alert-list">
          <div v-for="item in alerts" :key="item.title" class="alert-item">
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc }}</p>
            </div>
            <span :class="['alert-tag', item.level]">{{ item.time }}</span>
          </div>
        </div>
      </article>

      <article class="panel panel--wide">
        <div class="panel-head">
          <div>
            <h2>设备运转台账</h2>
            <p>用于日常查看设备运行、维保和工单信息。</p>
          </div>
          <div class="table-tools">
            <el-button @click="showLedger = true">查看设备台账</el-button>
          </div>
        </div>
        <el-table :data="ledgerRows" class="data-table">
          <el-table-column prop="device" label="设备名称" min-width="180" />
          <el-table-column prop="location" label="位置" width="160" />
          <el-table-column prop="owner" label="责任班组" width="120" />
          <el-table-column prop="runtime" label="运行时长" width="120" />
          <el-table-column prop="lastRepair" label="最近维保" width="120" />
          <el-table-column prop="nextPlan" label="下次计划" width="120" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </article>
    </section>

    <el-dialog v-model="showLedger" title="设备台账" width="86%">
      <el-table :data="ledgerRows">
        <el-table-column prop="device" label="设备名称" min-width="180" />
        <el-table-column prop="code" label="编码" width="140" />
        <el-table-column prop="location" label="位置" width="160" />
        <el-table-column prop="type" label="设备类型" width="120" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="owner" label="责任班组" width="120" />
        <el-table-column prop="note" label="说明" min-width="220" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const showLedger = ref(false)

const kpis = [
  { label: '设备总数', value: '209 台', note: '在用 209，备用 0' },
  { label: '在线监测设备', value: '71 台', note: '温度与振动实时采集' },
  { label: '待执行维保', value: '3 项', note: '含定期保养和紧急检修' },
  { label: '高风险设备', value: '1 台', note: '需优先安排处理' },
]

const devices = [
  { name: '602精煤压滤机入料泵', location: '主洗车间', status: '正常运行', level: 'good', temp: '48.2°C', vibration: '1.8 mm/s', load: '72%', health: '92 分', life: 72, color: '#45cbff' },
  { name: '311中煤卧式离心机', location: '离心机组', status: '性能预警', level: 'warn', temp: '64.5°C', vibration: '8.9 mm/s', load: '92%', health: '64 分', life: 24, color: '#ff6d6d' },
  { name: '101原煤皮带机', location: '原煤仓上', status: '正常运行', level: 'good', temp: '42.6°C', vibration: '2.4 mm/s', load: '68%', health: '88 分', life: 61, color: '#35d7a5' },
  { name: '325矸石脱介筛', location: '筛分车间', status: '保养到期', level: 'info', temp: '53.1°C', vibration: '4.1 mm/s', load: '74%', health: '79 分', life: 46, color: '#ffc857' },
]

const alerts = [
  { title: '高风险：振动异常', desc: '311中煤卧式离心机振动值达到 8.9 mm/s，建议停机检查轴承。', time: '2 分钟前', level: 'danger' },
  { title: '定期维护提醒', desc: '#402离心机滤网已使用 480 小时，建议 20 小时内更换。', time: '1 小时前', level: 'warn' },
  { title: 'AI 诊断建议', desc: '絮凝剂泵 P-203 电流波动异常，建议排查管路结垢。', time: '4 小时前', level: 'info' },
]

const ledgerRows = [
  { device: '602精煤压滤机入料泵', code: 'EQ-602-001', location: '主洗车间', type: '离心泵', owner: '机电车间', runtime: '21.6 h', lastRepair: '04-10', nextPlan: '04-22', status: '运行中', note: '温度与振动稳定' },
  { device: '311中煤卧式离心机', code: 'EQ-311-002', location: '离心机组', type: '离心机', owner: '生产一班', runtime: '18.2 h', lastRepair: '04-02', nextPlan: '04-15', status: '预警', note: '振动值偏高' },
  { device: '101原煤皮带机', code: 'EQ-101-001', location: '原煤仓上', type: '输送设备', owner: '生产二班', runtime: '22.8 h', lastRepair: '04-09', nextPlan: '04-29', status: '运行中', note: '负载平稳' },
  { device: '325矸石脱介筛', code: 'EQ-325-003', location: '筛分车间', type: '筛分设备', owner: '生产三班', runtime: '20.4 h', lastRepair: '03-28', nextPlan: '04-14', status: '待保养', note: '筛面磨损偏大' },
]
</script>

<style scoped>
.business-page {
  min-height: 100vh;
  padding: 24px 20px 28px;
  background: #091019;
  color: #eef6ff;
}

.page-header,
.kpi-grid,
.content-grid {
  width: min(100%, 1680px);
  margin: 0 auto 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 28px 30px;
  border: 1px solid rgba(96, 183, 255, 0.12);
  border-radius: 20px;
  background: rgba(8, 19, 30, 0.92);
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
.panel,
.device-card {
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
  grid-template-columns: 1.25fr 0.75fr;
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

.device-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.device-card {
  padding: 18px;
}

.device-card-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.device-card p {
  margin: 8px 0 14px;
  color: rgba(227, 239, 250, 0.62);
}

.device-metrics {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.device-metrics div,
.life-row {
  padding: 12px;
  border-radius: 12px;
  background: rgba(14, 27, 39, 0.55);
}

.device-metrics span,
.life-row span {
  display: block;
  color: rgba(227, 239, 250, 0.62);
  font-size: 13px;
}

.device-metrics strong,
.life-row strong {
  display: block;
  margin-top: 8px;
}

.life-row {
  margin-top: 12px;
}

.status-tag,
.alert-tag {
  height: fit-content;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.status-tag.good {
  background: rgba(33, 212, 164, 0.16);
  color: #21d4a4;
}

.status-tag.warn {
  background: rgba(255, 109, 109, 0.16);
  color: #ff6d6d;
}

.status-tag.info {
  background: rgba(255, 200, 87, 0.16);
  color: #ffc857;
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
  border-radius: 14px;
  background: rgba(14, 27, 39, 0.55);
}

.alert-item p {
  margin: 8px 0 0;
  color: rgba(227, 239, 250, 0.62);
  line-height: 1.6;
}

.alert-tag.danger {
  background: rgba(255, 109, 109, 0.16);
  color: #ff6d6d;
}

.alert-tag.warn {
  background: rgba(255, 200, 87, 0.16);
  color: #ffc857;
}

.alert-tag.info {
  background: rgba(87, 216, 255, 0.16);
  color: #57d8ff;
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
  .content-grid,
  .device-cards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .page-header {
    flex-direction: column;
  }

  .page-actions {
    flex-wrap: wrap;
  }
}
</style>
