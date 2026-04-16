<template>
  <div class="coal-page-v2 decision-page">
    <div class="decision-body">
      <main class="main-area">
        <section class="hero-strip">
          <div>
            <p class="eyebrow">智能决策</p>
            <h1>智能决策中心</h1>
            <p class="hero-desc">集成预测分析、工艺评估、密度优化和闭环跟踪的统一决策平台。</p>
          </div>
          <div class="hero-meta">
            <span class="meta-pill"><strong>待研判</strong> 12 项</span>
            <span class="meta-pill"><strong>今日建议</strong> 8 条</span>
            <span class="meta-pill"><strong>闭环率</strong> 83%</span>
          </div>
        </section>

        <nav class="tab-bar">
          <button
            v-for="item in sideNav"
            :key="item.key"
            type="button"
            :class="['tab-item', { active: activeModule === item.key }]"
            @click="activeModule = item.key"
          >
            <component :is="item.icon" style="width:16px;height:16px" />
            <span>{{ item.label }}</span>
          </button>
        </nav>

        <!-- 生产情况分析 -->
        <template v-if="activeModule === 'production'">
          <div class="grid-2">
            <div class="card">
              <div class="card-head">
                <h3>原煤性质分析</h3>
                <button class="btn-primary" :disabled="analyzing" @click="analyzeRawCoal">
                  {{ analyzing ? '分析中...' : '智能分析' }}
                </button>
              </div>
              <div v-if="rawCoalAnalysis" class="alert" :class="rawCoalAnalysis.type">
                <strong>{{ rawCoalAnalysis.title }}</strong>
                <p>{{ rawCoalAnalysis.content }}</p>
              </div>
              <div ref="rawCoalChart" class="chart-box"></div>
            </div>

            <div class="card">
              <div class="card-head"><h3>智能建议</h3></div>
              <div v-for="item in suggestions" :key="item.title" class="suggestion-item">
                <strong>{{ item.title }}</strong>
                <p>{{ item.description }}</p>
              </div>
            </div>
          </div>

          <div class="card">
            <div class="card-head">
              <h3>洗选产品分析</h3>
              <button class="btn-primary" :disabled="analyzing" @click="analyzeProduct">刷新分析</button>
            </div>
            <div v-if="productAnalysis" class="alert" :class="productAnalysis.type">
              <strong>{{ productAnalysis.title }}</strong>
              <p>{{ productAnalysis.content }}</p>
            </div>
            <div class="table-wrap">
              <table>
                <thead>
                  <tr><th>产品名称</th><th>灰分(%)</th><th>产率(%)</th><th>质量等级</th></tr>
                </thead>
                <tbody>
                  <tr v-for="row in productData" :key="row.productName">
                    <td>{{ row.productName }}</td>
                    <td>{{ row.ashContent }}</td>
                    <td>{{ row.yield }}</td>
                    <td><span class="badge" :class="row.quality === '优' ? 'good' : row.quality === '良' ? 'warn' : 'bad'">{{ row.quality }}</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </template>

        <!-- 工艺效果评估 -->
        <template v-if="activeModule === 'process'">
          <div class="card">
            <div class="card-head"><h3>设备工艺评估</h3></div>
            <div class="equipment-grid">
              <div v-for="equipment in equipmentList" :key="equipment.id" class="equipment-card">
                <div class="equipment-header">
                  <strong>{{ equipment.name }}</strong>
                  <span class="badge" :class="equipment.grade === '优秀' ? 'good' : equipment.grade === '良好' ? 'warn' : 'bad'">{{ equipment.grade }}</span>
                </div>
                <div v-for="metric in equipment.metrics" :key="metric.name" class="metric-row">
                  <span class="metric-label">{{ metric.name }}</span>
                  <span class="metric-value">{{ metric.value }}</span>
                  <div class="progress-bar">
                    <div class="progress-fill" :style="{ width: metric.score + '%', background: getProgressColor(metric.score) }"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="card">
            <div class="card-head">
              <h3>评估报告</h3>
              <button class="btn-primary" @click="generateFullReport">生成报告</button>
            </div>
            <div class="table-wrap">
              <table>
                <thead>
                  <tr><th>设备名称</th><th>效率(%)</th><th>等级</th><th>改进建议</th></tr>
                </thead>
                <tbody>
                  <tr v-for="row in evaluationReport" :key="row.equipment">
                    <td>{{ row.equipment }}</td>
                    <td>{{ row.efficiency }}</td>
                    <td><span class="badge" :class="row.grade === '优秀' ? 'good' : row.grade === '良好' ? 'warn' : 'bad'">{{ row.grade }}</span></td>
                    <td>{{ row.suggestion }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </template>

        <!-- 指标预测 -->
        <template v-if="activeModule === 'prediction'">
          <div class="card">
            <div class="card-head">
              <h3>重介密度预测</h3>
              <button class="btn-primary" :disabled="predicting" @click="runPrediction">
                {{ predicting ? '预测中...' : '运行预测' }}
              </button>
            </div>
            <div class="prediction-form">
              <div class="form-field">
                <label>当前密度</label>
                <input type="number" v-model.number="predictionForm.density" step="0.001" />
              </div>
              <div class="form-field">
                <label>目标灰分</label>
                <input type="number" v-model.number="predictionForm.targetAsh" step="0.1" />
              </div>
              <div class="form-field">
                <label>煤质类型</label>
                <select v-model="predictionForm.coalType">
                  <option value="A">原煤A</option>
                  <option value="B">原煤B</option>
                  <option value="C">原煤C</option>
                </select>
              </div>
            </div>
            <div v-if="predictionResult" class="alert success prediction-result">
              <strong>{{ predictionResult.title }}</strong>
              <p>{{ predictionResult.content }}</p>
              <div class="result-pills">
                <span class="meta-pill"><strong>推荐密度</strong> {{ predictionResult.recommendedDensity }}</span>
                <span class="meta-pill"><strong>预测灰分</strong> {{ predictionResult.predictedAsh }}</span>
                <span class="meta-pill"><strong>置信度</strong> {{ predictionResult.confidence }}</span>
              </div>
            </div>
            <div ref="predictionChart" class="chart-box"></div>
          </div>

          <div class="grid-2">
            <div class="card">
              <div class="card-head"><h3>模型状态</h3></div>
              <div v-for="model in modelStatus" :key="model.name" class="suggestion-item">
                <strong>{{ model.name }}</strong>
                <p>{{ model.description }}</p>
              </div>
            </div>

            <div class="card">
              <div class="card-head"><h3>决策任务台账</h3></div>
              <div class="table-wrap">
                <table>
                  <thead>
                    <tr><th>主题</th><th>触发来源</th><th>责任岗位</th><th>状态</th></tr>
                  </thead>
                  <tbody>
                    <tr v-for="row in taskRows" :key="row.topic">
                      <td>{{ row.topic }}</td>
                      <td>{{ row.source }}</td>
                      <td>{{ row.owner }}</td>
                      <td><span class="badge" :class="row.status === '已派发' ? 'good' : row.status === '执行中' ? 'warn' : 'bad'">{{ row.status }}</span></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </template>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { DataAnalysis, Filter, TrendCharts } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { echarts } from '../../utils/echarts'

const activeModule = ref('production')
const analyzing = ref(false)
const predicting = ref(false)
const rawCoalChart = ref<HTMLElement | null>(null)
const predictionChart = ref<HTMLElement | null>(null)
let rawChart: any = null
let predictChart: any = null

const sideNav = [
  { key: 'production', label: '生产分析', icon: Filter },
  { key: 'process', label: '工艺评估', icon: DataAnalysis },
  { key: 'prediction', label: '指标预测', icon: TrendCharts },
]

const rawCoalAnalysis = ref<{ title: string; type: string; content: string } | null>(null)
const productAnalysis = ref<{ title: string; type: string; content: string } | null>(null)

const productData = ref([
  { productName: '精煤', ashContent: 5.8, yield: 45.2, quality: '优' },
  { productName: '中煤', ashContent: 25.6, yield: 28.5, quality: '良' },
  { productName: '煤泥', ashContent: 42.3, yield: 15.8, quality: '一般' },
])

const suggestions = ref([
  { title: '密度调整建议', description: '建议将悬浮液密度调整至 1.42-1.44 范围，可进一步提升精煤产率。' },
  { title: '配煤优化', description: '当前配煤合格率 98.5%，建议保持当前比例，关注入料灰分波动。' },
  { title: '设备维护', description: '振动筛累计运行时长接近检修周期，建议安排下一班次预防性检修。' },
])

const equipmentList = ref([
  { id: 1, name: '重介分选机', grade: '优秀', metrics: [{ name: '分选效率', value: '95.2%', score: 95 }, { name: '可能偏差', value: '0.04', score: 92 }] },
  { id: 2, name: '振动筛', grade: '良好', metrics: [{ name: '筛分效率', value: '88.5%', score: 88 }, { name: '处理能力', value: '320t/h', score: 85 }] },
  { id: 3, name: '磁选机', grade: '优秀', metrics: [{ name: '磁选效率', value: '99.8%', score: 99 }, { name: '回收率', value: '99.9%', score: 99 }] },
  { id: 4, name: '浓缩机', grade: '一般', metrics: [{ name: '浓缩效率', value: '75.2%', score: 75 }, { name: '底流浓度', value: '45%', score: 70 }] },
])

const evaluationReport = ref([
  { equipment: '重介分选机', efficiency: 95.2, grade: '优秀', suggestion: '继续保持，定期检查介质回收系统。' },
  { equipment: '振动筛', efficiency: 88.5, grade: '良好', suggestion: '建议检查筛网磨损情况。' },
  { equipment: '磁选机', efficiency: 99.8, grade: '优秀', suggestion: '运行良好，按计划维护。' },
  { equipment: '浓缩机', efficiency: 75.2, grade: '一般', suggestion: '建议调整絮凝剂添加量。' },
])

const predictionForm = ref({ density: 1.42, targetAsh: 8.5, coalType: 'A' })
const predictionResult = ref<any>(null)

const modelStatus = ref([
  { name: '密度预测模型', description: '最近一次校准时间 2026-03-20，状态正常，偏差 < 0.3%。' },
  { name: '分选效果模型', description: '当前使用本地演示参数，适合页面联调与演示验证。' },
  { name: '能耗分析模型', description: '已预留接口，等待接入实时采集数据后自动激活。' },
])

const taskRows = ref([
  { topic: '重介密度波动优化', source: '质量偏差预警', owner: '工艺工程师', status: '待确认' },
  { topic: '主洗系统负荷重排', source: '产量预测', owner: '调度长', status: '执行中' },
  { topic: '离心机检修建议', source: '设备趋势分析', owner: '机修班', status: '已派发' },
  { topic: '药剂单耗偏高排查', source: '能耗异常', owner: '技术员', status: '待确认' },
])

const getProgressColor = (percentage: number) => {
  if (percentage >= 95) return '#18f0bf'
  if (percentage >= 80) return '#f7c76b'
  return '#ff6f73'
}

const renderRawChart = async () => {
  await nextTick()
  if (!rawCoalChart.value) return
  rawChart?.dispose()
  rawChart = echarts.init(rawCoalChart.value)
  rawChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['1号样', '2号样', '3号样', '4号样', '5号样'], axisLabel: { color: '#8ab4d6' }, axisLine: { lineStyle: { color: '#1e3a52' } } },
    yAxis: { type: 'value', axisLabel: { color: '#8ab4d6' }, splitLine: { lineStyle: { color: '#1e3a52' } } },
    series: [
      { name: '灰分', type: 'line', smooth: true, data: [28.2, 29.1, 27.8, 28.6, 27.5], itemStyle: { color: '#57d8ff' }, areaStyle: { color: 'rgba(87,216,255,0.12)' } },
      { name: '水分', type: 'line', smooth: true, data: [4.1, 3.8, 4.3, 4.0, 3.9], itemStyle: { color: '#25d39c' } },
    ],
  })
}

