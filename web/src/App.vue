<template>
  <div id="app">
    <CoalGlobalNav v-if="isCoalRoute" />
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import CoalGlobalNav from './components/coal/CoalGlobalNav.vue'

const route = useRoute()
const isCoalRoute = computed(() => route.path.startsWith('/coal'))
</script>
