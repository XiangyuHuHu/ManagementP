<template>
  <div class="coal-page-v2 monitor-page">
    <div class="page-layout">
      <main class="page-main">
        <section class="hero">
          <div>
            <p class="eyebrow">巡检监测</p>
            <h1>智能巡检与人员安全中心</h1>
            <p class="hero-text">集中展示巡检覆盖率、人员分布、AI 异常识别和任务执行状态，便于安全与调度协同处理。</p>
          </div>
          <div class="hero-tips">
            <div class="tip-card">
              <span>当前在线巡检员</span>
              <strong>8 人</strong>
            </div>
            <div class="tip-card">
              <span>待处理报警</span>
              <strong class="danger">3 条</strong>
            </div>
          </div>
        </section>

        <section class="stats-grid">
          <article v-for="item in stats" :key="item.label" class="stat-card" :class="item.className">
            <div>
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
              <small>{{ item.note }}</small>
            </div>
            <el-icon><component :is="item.icon" /></el-icon>
          </article>
        </section>

        <section class="main-grid">
          <article class="panel map-panel">
            <div class="panel-head">
              <h3>厂区 2D 区域监控与人员分布</h3>
              <div class="map-tags">
                <span>作业人员：42</span>
                <span>巡检员：8</span>
              </div>
            </div>

            <div class="map-stage">
              <div class="map-placeholder">2D 厂区平面示意</div>
              <span class="pulse p1"></span>
              <span class="pulse p2 danger"></span>
              <span class="pulse p3 blue"></span>
              <div class="activity-list">
                <article v-for="item in activities" :key="item.time + item.title" class="activity-card" :class="item.level">
                  <div>
                    <strong>{{ item.title }}</strong>
                    <p>{{ item.name }}</p>
                  </div>
                  <span>{{ item.time }}</span>
                </article>
              </div>
            </div>
          </article>

          <article class="panel ai-panel">
            <div class="panel-head">
              <h3>AI 视觉异常识别</h3>
            </div>
            <div class="ai-cards">
              <article v-for="item in aiCards" :key="item.title" class="ai-card">
                <div class="ai-image" :class="item.imageClass"></div>
                <div class="ai-copy">
                  <div class="ai-top">
                    <strong>{{ item.title }}</strong>
                    <span>{{ item.time }}</span>
                  </div>
                  <p>{{ item.desc }}</p>
                  <span class="ai-tag" :class="item.level">{{ item.tag }}</span>
                </div>
              </article>
            </div>
          </article>
        </section>

        <section class="bottom-grid">
          <article class="panel task-panel">
            <div class="panel-head">
              <h3>当前巡检任务</h3>
              <button type="button" class="ghost-btn" @click="notify('已打开全部巡检任务列表')">查看全部任务</button>
            </div>

            <table class="task-table">
              <thead>
                <tr>
                  <th>任务名称</th>
                  <th>巡检员</th>
                  <th>进度</th>
                  <th>状态</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in tasks" :key="item.name">
                  <td>{{ item.name }}</td>
                  <td>{{ item.user }}</td>
                  <td>
                    <div class="progress"><div class="progress-fill" :style="{ width: item.progress }"></div></div>
                    <span>{{ item.progress }}</span>
                  </td>
                  <td>
                    <span v-if="item.status === '已完成'" class="task-status done">已完成</span>
                    <button v-else-if="item.status === '等待开始'" type="button" class="task-action" @click="startTask(item)">开始巡检</button>
                    <button v-else type="button" class="task-action running" @click="completeTask(item)">标记完成</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </article>

          <article class="panel alert-panel">
            <div class="panel-head">
              <h3>安全报警中心</h3>
            </div>
            <div class="alert-list">
              <article v-for="(item, idx) in alerts" :key="item.title + item.time" class="alert-card" :class="item.level">
                <div class="alert-top">
                  <strong>{{ item.title }}</strong>
                  <span>{{ item.time }}</span>
                </div>
                <p>{{ item.desc }}</p>
                <button v-if="item.level !== 'normal'" type="button" class="alert-ack-btn" @click="ackAlert(idx)">确认处理</button>
              </article>
            </div>
          </article>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell, Monitor, Setting, User } from '@element-plus/icons-vue'

type TaskItem = { name: string; user: string; progress: string; status: string; statusClass: string }
type AlertItem = { title: string; time: string; desc: string; level: string }

const tasks = ref<TaskItem[]>([
  { name: '三层原煤输送系统日检', user: '王克勤', progress: '75%', status: '正在执行', statusClass: 'running' },
  { name: '浮选机液位巡检', user: '赵大成', progress: '45%', status: '正在执行', statusClass: 'running' },
  { name: '变电站配电柜季检', user: '孙兴', progress: '0%', status: '等待开始', statusClass: 'idle' },
])

