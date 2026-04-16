<template>
  <div class="home">
    <el-header class="header">
      <h1>智能管控平台</h1>
    </el-header>
    <el-main class="main">
      <el-grid :cols="2" :gutter="20">
        <el-grid-item>
          <el-card class="menu-card" @click="navigate('/dashboard')">
            <template #header>
              <div class="card-header">
                <span>数据概览</span>
              </div>
            </template>
            <div class="card-content">
              <i class="el-icon-data-analysis"></i>
              <p>查看系统整体运行状态</p>
            </div>
          </el-card>
        </el-grid-item>
        <el-grid-item>
          <el-card class="menu-card" @click="navigate('/monitor')">
            <template #header>
              <div class="card-header">
                <span>实时监控</span>
              </div>
            </template>
            <div class="card-content">
              <i class="el-icon-video-camera"></i>
              <p>查看设备实时运行数据</p>
            </div>
          </el-card>
        </el-grid-item>
        <el-grid-item>
          <el-card class="menu-card" @click="navigate('/alarm')">
            <template #header>
              <div class="card-header">
                <span>报警中心</span>
              </div>
            </template>
            <div class="card-content">
              <i class="el-icon-warning"></i>
              <p>查看系统报警信息</p>
            </div>
          </el-card>
        </el-grid-item>
        <el-grid-item>
          <el-card class="menu-card" @click="navigate('/device')">
            <template #header>
              <div class="card-header">
                <span>设备管理</span>
              </div>
            </template>
            <div class="card-content">
              <i class="el-icon-s-tools"></i>
              <p>查看设备台账信息</p>
            </div>
          </el-card>
        </el-grid-item>
        <el-grid-item>
          <el-card class="menu-card" @click="navigate('/report')">
            <template #header>
              <div class="card-header">
                <span>生产报表</span>
              </div>
            </template>
            <div class="card-content">
              <i class="el-icon-document"></i>
              <p>查看生产和煤质报表</p>
            </div>
          </el-card>
        </el-grid-item>
        <el-grid-item>
          <el-card class="menu-card" @click="navigate('/workorder')">
            <template #header>
              <div class="card-header">
                <span>工单管理</span>
              </div>
            </template>
            <div class="card-content">
              <i class="el-icon-notebook-2"></i>
              <p>查看设备工单信息</p>
            </div>
          </el-card>
        </el-grid-item>
      </el-grid>
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const navigate = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #409EFF;
  color: white;
  text-align: center;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header h1 {
  margin: 0;
  font-size: 1.5rem;
}

.main {
  flex: 1;
  padding: 1rem;
  background-color: #f5f7fa;
}

.menu-card {
  cursor: pointer;
  transition: all 0.3s ease;
  height: 120px;
}

.menu-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80px;
}

.card-content i {
  font-size: 2rem;
  color: #409EFF;
  margin-bottom: 0.5rem;
}

.card-content p {
  margin: 0;
  font-size: 0.9rem;
  color: #606266;
  text-align: center;
}
</style>
