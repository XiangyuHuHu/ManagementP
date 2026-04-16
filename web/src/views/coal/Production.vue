<template>
  <div class="coal-page-v2 production-page">
    <div class="page-layout">
      <main class="page-main">
        <section class="hero">
          <div>
            <p class="eyebrow">生产工艺</p>
            <h1>实时工艺流程图</h1>
            <p class="hero-text">集中展示原煤入厂、破碎、洗选、回收和产品仓的流程状态，方便班组和值班长快速定位异常环节。</p>
          </div>
        </section>

        <section class="main-grid">
          <article class="panel flow-panel">
            <div class="flow-head">
              <div class="legend">
                <span><i class="green"></i>运行</span>
                <span><i class="gray"></i>空闲</span>
                <span><i class="red"></i>报警</span>
              </div>
            </div>

            <div class="flow-map">
              <div class="connector"></div>
              <div v-for="node in nodes" :key="node.name" class="flow-node" :class="node.state">
                <div class="node-icon"><el-icon><component :is="node.icon" /></el-icon></div>
                <strong>{{ node.name }}</strong>
                <span>{{ node.meta }}</span>
                <small>{{ node.status }}</small>
              </div>
            </div>

            <div class="flow-metrics">
              <article class="metric blue"><span>总处理量</span><strong>1,482.5 吨/日</strong></article>
              <article class="metric green"><span>效率指数</span><strong>94.2%</strong></article>
              <article class="metric yellow"><span>当前报警数</span><strong>03</strong></article>
              <article class="metric"><span>班组计时</span><strong>06:22:45</strong></article>
            </div>
          </article>

          <aside class="side-panels">
            <article class="panel log-box">
              <h3>近期故障报告</h3>
              <article v-for="item in faults" :key="item.title" class="fault-card" :class="item.level">
                <div class="fault-top">
                  <strong>{{ item.title }}</strong>
                  <span>{{ item.time }}</span>
                </div>
                <p>{{ item.desc }}</p>
                <small>处理人：{{ item.user }}</small>
              </article>
            </article>

            <article class="panel shift-box">
              <h3>班组运行日志</h3>
              <div class="timeline">
                <div v-for="item in shiftLogs" :key="item.time" class="timeline-item" :class="item.level">
                  <span class="point"></span>
                  <div>
                    <strong>{{ item.time }}</strong>
                    <p>{{ item.desc }}</p>
                  </div>
                </div>
              </div>
            </article>
          </aside>
        </section>

        <section class="panel alert-table">
          <div class="panel-head">
            <h3>实时报警日志</h3>
            <span>{{ alarmRows.filter(r => r.level === '严重').length }} 条严重 / {{ alarmRows.filter(r => r.level === '告警').length }} 条一般告警</span>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>时间点</th>
                <th>级别</th>
                <th>设备编号</th>
                <th>报警描述</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in alarmRows" :key="item.time + item.device">
                <td>{{ item.time }}</td>
                <td>{{ item.level }}</td>
                <td>{{ item.device }}</td>
                <td>{{ item.desc }}</td>
                <td><button type="button" class="ack-btn" @click="ackAlarm(item)">确认</button></td>
              </tr>
            </tbody>
          </table>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Box, Cpu, Goods, Opportunity } from '@element-plus/icons-vue'

const nodes = ref([
  { name: '原煤仓', meta: '42.8 吨/小时', status: '运行', state: 'ok', icon: Box },
  { name: '给料皮带', meta: '98.2% 负荷', status: '运行', state: 'ok', icon: Goods },
  { name: '主破碎机', meta: '故障：过热', status: '报警', state: 'danger', icon: Cpu },
  { name: '洗选回路', meta: '待机', status: '空闲', state: 'idle', icon: Opportunity },
  { name: '产品仓 L-04', meta: '容量：82%', status: '运行', state: 'ok', icon: Box },
])

const faults = ref([
  { title: 'E-LOK 停机', time: '2 小时前', desc: '4 区碎料堵塞导致联锁停机，现场已安排检修。', user: '张工', level: 'danger' },
  { title: '传感器漂移', time: '5 小时前', desc: '灰分传感器校准值偏离 2%，需要重新标定。', user: '陈工', level: 'warn' },
])

const shiftLogs = ref([
  { time: '14:00', desc: '换班开始，B 班交接完成，主回路运行正常。', level: 'ok' },
  { time: '14:30', desc: '根据生产要求将原煤给料量提升至 45.0 吨/小时。', level: 'info' },
  { time: '15:15', desc: '检测到主破碎机过热报警，已通知检修人员赶赴现场。', level: 'danger' },
  { time: '16:00', desc: '从产品仓 L-04 抽取质量样本，已送往实验室。', level: 'idle' },
])

type AlarmRow = { time: string; level: string; device: string; desc: string }
const alarmRows = ref<AlarmRow[]>([
  { time: '14:22:15', level: '严重', device: 'CR-002-ALPHA', desc: '主破碎机轴承温度超过阈值（105℃）' },
  { time: '13:58:40', level: '告警', device: 'CONV-041-A', desc: '给料皮带 B 段驱动轮疑似打滑' },
  { time: '13:45:12', level: '告警', device: 'PMP-018', desc: '18 号污水泵振动值偏高' },
])

