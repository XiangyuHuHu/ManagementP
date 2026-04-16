<template>
  <div class="data-page">
    <CoalQuickBar
      title="数据接入管理"
      subtitle="按数据源、接口和接入拓扑组织页面，先把数据接入功能页做成完整可展示版本。"
    />

    <section class="stat-row">
      <article v-for="item in stats" :key="item.label" class="stat-card">
        <strong>{{ item.value }}</strong>
        <span>{{ item.label }}</span>
      </article>
    </section>

    <section class="panel">
      <div class="panel-head">
        <div>
          <h2>数据源概览</h2>
          <p>覆盖自动化系统、设备管理、智能化系统和第三方业务系统。</p>
        </div>
      </div>
      <div class="source-grid">
        <article v-for="source in dataSources" :key="source.name" class="source-card">
          <div class="source-top">
            <strong>{{ source.name }}</strong>
            <span :class="['badge', source.level]">{{ source.status }}</span>
          </div>
          <p>{{ source.desc }}</p>
          <div class="source-meta">
            <span>数据量：{{ source.volume }}</span>
            <span>频率：{{ source.frequency }}</span>
            <span>协议：{{ source.protocol }}</span>
          </div>
        </article>
      </div>
    </section>

    <section class="grid-two">
      <article class="panel">
        <div class="panel-head">
          <div>
            <h2>接口清单</h2>
            <p>统一管理第三方接入接口与传输方式。</p>
          </div>
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>接口名称</th>
                <th>协议</th>
                <th>方向</th>
                <th>频率</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="api in apiList" :key="api.name">
                <td>{{ api.name }}</td>
                <td>{{ api.protocol }}</td>
                <td>{{ api.direction }}</td>
                <td>{{ api.frequency }}</td>
                <td><span :class="['badge', api.level]">{{ api.status }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>

      <article class="panel">
        <div class="panel-head">
          <div>
            <h2>接入拓扑</h2>
            <p>展示从现场终端到平台应用的接入路径。</p>
          </div>
        </div>
        <div class="topology">
          <div v-for="(layer, index) in topology" :key="layer.name" class="topology-layer">
            <div class="topology-title">{{ layer.name }}</div>
            <div class="topology-nodes">
              <span v-for="node in layer.nodes" :key="node" class="topology-node">{{ node }}</span>
            </div>
            <div v-if="index < topology.length - 1" class="topology-arrow">↓</div>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import CoalQuickBar from '../../components/coal/CoalQuickBar.vue'

const stats = [
  { label: '数据源数量', value: '5' },
  { label: '接入点位', value: '426' },
  { label: '接入成功率', value: '98.6%' },
  { label: '标准接口', value: '24' },
]

const dataSources = [
  { name: '自动化系统数据', status: '已接入', level: 'good', desc: 'PLC、DCS 和现场控制层实时数据。', volume: '~120 万条/日', frequency: '实时 1 秒', protocol: 'OPC-UA' },
  { name: '生产过程数据', status: '已接入', level: 'good', desc: '重介、浮选、脱水等关键工艺参数。', volume: '~80 万条/日', frequency: '实时 5 秒', protocol: 'Modbus' },
  { name: '设备维保数据', status: '已接入', level: 'good', desc: '设备台账、点检、工单和备件库存。', volume: '~5000 条/日', frequency: '事件触发', protocol: 'REST API' },
  { name: '智能化系统数据', status: '对接中', level: 'warn', desc: 'AI 视频分析、人员定位和巡检事件。', volume: '~30 万条/日', frequency: '实时 10 秒', protocol: 'MQTT' },
  { name: '第三方业务系统', status: '已接入', level: 'good', desc: 'ERP、LIMS、称重和财务相关数据。', volume: '~2000 条/日', frequency: '定时同步', protocol: 'HTTP/SOAP' },
]

const apiList = [
  { name: 'IOT 实时数据接口', protocol: 'WebSocket', direction: '入站', frequency: '实时', status: '正常', level: 'good' },
  { name: '设备台账同步接口', protocol: 'REST', direction: '双向', frequency: '每小时', status: '正常', level: 'good' },
  { name: 'ERP 生产计划接口', protocol: 'SOAP', direction: '入站', frequency: '每日', status: '正常', level: 'good' },
  { name: '称重系统数据接口', protocol: 'TCP', direction: '入站', frequency: '实时', status: '正常', level: 'good' },
  { name: 'AI 视频分析接口', protocol: 'MQTT', direction: '入站', frequency: '实时', status: '告警', level: 'bad' },
]

const topology = [
  { name: '现场层', nodes: ['PLC', '传感器', '摄像头', '称重仪', '化验设备'] },
  { name: '采集层', nodes: ['OPC 网关', 'MQTT Broker', '协议适配器'] },
  { name: '传输层', nodes: ['工业以太网', '消息总线', '缓存服务'] },
  { name: '平台层', nodes: ['数据清洗', '规则引擎', '实时计算', '数据分发'] },
  { name: '应用层', nodes: ['大屏总览', '设备管理', '生产管理', '报表分析'] },
]
</script>

<style scoped>
.data-page {
  min-height: 100vh;
  padding: 0 20px 24px;
  background: #091019;
  color: #eef6ff;
}

.stat-row,
.panel,
.grid-two {
  width: min(100%, 1680px);
  margin: 0 auto 16px;
}

.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card,
.panel {
  border: 1px solid rgba(96, 183, 255, 0.12);
  border-radius: 18px;
  background: rgba(8, 19, 30, 0.92);
}

.stat-card {
  padding: 20px;
}

.stat-card strong {
  display: block;
  font-size: 32px;
  color: #57d8ff;
}

.stat-card span {
  display: block;
  margin-top: 8px;
  color: rgba(227, 239, 250, 0.66);
}

.panel {
  padding: 24px;
}

.panel-head {
  margin-bottom: 18px;
}

.panel-head h2 {
  margin: 0;
}

.panel-head p {
  margin: 10px 0 0;
  color: rgba(227, 239, 250, 0.66);
}

.source-grid,
.grid-two {
  display: grid;
  gap: 16px;
}

.source-grid {
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.grid-two {
  grid-template-columns: 1fr 1fr;
}

.source-card {
  padding: 18px;
  border: 1px solid rgba(96, 183, 255, 0.1);
  border-radius: 14px;
  background: rgba(14, 27, 39, 0.55);
}

.source-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.source-card p {
  margin: 12px 0;
  color: rgba(227, 239, 250, 0.66);
  line-height: 1.7;
}

.source-meta {
  display: grid;
  gap: 6px;
  color: #7ecfff;
  font-size: 13px;
}

.table-wrap {
  overflow-x: auto;
}

.table-wrap table {
  width: 100%;
  border-collapse: collapse;
}

.table-wrap th,
.table-wrap td {
  padding: 14px 12px;
  text-align: left;
  border-bottom: 1px solid rgba(96, 183, 255, 0.08);
}

.table-wrap th {
  color: #7ecfff;
  font-size: 13px;
}

.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.badge.good {
  background: rgba(33, 212, 164, 0.16);
  color: #21d4a4;
}

.badge.warn {
  background: rgba(255, 200, 87, 0.16);
  color: #ffc857;
}

.badge.bad {
  background: rgba(255, 123, 114, 0.16);
  color: #ff7b72;
}

.topology {
  display: grid;
  gap: 8px;
}

.topology-layer {
  text-align: center;
}

.topology-title {
  margin-bottom: 10px;
  color: #7ecfff;
  font-size: 13px;
}

.topology-nodes {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.topology-node {
  padding: 8px 12px;
  border: 1px solid rgba(96, 183, 255, 0.12);
  border-radius: 12px;
  background: rgba(14, 27, 39, 0.55);
}

.topology-arrow {
  margin-top: 8px;
  color: rgba(126, 207, 255, 0.66);
  font-size: 20px;
}

@media (max-width: 1100px) {
  .grid-two {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .stat-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
