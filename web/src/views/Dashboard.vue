<template>
  <div class="dashboard">
    <header class="header">
      <div class="header-left">
        <div class="logo">
          <el-icon><Monitor /></el-icon>
          <span>智能管控平台</span>
        </div>
        <nav class="nav-menu">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/dashboard" class="nav-item active">控制台</router-link>
          <router-link to="/dashboard/device" class="nav-item">设备管理</router-link>
          <router-link to="/dashboard/monitor" class="nav-item">实时监测</router-link>
          <router-link to="/dashboard/alarm" class="nav-item">告警中心</router-link>
        </nav>
      </div>
      <div class="header-right">
        <div class="time">
          <span class="time-value">{{ currentTime }}</span>
          <span class="date-value">{{ currentDate }}</span>
        </div>
        <div class="user">
          <el-icon><User /></el-icon>
          <span>admin</span>
        </div>
      </div>
    </header>

    <main class="main-content">
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><Cpu /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">1,234</span>
            <span class="stat-label">设备总数</span>
            <div class="stat-trend up">+12 本月新增</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon success">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">98.5%</span>
            <span class="stat-label">设备完好率</span>
            <div class="stat-trend up">+2.3% 较上月</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon warning">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">23</span>
            <span class="stat-label">待处理告警</span>
            <div class="stat-trend down">+5 今日新增</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon info">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">24h</span>
            <span class="stat-label">连续运行</span>
            <div class="stat-trend">系统稳定</div>
          </div>
        </div>
      </div>

      <div class="charts-row">
        <div class="chart-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="title-icon"></span>
              <h3>设备状态分布</h3>
            </div>
            <el-radio-group v-model="deviceTimeRange" size="small">
              <el-radio-button value="day">今日</el-radio-button>
              <el-radio-button value="week">本周</el-radio-button>
              <el-radio-button value="month">本月</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="deviceChart" class="chart-container"></div>
        </div>

        <div class="chart-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="title-icon"></span>
              <h3>告警趋势</h3>
            </div>
            <el-radio-group v-model="alarmTimeRange" size="small">
              <el-radio-button value="day">今日</el-radio-button>
              <el-radio-button value="week">本周</el-radio-button>
              <el-radio-button value="month">本月</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="alarmChart" class="chart-container"></div>
        </div>
      </div>

      <div class="bottom-row">
        <div class="list-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="title-icon"></span>
              <h3>最近告警</h3>
            </div>
            <router-link to="/dashboard/alarm" class="view-all">查看全部</router-link>
          </div>
          <div class="list-content">
            <div v-for="alarm in recentAlarms" :key="alarm.id" class="list-item">
              <div class="item-icon" :class="alarm.level">
                <el-icon><Bell /></el-icon>
              </div>
              <div class="item-content">
                <div class="item-title">{{ alarm.title }}</div>
                <div class="item-meta">
                  <span>{{ alarm.device }}</span>
                  <span>{{ alarm.time }}</span>
                </div>
              </div>
              <el-tag :type="alarm.level === 'high' ? 'danger' : alarm.level === 'medium' ? 'warning' : 'info'" size="small" effect="dark">
                {{ alarm.levelText }}
              </el-tag>
            </div>
          </div>
        </div>

        <div class="list-panel">
          <div class="panel-header">
            <div class="panel-title">
              <span class="title-icon"></span>
              <h3>待处理工单</h3>
            </div>
            <router-link to="/dashboard/workorder" class="view-all">查看全部</router-link>
          </div>
          <div class="list-content">
            <div v-for="order in pendingWorkOrders" :key="order.id" class="list-item">
              <div class="item-icon" :class="order.type">
                <el-icon><Tickets /></el-icon>
              </div>
              <div class="item-content">
                <div class="item-title">{{ order.title }}</div>
                <div class="item-meta">
                  <span>{{ order.device }}</span>
                  <span>截止: {{ order.deadline }}</span>
                </div>
              </div>
              <el-tag :type="order.priority === 'high' ? 'danger' : 'warning'" size="small" effect="dark">
                {{ order.priorityText }}
              </el-tag>
            </div>
          </div>
        </div>

        <div class="chart-panel small">
          <div class="panel-header">
            <div class="panel-title">
              <span class="title-icon"></span>
              <h3>设备类型分布</h3>
            </div>
          </div>
          <div ref="typeChart" class="chart-container"></div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { echarts } from '../utils/echarts'
import { Bell, CircleCheck, Cpu, Monitor, Tickets, TrendCharts, User, Warning } from '@element-plus/icons-vue'