const alerts = ref<AlertItem[]>([
  { title: '区域闯入报警', time: '14:42:01', desc: '外来人员未授权进入高压电磁屏蔽区，系统已通知现场安保。', level: 'danger' },
  { title: '巡检超时预警', time: '14:15:33', desc: '4 号浓缩池巡检停留时间超过 20 分钟，请确认是否需要协助。', level: 'warn' },
  { title: '报警处理完成', time: '13:50:00', desc: '胶仓皮带偏斜报警已由现场人工复位，系统恢复正常监控。', level: 'normal' },
])

const activeAlertCount = computed(() => alerts.value.filter(a => a.level !== 'normal').length)

const stats = computed(() => [
  { label: '人员到岗率', value: '98.4%', note: '较昨日提升 1.2%', className: 'green', icon: User },
  { label: '巡检覆盖率', value: `${Math.round(tasks.value.filter(t => t.status === '已完成').length / tasks.value.length * 100) || 100}%`, note: `${tasks.value.filter(t => t.status === '已完成').length}/${tasks.value.length} 项已完成`, className: 'blue', icon: Monitor },
  { label: '运行设备', value: '124 台', note: '3 台正在检修中', className: 'yellow', icon: Setting },
  { label: '异常告警', value: `${String(activeAlertCount.value).padStart(2, '0')} 条`, note: `${alerts.value.length} 条记录`, className: 'red', icon: Bell },
])

const activities = [
  { title: '作业中', name: '张伟 - 3 号洗选池', time: '14:52', level: 'ok' },
  { title: '巡检中', name: '王克勤 - 2 号皮带', time: '14:50', level: 'info' },
  { title: '警报', name: '李明 - 限制区域', time: '14:48', level: 'danger' },
  { title: '作业中', name: '赵强 - 压滤车间', time: '14:45', level: 'ok' },
  { title: '作业中', name: '孙大志 - 1 号转载点', time: '14:42', level: 'ok' },
]

const aiCards = [
  { title: '2 号传送带区域', desc: '检测到人员进入作业区域但未正确佩戴防护装备，需要现场复核。', time: '14:23:45', tag: '未戴安全帽', level: 'danger', imageClass: 'image-warn' },
  { title: '压滤机 4 号阀门', desc: '检测到设备附近存在异常高温点，建议立即安排巡检人员复查。', time: '14:10:12', tag: '疑似高温异常', level: 'warn', imageClass: 'image-heat' },
]

function startTask(item: TaskItem) {
  item.status = '正在执行'
  item.statusClass = 'running'
  item.progress = '10%'
  ElMessage.success(`${item.name} 已开始执行`)
}

function completeTask(item: TaskItem) {
  item.status = '已完成'
  item.statusClass = 'done'
  item.progress = '100%'
  ElMessage.success(`${item.name} 已标记完成`)
}

function ackAlert(idx: number) {
  const item = alerts.value[idx]
  const now = new Date()
  const timeStr = `${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}:${String(now.getSeconds()).padStart(2,'0')}`
  alerts.value.splice(idx, 1, { ...item, level: 'normal', title: item.title + '（已处理）', time: timeStr, desc: item.desc + ' —— 已确认处理。' })
  ElMessage.success('报警已确认处理')
}

const notify = (message: string) => ElMessage.success(message)
</script>