async function ackAlarm(item: AlarmRow) {
  try {
    await ElMessageBox.confirm(`确认处理设备 ${item.device} 的报警？`, '报警确认', { confirmButtonText: '确认处理', cancelButtonText: '取消', type: 'warning' })
    alarmRows.value = alarmRows.value.filter(r => r !== item)
    const now = new Date()
    const timeStr = `${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`
    shiftLogs.value.push({ time: timeStr, desc: `报警已确认：${item.device} - ${item.desc}`, level: 'info' })
    ElMessage.success(`${item.device} 报警已确认处理`)
  } catch { /* cancelled */ }
}
</script>

<style scoped>
.coal-page-v2{min-height:100vh;background:#091019;color:#eef6ff}
.page-layout{width:min(100%,1800px);margin:0 auto;display:grid;grid-template-columns:1fr;gap:24px;padding:24px}
.panel{border:1px solid rgba(122,190,255,.1);background:rgba(12,20,31,.92);box-shadow:0 18px 40px rgba(0,0,0,.18)}
.hero h1{margin:0;font-size:52px}.hero-text{max-width:760px;margin:12px 0 0;color:#97aac0;font-size:16px;line-height:1.7}.eyebrow{margin:0 0 10px;color:#78cfff;font-size:12px;letter-spacing:.2em;text-transform:uppercase}
.main-grid{display:grid;grid-template-columns:1.5fr .85fr;gap:24px;margin-top:20px}
.panel{padding:22px;border-radius:24px}
.flow-head{display:flex;justify-content:flex-end}
.legend{display:flex;gap:18px}.legend span{display:flex;align-items:center;gap:8px;color:#d0dae5}.legend i{width:12px;height:12px;border-radius:50%;display:inline-block}.legend .green{background:#18f0bf}.legend .gray{background:#6f7682}.legend .red{background:#ff6f73}
.flow-map{position:relative;display:grid;grid-template-columns:repeat(5,1fr);align-items:flex-start;gap:22px;height:360px;padding:38px 28px 20px}
.connector{position:absolute;left:90px;right:90px;top:140px;height:3px;background:#23313d}
.flow-node{position:relative;z-index:1;text-align:center}.node-icon{width:98px;height:98px;margin:0 auto 14px;border-radius:16px;display:grid;place-items:center;font-size:34px;background:#252c35;border:2px solid #3d4957}
.flow-node.ok .node-icon{border-color:#18f0bf;box-shadow:0 0 18px rgba(24,240,191,.18)}.flow-node.danger .node-icon{border-color:#ff6f73;color:#ff8d91;box-shadow:0 0 18px rgba(255,111,115,.18)}.flow-node.idle .node-icon{border-color:#49525e;color:#6f7682}
.flow-node strong{display:block;font-size:22px;margin-bottom:8px}.flow-node span,.flow-node small{display:block;color:#d6e0ea}.flow-node.danger strong,.flow-node.danger small{color:#ff7a7f}.flow-node.idle strong,.flow-node.idle small{color:#7e8896}
.flow-metrics{display:grid;grid-template-columns:repeat(4,1fr);gap:16px;margin-top:12px}
.metric{padding:18px;border-radius:16px;background:#06090d}.metric span{display:block;color:#9ba8b8;font-size:14px}.metric strong{display:block;margin-top:16px;font-size:30px}.metric.blue{box-shadow:inset 3px 0 0 #67d8ff}.metric.green{box-shadow:inset 3px 0 0 #18f0bf}.metric.yellow{box-shadow:inset 3px 0 0 #f8c76d}
.side-panels{display:grid;gap:24px}.log-box h3,.shift-box h3,.panel-head h3{margin:0 0 18px;font-size:24px}
.fault-card{padding:18px;border-radius:14px;background:#1c2430;margin-bottom:18px}.fault-card.danger{box-shadow:inset 4px 0 0 #d86f67}.fault-card.warn{box-shadow:inset 4px 0 0 #e0b56b}
.fault-top{display:flex;justify-content:space-between;gap:8px;margin-bottom:12px}.fault-card p{margin:0 0 14px;line-height:1.5;color:#d7e0e9}.fault-card small{color:#b9c6d1}
.timeline{display:grid;gap:16px}.timeline-item{display:grid;grid-template-columns:18px 1fr;gap:14px}.point{width:14px;height:14px;border-radius:50%;background:#18f0bf;box-shadow:0 0 10px rgba(24,240,191,.6);margin-top:4px}.timeline-item.info .point{background:#67d8ff}.timeline-item.danger .point{background:#ff6f73}.timeline-item.idle .point{background:#9097a3}
.timeline-item strong{display:block;margin-bottom:8px}.timeline-item p{margin:0;color:#d7e0e9;line-height:1.6}
.alert-table{margin-top:24px}.panel-head{display:flex;justify-content:space-between;align-items:center;gap:18px;margin-bottom:12px}.panel-head span{color:#ff8b90}
.data-table{width:100%;border-collapse:collapse}.data-table th,.data-table td{padding:16px 14px;text-align:left}.data-table thead th{background:#1b222c;color:#95a2b1}.data-table tbody td{border-top:1px solid rgba(120,161,195,.08)}
.ack-btn{width:72px;height:42px;border:1px solid #2f93ba;background:transparent;color:#86dfff;border-radius:8px;cursor:pointer}
@media (max-width: 1450px){
  .page-layout,.main-grid,.flow-metrics,.flow-map{grid-template-columns:1fr}
}
</style>