const deviceTimeRange = ref('day')
const alarmTimeRange = ref('week')
const deviceChart = ref<HTMLElement>()
const alarmChart = ref<HTMLElement>()
const typeChart = ref<HTMLElement>()
const currentTime = ref('')
const currentDate = ref('')

let deviceChartInstance: echarts.ECharts | null = null
let alarmChartInstance: echarts.ECharts | null = null
let typeChartInstance: echarts.ECharts | null = null
let timer: number

const recentAlarms = ref([
  { id: 1, title: '设备温度过高', device: '主破碎机 #1', time: '5 分钟前', level: 'high', levelText: '紧急' },
  { id: 2, title: '电机电流异常', device: '1 号皮带机', time: '15 分钟前', level: 'medium', levelText: '重要' },
  { id: 3, title: '润滑油位偏低', device: '球磨机 #2', time: '1 小时前', level: 'low', levelText: '一般' },
  { id: 4, title: '振动超限预警', device: '振动筛 #1', time: '2 小时前', level: 'medium', levelText: '重要' },
])

const pendingWorkOrders = ref([
  { id: 1, title: '破碎机定期保养', device: '主破碎机 #1', deadline: '2026-03-24', type: 'maintenance', priority: 'high', priorityText: '高' },
  { id: 2, title: '皮带机故障检修', device: '1 号皮带机', deadline: '2026-03-23', type: 'repair', priority: 'high', priorityText: '高' },
  { id: 3, title: '电机巡检复核', device: '球磨机 #1', deadline: '2026-03-25', type: 'inspection', priority: 'medium', priorityText: '中' },
])

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'long',
  })
}

const handleResize = () => {
  deviceChartInstance?.resize()
  alarmChartInstance?.resize()
  typeChartInstance?.resize()
}

onMounted(() => {
  updateTime()
  timer = window.setInterval(updateTime, 1000)

  if (deviceChart.value) {
    deviceChartInstance = echarts.init(deviceChart.value)
    deviceChartInstance.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: 'rgba(0,0,0,0.8)', borderColor: '#00c8ff', textStyle: { color: '#fff' } },
      legend: { bottom: '5%', left: 'center', textStyle: { color: 'rgba(255,255,255,0.7)' } },
      series: [
        {
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#0a1628', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold', color: '#fff' } },
          data: [
            { value: 1048, name: '运行中', itemStyle: { color: '#00d26a' } },
            { value: 735, name: '待机', itemStyle: { color: '#00c8ff' } },
            { value: 580, name: '检修中', itemStyle: { color: '#ff9500' } },
            { value: 484, name: '故障', itemStyle: { color: '#ff5252' } },
          ],
        },
      ],
    })
  }

  if (alarmChart.value) {
    alarmChartInstance = echarts.init(alarmChart.value)
    alarmChartInstance.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'axis', backgroundColor: 'rgba(0,0,0,0.8)', borderColor: '#00c8ff', textStyle: { color: '#fff' } },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
        axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } },
        axisLabel: { color: 'rgba(255,255,255,0.6)' },
      },
      yAxis: {
        type: 'value',
        axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } },
        axisLabel: { color: 'rgba(255,255,255,0.6)' },
        splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } },
      },
      series: [
        {
          name: '告警数',
          type: 'line',
          smooth: true,
          data: [12, 15, 8, 20, 18, 10, 14],
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(255, 82, 82, 0.3)' },
              { offset: 1, color: 'rgba(255, 82, 82, 0.05)' },
            ]),
          },
          itemStyle: { color: '#ff5252' },
          lineStyle: { width: 3 },
        },
      ],
    })
  }

  if (typeChart.value) {
    typeChartInstance = echarts.init(typeChart.value)
    typeChartInstance.setOption({
      backgroundColor: 'transparent',
      tooltip: { trigger: 'item', backgroundColor: 'rgba(0,0,0,0.8)', borderColor: '#00c8ff', textStyle: { color: '#fff' } },
      series: [
        {
          type: 'pie',
          radius: '60%',
          center: ['50%', '50%'],
          data: [
            { value: 335, name: '破碎设备', itemStyle: { color: '#00c8ff' } },
            { value: 310, name: '输送设备', itemStyle: { color: '#448aff' } },
            { value: 234, name: '筛分设备', itemStyle: { color: '#00d4ff' } },
            { value: 135, name: '泵类设备', itemStyle: { color: '#00d26a' } },
            { value: 148, name: '其他设备', itemStyle: { color: '#78909c' } },
          ],
          label: { color: 'rgba(255,255,255,0.7)' },
        },
      ],
    })
  }

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  clearInterval(timer)
  window.removeEventListener('resize', handleResize)
  deviceChartInstance?.dispose()
  alarmChartInstance?.dispose()
  typeChartInstance?.dispose()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #0a1628 0%, #1a2a4a 50%, #0d1b2a 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  height: 70px;
  background: linear-gradient(90deg, rgba(0,150,255,0.1) 0%, rgba(0,200,255,0.05) 50%, rgba(0,150,255,0.1) 100%);
  border-bottom: 1px solid rgba(0,200,255,0.2);
  position: relative;
}