<style scoped>
.coal-page-v2{min-height:100vh;background:#091019;color:#eef6ff}
.page-layout{width:min(100%,1800px);margin:0 auto;display:grid;grid-template-columns:1fr;gap:24px;padding:24px}
.panel,.stat-card{border:1px solid rgba(122,190,255,.1);background:rgba(12,20,31,.92);box-shadow:0 18px 40px rgba(0,0,0,.18)}
.hero{display:flex;justify-content:space-between;gap:24px;align-items:flex-end;margin-bottom:18px}
.eyebrow{margin:0 0 10px;color:#78cfff;font-size:12px;letter-spacing:.2em;text-transform:uppercase}
.hero h1{margin:0;font-size:46px;line-height:1.05}
.hero-text{max-width:760px;margin:12px 0 0;color:#97aac0;font-size:16px;line-height:1.7}
.hero-tips{display:flex;gap:12px}
.tip-card{min-width:160px;padding:16px;border-radius:16px;background:#141f2d}
.tip-card span{display:block;color:#92a6bc;font-size:12px}
.tip-card strong{display:block;margin-top:8px;font-size:26px}
.tip-card strong.danger{color:#ff7a82}
.stats-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:18px}
.stat-card{display:flex;justify-content:space-between;align-items:flex-start;padding:22px;border-radius:24px}
.stat-card span{display:block;color:#97a4b4;font-size:14px}.stat-card strong{display:block;margin:18px 0 10px;font-size:54px;line-height:1}.stat-card small{color:#c4d0db;font-size:16px}
.stat-card.green{box-shadow:inset 4px 0 0 #18f0bf,0 18px 40px rgba(0,0,0,.18)}.stat-card.blue{box-shadow:inset 4px 0 0 #67d8ff,0 18px 40px rgba(0,0,0,.18)}.stat-card.yellow{box-shadow:inset 4px 0 0 #f9c76a,0 18px 40px rgba(0,0,0,.18)}.stat-card.red{box-shadow:inset 4px 0 0 #ff6f73,0 18px 40px rgba(0,0,0,.18)}
.main-grid,.bottom-grid{display:grid;grid-template-columns:1.65fr .75fr;gap:18px;margin-top:18px}
.panel{padding:22px;border-radius:24px}
.panel-head{display:flex;justify-content:space-between;align-items:center;gap:18px;margin-bottom:14px}
.panel-head h3{margin:0;font-size:26px}
.map-tags{display:flex;gap:12px}.map-tags span{padding:8px 12px;border-radius:999px;background:#1e2631;color:#afc1d0}
.map-stage{position:relative;height:430px;border-radius:18px;background:#0f141b;overflow:hidden}
.map-placeholder{position:absolute;left:18px;top:16px;color:#8895a7;font-size:18px}
.pulse{position:absolute;width:18px;height:18px;border-radius:50%;background:#19f0c1;box-shadow:0 0 0 6px rgba(25,240,193,.12),0 0 16px rgba(25,240,193,.8)}.pulse.blue{background:#68d8ff;box-shadow:0 0 0 6px rgba(104,216,255,.12),0 0 16px rgba(104,216,255,.8)}.pulse.danger{background:#ff6f73;box-shadow:0 0 0 6px rgba(255,111,115,.12),0 0 16px rgba(255,111,115,.8)}
.p1{left:38%;top:34%}.p2{left:63%;top:66%}.p3{left:70%;top:82%}
.activity-list{position:absolute;right:10px;top:0;width:210px;height:100%;padding:12px;background:#1b222c}
.activity-card{display:flex;justify-content:space-between;gap:10px;padding:12px 10px;margin-bottom:10px;border-left:3px solid #3adcb7;background:#202732}.activity-card.info{border-left-color:#68d8ff}.activity-card.danger{border-left-color:#ff6f73}.activity-card strong{display:block;margin-bottom:4px}.activity-card p{margin:0;color:#dbe6f1}
.ai-cards{display:grid;gap:18px}.ai-card{border:1px solid rgba(255,255,255,.08);border-radius:16px;overflow:hidden;background:#1b222c}
.ai-image{height:176px}.ai-image.image-warn{background:radial-gradient(circle at 58% 40%,#f9cb68,#8d5516 36%,#23160f 70%)}.ai-image.image-heat{background:radial-gradient(circle at 35% 28%,#676767,#111 56%,#321818 100%)}
.ai-copy{padding:14px}.ai-top{display:flex;justify-content:space-between;gap:10px;margin-bottom:8px}.ai-copy p{margin:0 0 12px;color:#cad6e0;line-height:1.5}
.ai-tag{display:inline-flex;padding:6px 10px;border-radius:6px;font-size:12px}.ai-tag.danger{background:#ff7b7f;color:#fff}.ai-tag.warn{background:#f7c463;color:#3b2800}
.ghost-btn{height:36px;padding:0 14px;border:1px solid rgba(104,216,255,.35);border-radius:8px;background:transparent;color:#68d8ff;cursor:pointer}
.task-table{width:100%;border-collapse:collapse}.task-table th,.task-table td{padding:14px 16px;text-align:left}.task-table thead th{background:#111821;color:#96a4b7}
.progress{display:inline-block;width:155px;height:10px;margin-right:10px;border-radius:999px;background:#2b3240;vertical-align:middle}.progress-fill{height:100%;border-radius:inherit;background:#78dcff}
.task-status{display:inline-block;padding:6px 10px;border-radius:6px;font-size:13px}.task-status.running{background:rgba(24,240,191,.18);color:#18f0bf}.task-status.idle{background:#2d3440;color:#c8d4de}.task-status.done{background:rgba(103,216,255,.15);color:#67d8ff}
.task-action{height:36px;padding:0 14px;border:1px solid #2f93ba;background:transparent;color:#86dfff;border-radius:8px;cursor:pointer}.task-action:hover{background:rgba(104,216,255,.1)}.task-action.running{border-color:#18f0bf;color:#18f0bf}
.alert-list{display:grid;gap:18px}.alert-card{padding:18px;border-radius:16px;background:#1b222c}.alert-card.danger{box-shadow:inset 4px 0 0 #ff6f73}.alert-card.warn{box-shadow:inset 4px 0 0 #f7c463}.alert-card.normal{box-shadow:inset 4px 0 0 #8f99a6}.alert-top{display:flex;justify-content:space-between;gap:10px;margin-bottom:10px}.alert-card p{margin:0;color:#cad6e0;line-height:1.6}
.alert-ack-btn{margin-top:12px;height:34px;padding:0 16px;border:1px solid rgba(255,111,115,.4);border-radius:8px;background:transparent;color:#ff8b90;cursor:pointer}.alert-ack-btn:hover{background:rgba(255,111,115,.1)}
@media (max-width: 1450px){
  .page-layout,.stats-grid,.main-grid,.bottom-grid{grid-template-columns:1fr}
}
</style>
