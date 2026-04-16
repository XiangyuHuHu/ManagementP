<template>
  <div class="device">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备管理</span>
          <el-button type="primary" size="small">新增设备</el-button>
        </div>
      </template>

      <el-table :data="deviceList" style="width: 100%">
        <el-table-column prop="id" label="设备 ID" width="120" />
        <el-table-column prop="name" label="设备名称" />
        <el-table-column prop="category" label="设备分类" width="160" />
        <el-table-column prop="location" label="设备位置" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button size="small">编辑</el-button>
            <el-button size="small" type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const deviceList = ref([
  { id: 'DEV001', name: '主破碎机', category: '破碎设备', location: '破碎车间', status: '运行中' },
  { id: 'DEV002', name: '振动筛', category: '筛分设备', location: '筛分车间', status: '待机' },
  { id: 'DEV003', name: '1 号皮带机', category: '输送设备', location: '输送车间', status: '运行中' },
  { id: 'DEV004', name: '循环水泵', category: '泵类设备', location: '泵房', status: '检修中' },
])

const getTagType = (status: string) => {
  switch (status) {
    case '运行中':
      return 'success'
    case '待机':
      return 'warning'
    case '检修中':
      return 'danger'
    default:
      return 'info'
  }
}
</script>

<style scoped>
.device {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