.header::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, #00c8ff 50%, transparent 100%);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 60px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 700;
  color: #00c8ff;
  text-shadow: 0 0 20px rgba(0,200,255,0.5);
}

.logo .el-icon {
  font-size: 32px;
}

.nav-menu {
  display: flex;
  gap: 40px;
}

.nav-item {
  color: rgba(255,255,255,0.7);
  text-decoration: none;
  font-size: 16px;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s ease;
  position: relative;
}

.nav-item:hover,
.nav-item.active {
  color: #00c8ff;
  background: rgba(0,200,255,0.1);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -16px;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 3px;
  background: #00c8ff;
  border-radius: 2px;
  box-shadow: 0 0 10px rgba(0,200,255,0.5);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.time {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.time-value {
  font-size: 20px;
  font-weight: 600;
  color: #00c8ff;
  font-family: 'Courier New', monospace;
}

.date-value {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}

.user {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(0,200,255,0.1);
  border-radius: 20px;
  border: 1px solid rgba(0,200,255,0.3);
  color: #00c8ff;
}

.main-content {
  flex: 1;
  padding: 24px 40px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  overflow: auto;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: rgba(0,100,200,0.1);
  border: 1px solid rgba(0,200,255,0.2);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  border-color: rgba(0,200,255,0.4);
  box-shadow: 0 4px 20px rgba(0,200,255,0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(135deg, #00c8ff, #0066ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  box-shadow: 0 4px 20px rgba(0,200,255,0.3);
}

.stat-icon.success {
  background: linear-gradient(135deg, #00d26a, #00a854);
  box-shadow: 0 4px 20px rgba(0,210,106,0.3);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #ff9500, #ff5500);
  box-shadow: 0 4px 20px rgba(255,149,0,0.3);
}

.stat-icon.info {
  background: linear-gradient(135deg, #00d4ff, #0099ff);
  box-shadow: 0 4px 20px rgba(0,212,255,0.3);
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  color: rgba(255,255,255,0.6);
  margin: 4px 0;
}

.stat-trend {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
}

.stat-trend.up {
  color: #00d26a;
}

.stat-trend.down {
  color: #ff5252;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-panel {
  background: rgba(0,50,100,0.2);
  border: 1px solid rgba(0,200,255,0.15);
  border-radius: 12px;
  padding: 20px;
}

.chart-panel.small {
  min-height: 300px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #00c8ff, #0066ff);
  border-radius: 2px;
}

.panel-title h3 {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin: 0;
}

.view-all {
  font-size: 13px;
  color: #00c8ff;
  text-decoration: none;
}

.view-all:hover {
  text-decoration: underline;
}

.chart-container {
  height: 280px;
}

.bottom-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.list-panel {
  background: rgba(0,50,100,0.2);
  border: 1px solid rgba(0,200,255,0.15);
  border-radius: 12px;
  padding: 20px;
}

.list-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(0,30,60,0.3);
  border-radius: 8px;
  border: 1px solid rgba(0,200,255,0.1);
  transition: all 0.3s ease;
}

.list-item:hover {
  border-color: rgba(0,200,255,0.3);
  background: rgba(0,50,100,0.3);
}

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: rgba(255,82,82,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ff5252;
}

.item-icon.medium {
  background: rgba(255,149,0,0.2);
  color: #ff9500;
}

.item-icon.low {
  background: rgba(0,200,255,0.2);
  color: #00c8ff;
}

.item-icon.maintenance {
  background: rgba(255,149,0,0.2);
  color: #ff9500;
}

.item-icon.repair {
  background: rgba(255,82,82,0.2);
  color: #ff5252;
}

.item-icon.inspection {
  background: rgba(0,210,106,0.2);
  color: #00d26a;
}

.item-content {
  flex: 1;
}

.item-title {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  margin-bottom: 4px;
}

.item-meta {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
  display: flex;
  gap: 12px;
}

@media (max-width: 1400px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-row {
    grid-template-columns: 1fr;
  }

  .bottom-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 0 20px;
  }

  .nav-menu {
    display: none;
  }

  .main-content {
    padding: 20px;
  }

  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