const renderPredictionChart = async () => {
  await nextTick()
  if (!predictionChart.value) return
  predictChart?.dispose()
  predictChart = echarts.init(predictionChart.value)
  predictChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['1.38', '1.40', '1.42', '1.44', '1.46', '1.48'], axisLabel: { color: '#8ab4d6' }, axisLine: { lineStyle: { color: '#1e3a52' } } },
    yAxis: { type: 'value', name: '灰分(%)', axisLabel: { color: '#8ab4d6' }, splitLine: { lineStyle: { color: '#1e3a52' } } },
    series: [
      { name: '预测灰分', type: 'line', smooth: true, data: [10.5, 9.6, 8.8, 8.2, 7.9, 7.6], itemStyle: { color: '#25d39c' }, areaStyle: { color: 'rgba(37,211,156,0.12)' } },
      { name: '目标灰分', type: 'line', data: [8.5, 8.5, 8.5, 8.5, 8.5, 8.5], lineStyle: { type: 'dashed', color: '#f7c76b' }, itemStyle: { color: '#f7c76b' } },
    ],
  })
}

const analyzeRawCoal = () => {
  analyzing.value = true
  setTimeout(() => {
    rawCoalAnalysis.value = { title: '原煤波动可控', type: 'success', content: '原煤热值和灰分波动处于可控范围，当前工艺参数可以维持。' }
    analyzing.value = false
  }, 600)
}

