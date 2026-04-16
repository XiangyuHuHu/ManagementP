<template>
  <div class="data-page">
    <CoalQuickBar
      title="数据集成中心"
      subtitle="统一展示多引擎接入、集成任务和数据流向，保证这一组页面可完整演示。"
    />

    <section class="stat-row">
      <article v-for="item in stats" :key="item.label" class="stat-card">
        <strong>{{ item.value }}</strong>
        <span>{{ item.label }}</span>
      </article>
    </section>

    <section class="panel">
      <div class="panel-head">
        <h2>集成引擎</h2>
        <p>统一管理数据库、消息总线和缓存中间件的接入状态。</p>
      </div>
      <div class="engine-grid">
        <article v-for="engine in engines" :key="engine.name" class="engine-card">
          <div class="engine-top">
            <strong>{{ engine.name }}</strong>
            <span :class="['badge', engine.level]">{{ engine.status }}</span>
          </div>
          <p>{{ engine.desc }}</p>
          <div class="engine-meta">
            <span>类型：{{ engine.type }}</span>
            <span>延迟：{{ engine.latency }}</span>
          </div>
        </article>
      </div>
    </section>

    <section class="grid-two">
      <article class="panel">
        <div class="panel-head">
          <h2>集成任务</h2>
          <p>管理清洗、去重、索引和分发等核心任务。</p>
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>任务名称</th>
                <th>类型</th>
                <th>调度</th>
                <th>状态</th>
                <th>最近执行</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="task in tasks" :key="task.name">
                <td>{{ task.name }}</td>
                <td>{{ task.type }}</td>
                <td>{{ task.schedule }}</td>
                <td><span :class="['badge', task.level]">{{ task.status }}</span></td>
                <td>{{ task.lastRun }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>

      <article class="panel">
        <div class="panel-head">
          <h2>数据流向</h2>
          <p>展示从采集到服务输出的完整路径。</p>
        </div>
        <div class="flow-list">
          <article v-for="(flow, index) in dataFlows" :key="flow.stage" class="flow-card">
            <div class="flow-step">
              <span class="flow-index">{{ index + 1 }}</span>
              <div>
                <strong>{{ flow.stage }}</strong>
                <p>{{ flow.desc }}</p>
              </div>
            </div>
            <div class="flow-tags">
              <span v-for="tag in flow.techs" :key="tag" class="badge info">{{ tag }}</span>
            </div>
          </article>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import CoalQuickBar from '../../components/coal/CoalQuickBar.vue'

const stats = [
  { label: '集成引擎', value: '6' },
  { label: '集成任务', value: '18' },
  { label: '任务成功率', value: '99.2%' },
  { label: '日处理量', value: '2.4 TB' },
]

const engines = [
  { name: 'MySQL', type: '关系型数据库', status: '已连接', level: 'good', latency: '< 5ms', desc: '承载生产、质量、设备和台账类结构化数据。' },
  { name: 'MongoDB', type: '文档数据库', status: '已连接', level: 'good', latency: '< 8ms', desc: '承载日志、巡检记录和非结构化业务数据。' },
  { name: 'InfluxDB', type: '时序数据库', status: '已连接', level: 'good', latency: '< 3ms', desc: '承载 IOT 点位和实时采集数据。' },
  { name: 'Redis', type: '缓存服务', status: '已连接', level: 'good', latency: '< 1ms', desc: '承载实时看板、热点数据和缓存结果。' },
  { name: 'Hive', type: '离线分析引擎', status: '待配置', level: 'warn', latency: '秒级', desc: '预留离线统计和历史归档分析能力。' },
  { name: 'Kafka', type: '消息总线', status: '已连接', level: 'good', latency: '< 10ms', desc: '承载实时接入和分发任务。' },
]

const tasks = [
  { name: 'IOT 实时采集任务', type: '实时', schedule: '持续运行', status: '运行中', level: 'good', lastRun: '2026-04-13 10:30' },
  { name: '设备状态 ETL', type: '实时', schedule: '每分钟', status: '运行中', level: 'good', lastRun: '2026-04-13 10:30' },
  { name: '生产日报汇总', type: '离线', schedule: '每日 02:00', status: '运行中', level: 'good', lastRun: '2026-04-13 02:00' },
  { name: '质量化验同步', type: '批量', schedule: '每班', status: '运行中', level: 'good', lastRun: '2026-04-13 08:00' },
  { name: '月度能耗归档', type: '离线', schedule: '每月 1 日', status: '暂停', level: 'warn', lastRun: '2026-04-01 03:00' },
]

const dataFlows = [
  { stage: '数据采集', desc: '从 PLC、传感器、摄像头、化验和称重系统采集原始数据。', techs: ['OPC-UA', 'MQTT', 'Modbus', 'HTTP'] },
  { stage: '数据接入', desc: '通过消息总线和适配器统一接入实时与批量数据。', techs: ['Kafka', 'Flink', 'Logstash'] },
  { stage: '数据处理', desc: '执行清洗、去重、转换、打标和索引。', techs: ['Spark', 'Flink', 'Python'] },
  { stage: '数据存储', desc: '按数据特征分层落地到不同存储引擎。', techs: ['MySQL', 'InfluxDB', 'MongoDB'] },
  { stage: '数据服务', desc: '通过接口、订阅和看板服务向业务页面提供数据。', techs: ['REST API', 'WebSocket', 'GraphQL'] },
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

.engine-grid,
.grid-two {
  display: grid;
  gap: 16px;
}

.engine-grid {
  grid-template-columns: repeat(3, 1fr);
}

.grid-two {
  grid-template-columns: 1fr 1fr;
}

.engine-card,
.flow-card {
  padding: 18px;
  border: 1px solid rgba(96, 183, 255, 0.1);
  border-radius: 14px;
  background: rgba(14, 27, 39, 0.55);
}

.engine-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.engine-card p,
.flow-step p {
  margin: 10px 0;
  color: rgba(227, 239, 250, 0.66);
  line-height: 1.7;
}

.engine-meta,
.flow-tags {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
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

.flow-list {
  display: grid;
  gap: 12px;
}

.flow-step {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.flow-index {
  width: 28px;
  height: 28px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #57d8ff, #21d4a4);
  color: #082338;
  font-weight: 700;
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

.badge.info {
  background: rgba(87, 216, 255, 0.16);
  color: #57d8ff;
}

@media (max-width: 1100px) {
  .engine-grid,
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
