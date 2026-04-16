<template>
  <CoalCommandModulePage v-if="config" :config="config" />
  <div v-else class="fallback">模块未配置</div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import CoalCommandModulePage from '../../components/coal/CoalCommandModulePage.vue'
import { moduleConfigs } from './moduleConfigs'

const route = useRoute()

const config = computed(() => {
  const key = String(route.meta.moduleKey || '')
  return moduleConfigs[key]
})
</script>

<style scoped>
.fallback {
  min-height: 100%;
  display: grid;
  place-items: center;
  color: #c6def6;
  background: #07111b;
}
</style>