const analyzeProduct = () => {
  analyzing.value = true
  setTimeout(() => {
    productAnalysis.value = { title: '产品结构稳定', type: 'success', content: '精煤比例稳定在45%以上，中煤和煤泥占比没有明显异常。' }
    analyzing.value = false
  }, 600)
}

const runPrediction = () => {
  predicting.value = true
  setTimeout(() => {
    predictionResult.value = {
      title: '预测完成',
      type: 'success',
      content: '根据当前输入参数，推荐适度上调重介密度以优化精煤质量。',
      recommendedDensity: '1.438',
      predictedAsh: '8.3%',
      confidence: '91%',
    }
    predicting.value = false
  }, 700)
}

const generateFullReport = () => {
  const csv = ['设备名称,效率(%),等级,改进建议', ...evaluationReport.value.map(r => `${r.equipment},${r.efficiency},${r.grade},${r.suggestion}`)].join('\n')
  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = `工艺评估报告_${new Date().toISOString().slice(0, 10)}.csv`; a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('评估报告 CSV 已下载')
}

const handleResize = () => { rawChart?.resize(); predictChart?.resize() }

watch(activeModule, async (value) => {
  await nextTick()
  if (value === 'production') renderRawChart()
  if (value === 'prediction') renderPredictionChart()
})

