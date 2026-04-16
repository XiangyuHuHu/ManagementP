<template>
  <div class="data-page">
    <CoalQuickBar
      title="数据治理中心"
      subtitle="围绕元数据、质量规则和数据模型做统一治理，页面先完整落地，接口后补。"
    />

    <section class="stat-row">
      <article v-for="item in stats" :key="item.label" class="stat-card">
        <strong>{{ item.value }}</strong>
        <span>{{ item.label }}</span>
      </article>
    </section>

    <section class="grid-two">
      <article class="panel">
        <div class="panel-head">
          <h2>元数据目录</h2>
          <p>统一维护数据实体、来源系统和更新频率。</p>
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>数据实体</th>
                <th>类型</th>
                <th>来源系统</th>
                <th>更新频率</th>
                <th>质量评分</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="row in metadataRows" :key="row.entity">
                <td>{{ row.entity }}</td>
                <td>{{ row.type }}</td>
                <td>{{ row.source }}</td>
                <td>{{ row.frequency }}</td>
                <td>
                  <div class="score-row">
                    <div class="progress-bar">
                      <div class="progress-fill" :style="{ width: `${row.score}%`, background: getColor(row.score) }"></div>
                    </div>
                    <span>{{ row.score }}%</span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </article>

      <article class="panel">
        <div class="panel-head">
          <h2>质量规则监控</h2>
          <p>对完整性、准确性和时效性做规则约束。</p>
        </div>
        <div class="rule-list">
          <article v-for="item in qualityRules" :key="item.name" class="rule-card">
            <div class="rule-top">
              <strong>{{ item.name }}</strong>
              <span :class="['badge', item.level]">{{ item.status }}</span>
            </div>
            <p>{{ item.rule }}</p>
            <div class="rule-metrics">
              <span>完整性 {{ item.completeness }}%</span>
              <span>准确性 {{ item.accuracy }}%</span>
              <span>时效性 {{ item.timeliness }}%</span>
            </div>
          </article>
        </div>
      </article>
    </section>

    <section class="panel">
      <div class="panel-head">
        <h2>数据模型管理</h2>
        <p>按业务域沉淀模型，保证后续页面和接口命名一致。</p>
      </div>
      <div class="model-grid">
        <article v-for="item in dataModels" :key="item.name" class="model-card">
          <strong>{{ item.name }}</strong>
          <p>{{ item.desc }}</p>
          <div class="model-meta">
            <span>{{ item.tables }} 张表</span>
            <span>{{ item.fields }} 个字段</span>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import CoalQuickBar from '../../components/coal/CoalQuickBar.vue'

const stats = [
  { label: '数据实体', value: '86' },
  { label: '字段总数', value: '1284' },
  { label: '质量达标率', value: '94.2%' },
  { label: '治理规则', value: '12' },
]

const metadataRows = [
  { entity: '设备运行数据', type: '时序数据', source: 'IOT 采集系统', frequency: '实时 1 秒', score: 96 },
  { entity: '生产计划数据', type: '结构化', source: 'MES 系统', frequency: '每日', score: 92 },
  { entity: '煤质化验数据', type: '结构化', source: '化验室系统', frequency: '每班', score: 88 },
  { entity: '能耗数据', type: '时序数据', source: '能源监测系统', frequency: '实时 5 秒', score: 94 },
  { entity: '调度记录', type: '文档', source: '调度系统', frequency: '实时', score: 85 },
]

const qualityRules = [
  { name: '设备在线完整性', rule: '在线设备每分钟至少上报 1 次数据。', status: '通过', level: 'good', completeness: 98, accuracy: 96, timeliness: 99 },
  { name: '煤质数据准确性', rule: '灰分、水分、硫分偏差不超过标准范围。', status: '通过', level: 'good', completeness: 95, accuracy: 94, timeliness: 92 },
  { name: '能源数据时效性', rule: '电力数据延迟不超过 30 秒。', status: '关注', level: 'warn', completeness: 96, accuracy: 93, timeliness: 87 },
  { name: '视频分析在线率', rule: '关键区域摄像头在线率大于 95%。', status: '告警', level: 'bad', completeness: 82, accuracy: 90, timeliness: 76 },
]

const dataModels = [
  { name: '生产域模型', desc: '覆盖计划、班组、产量和工艺参数等核心生产数据。', tables: 12, fields: 186 },
  { name: '设备域模型', desc: '覆盖设备台账、运行参数、点检与维保记录。', tables: 8, fields: 142 },
  { name: '质量域模型', desc: '覆盖煤质化验、质量追踪和达标分析。', tables: 6, fields: 98 },
  { name: '能源域模型', desc: '覆盖电、水、药剂、介质与燃油消耗。', tables: 10, fields: 164 },
  { name: '安全域模型', desc: '覆盖巡检、告警、视频分析和人员事件。', tables: 7, fields: 112 },
  { name: '经营域模型', desc: '覆盖储运、成本、报表和决策分析。', tables: 9, fields: 138 },
]

const getColor = (score: number) => {
  if (score >= 90) return '#21d4a4'
  if (score >= 80) return '#ffc857'
  return '#ff7b72'
}
</script>

<style scoped>
.data-page {
  min-height: 100vh;
  padding: 0 20px 24px;
  background: #091019;
  color: #eef6ff;
}

.stat-row,
.grid-two,
.panel {
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

.grid-two {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 16px;
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

.score-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar {
  width: 100px;
  height: 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: inherit;
}

.rule-list {
  display: grid;
  gap: 12px;
}

.rule-card,
.model-card {
  padding: 18px;
  border: 1px solid rgba(96, 183, 255, 0.1);
  border-radius: 14px;
  background: rgba(14, 27, 39, 0.55);
}

.rule-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.rule-card p {
  margin: 10px 0;
  color: rgba(227, 239, 250, 0.66);
  line-height: 1.7;
}

.rule-metrics,
.model-meta {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
  color: #7ecfff;
  font-size: 13px;
}

.model-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.model-card p {
  margin: 10px 0 14px;
  color: rgba(227, 239, 250, 0.66);
  line-height: 1.7;
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

@media (max-width: 1100px) {
  .grid-two,
  .model-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .stat-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
