<template>
  <section class="quick-bar">
    <div class="quick-bar__copy">
      <p class="quick-bar__eyebrow">模块入口</p>
      <h2>{{ title }}</h2>
      <p v-if="subtitle">{{ subtitle }}</p>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  subtitle?: string
}>()
</script>

<style scoped>
.quick-bar {
  width: min(100%, 1680px);
  margin: 0 auto 16px;
  padding: 20px 22px;
  border: 1px solid rgba(96, 183, 255, 0.12);
  border-radius: 20px;
  background: rgba(9, 19, 29, 0.84);
  box-shadow: inset 0 1px 0 rgba(136, 211, 255, 0.06);
}

.quick-bar__eyebrow {
  margin: 0 0 6px;
  color: #7ecfff;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.2em;
  text-transform: uppercase;
}

.quick-bar h2 {
  margin: 0;
  font-size: 22px;
  line-height: 1.1;
  color: #f3faff;
}

.quick-bar__copy p:last-child {
  margin: 8px 0 0;
  color: rgba(224, 239, 251, 0.64);
  line-height: 1.6;
}
</style>
