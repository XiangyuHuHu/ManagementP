<template>
  <div class="workorder">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>工单管理</span>
          <el-button type="primary" size="small">创建工单</el-button>
        </div>
      </template>

      <el-table :data="workOrderList" style="width: 100%">
        <el-table-column prop="id" label="工单 ID" width="120" />
        <el-table-column prop="type" label="工单类型" width="150" />
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="creator" label="创建人" width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default>
            <el-button size="small">查看</el-button>
            <el-button size="small" type="primary">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const workOrderList = ref([
  { id: 'WO001', type: '检修工单', deviceName: '1 号皮带机', creator: '系统管理员', createTime: '2026-03-21 10:00:00', status: '待处理' },
  { id: 'WO002', type: '维修工单', deviceName: '主破碎机', creator: '张三', createTime: '2026-03-21 09:30:00', status: '处理中' },
  { id: 'WO003', type: '保养工单', deviceName: '振动筛', creator: '李四', createTime: '2026-03-21 08:15:00', status: '已完成' },
  { id: 'WO004', type: '检修工单', deviceName: '循环水泵', creator: '系统管理员', createTime: '2026-03-21 07:45:00', status: '待处理' },
])

const getStatusType = (status: string) => {
  switch (status) {
    case '待处理':
      return 'warning'
    case '处理中':
      return 'info'
    case '已完成':
      return 'success'
    case '已关闭':
      return 'danger'
    default:
      return 'info'
  }
}
</script>

<style scoped>
.workorder {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