onMounted(() => {
  renderRawChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  rawChart?.dispose()
  predictChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.coal-page-v2{min-height:100vh;background:#091019;color:#eef6ff}
.decision-body{width:min(100%,1800px);margin:0 auto;padding:80px 24px 20px}
.main-area{min-width:0}
.tab-bar{display:flex;gap:8px;margin-bottom:16px;padding:6px;border:1px solid rgba(96,183,255,.12);border-radius:16px;background:rgba(8,19,30,.9)}
.tab-item{display:flex;align-items:center;gap:8px;padding:12px 20px;border:1px solid transparent;border-radius:12px;background:transparent;color:rgba(226,241,255,.65);cursor:pointer;transition:.2s;font-size:14px}
.tab-item:hover{color:#fff;background:rgba(42,100,160,.12)}
.tab-item.active{border-color:rgba(87,216,255,.25);background:rgba(42,100,160,.2);color:#fff}
.hero-strip{display:flex;justify-content:space-between;align-items:flex-end;gap:20px;padding:24px 28px;border:1px solid rgba(96,183,255,.12);border-radius:20px;background:rgba(8,19,30,.9);margin-bottom:16px}
.eyebrow{margin:0 0 8px;color:#7bc8ff;font-size:11px;font-weight:700;letter-spacing:.18em;text-transform:uppercase}
.hero-strip h1{margin:0;font-size:28px}
.hero-desc{margin:8px 0 0;color:rgba(227,239,250,.65);max-width:640px;line-height:1.7}
.hero-meta{display:flex;gap:10px;flex-wrap:wrap}
.meta-pill{padding:8px 14px;border-radius:12px;background:rgba(255,255,255,.06);font-size:13px}
.meta-pill strong{color:#67d8ff;margin-right:6px}
.grid-2{display:grid;grid-template-columns:1fr 1fr;gap:16px;margin-bottom:16px}
.card{padding:22px;border:1px solid rgba(96,183,255,.12);border-radius:18px;background:rgba(8,19,30,.9);margin-bottom:16px}
.card-head{display:flex;justify-content:space-between;align-items:center;margin-bottom:16px}
.card-head h3{margin:0;font-size:17px;color:#f3faff}
.btn-primary{padding:8px 18px;border:0;border-radius:10px;background:linear-gradient(135deg,#2cd1ff,#21b7ff);color:#082338;font-weight:600;cursor:pointer}
.btn-primary:disabled{opacity:.5;cursor:not-allowed}
.alert{padding:14px 18px;border-radius:12px;margin-bottom:14px}
.alert.success{background:rgba(24,240,191,.1);border:1px solid rgba(24,240,191,.25)}
.alert.warning{background:rgba(247,199,107,.1);border:1px solid rgba(247,199,107,.25)}
.alert strong{display:block;margin-bottom:4px}
.alert p{margin:0;color:rgba(227,239,250,.7);font-size:13px;line-height:1.6}
.chart-box{width:100%;height:280px}
.suggestion-item{padding:14px;border:1px solid rgba(96,183,255,.08);border-radius:12px;margin-bottom:10px}
.suggestion-item strong{color:#67d8ff;font-size:14px}
.suggestion-item p{margin:6px 0 0;color:rgba(227,239,250,.65);font-size:13px;line-height:1.6}
.table-wrap{overflow-x:auto}
.table-wrap table{width:100%;border-collapse:collapse}
.table-wrap th,.table-wrap td{padding:12px 14px;text-align:left;border-bottom:1px solid rgba(96,183,255,.08)}
.table-wrap th{color:#7bc8ff;font-size:12px;font-weight:600;text-transform:uppercase;letter-spacing:.06em}
.table-wrap td{color:rgba(227,239,250,.82);font-size:14px}
.badge{display:inline-block;padding:3px 10px;border-radius:8px;font-size:12px;font-weight:600}
.badge.good{background:rgba(24,240,191,.15);color:#18f0bf}
.badge.warn{background:rgba(247,199,107,.15);color:#f7c76b}
.badge.bad{background:rgba(255,111,115,.15);color:#ff6f73}
.equipment-grid{display:grid;grid-template-columns:repeat(2,1fr);gap:14px}
.equipment-card{padding:18px;border:1px solid rgba(96,183,255,.1);border-radius:14px;background:rgba(14,27,39,.6)}
.equipment-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:14px}
.metric-row{display:flex;align-items:center;gap:10px;margin-bottom:8px;font-size:13px}
.metric-label{width:80px;color:rgba(227,239,250,.6)}
.metric-value{width:60px;color:#f3faff;font-weight:600}
.progress-bar{flex:1;height:6px;border-radius:3px;background:rgba(255,255,255,.08)}
.progress-fill{height:100%;border-radius:3px;transition:width .4s ease}
.prediction-form{display:flex;gap:20px;margin-bottom:18px;flex-wrap:wrap}
.form-field{display:flex;flex-direction:column;gap:6px}
.form-field label{color:#7bc8ff;font-size:12px;font-weight:600;letter-spacing:.06em}
.form-field input,.form-field select{padding:10px 14px;border:1px solid rgba(96,183,255,.18);border-radius:10px;background:rgba(14,27,39,.9);color:#f3faff;font-size:14px;width:160px}
.prediction-result{margin-top:16px}
.result-pills{display:flex;gap:10px;margin-top:12px;flex-wrap:wrap}
@media(max-width:1100px){.tab-bar{flex-wrap:wrap}.grid-2,.equipment-grid{grid-template-columns:1fr}}
</style>
