<template>
  <header class="coal-nav-shell">
    <div class="coal-nav">
      <button type="button" class="coal-nav__brand" @click="navigateAndClose('/coal')">
        <span class="coal-nav__brand-mark"></span>
        <span class="coal-nav__brand-copy">
          <strong>COAL</strong>
          <small>选煤厂综合管控平台</small>
        </span>
      </button>

      <nav class="coal-nav__links" aria-label="coal navigation">
        <template v-for="item in coalQuickNav" :key="item.path">
          <button
            type="button"
            class="coal-nav__button"
            :class="{ active: route.path === item.path }"
            @click="navigateAndClose(item.path)"
          >
            {{ item.label }}
          </button>
        </template>
      </nav>

      <details ref="extensionDetailsRef" class="coal-nav__more">
        <summary>扩展功能</summary>
        <div class="coal-nav__panel">
          <button
            v-for="item in extraLinks"
            :key="item.path"
            type="button"
            class="coal-nav__extra"
            :class="{ active: route.path === item.path }"
            @click="navigateAndClose(item.path)"
          >
            <strong>{{ item.label }}</strong>
            <small>{{ item.desc }}</small>
          </button>
        </div>
      </details>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { coalExtensionEntries, coalQuickNav } from '../../views/coal/coalNav'

const route = useRoute()
const router = useRouter()

const extraLinks = coalExtensionEntries

const extensionDetailsRef = ref<HTMLDetailsElement | null>(null)

function navigateAndClose(path: string) {
  router.push(path)
  if (extensionDetailsRef.value) extensionDetailsRef.value.open = false
}
</script>

<style scoped>
.coal-nav-shell {
  position: fixed;
  top: 10px;
  left: 0;
  z-index: 4000;
  width: 100%;
  padding: 0 20px;
}

.coal-nav {
  display: flex;
  align-items: center;
  gap: 18px;
  width: min(100%, 1680px);
  margin: 0 auto;
  padding: 12px 14px;
  border: 1px solid rgba(96, 183, 255, 0.14);
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(8, 18, 28, 0.96), rgba(10, 21, 33, 0.92));
  box-shadow: 0 18px 42px rgba(0, 0, 0, 0.34), inset 0 1px 0 rgba(142, 212, 255, 0.08);
  backdrop-filter: blur(14px);
}

.coal-nav__brand {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 0;
  border: 0;
  background: transparent;
  color: #eff8ff;
  cursor: pointer;
}

.coal-nav__brand-mark {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #59d8ff, #21d69f);
  box-shadow: 0 0 22px rgba(87, 216, 255, 0.45);
}

.coal-nav__brand-copy {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.coal-nav__brand-copy strong {
  font-family: 'Space Grotesk', 'Inter', sans-serif;
  font-size: 14px;
  letter-spacing: 0.18em;
}

.coal-nav__brand-copy small {
  color: rgba(219, 236, 251, 0.62);
  font-size: 11px;
  letter-spacing: 0.08em;
}

.coal-nav__links {
  display: flex;
  flex: 1;
  flex-wrap: wrap;
  gap: 8px;
  min-width: 0;
}

.coal-nav__button,
.coal-nav__extra {
  border: 1px solid rgba(96, 183, 255, 0.12);
  cursor: pointer;
  transition: 0.2s ease;
}

.coal-nav__button {
  padding: 9px 14px;
  border-radius: 999px;
  background: rgba(15, 28, 41, 0.92);
  color: rgba(226, 241, 255, 0.86);
}

.coal-nav__button.active,
.coal-nav__button:hover {
  border-color: rgba(87, 216, 255, 0.34);
  background: linear-gradient(135deg, rgba(72, 176, 255, 0.22), rgba(30, 216, 166, 0.16));
  color: #ffffff;
}

.coal-nav__more {
  position: relative;
  flex: none;
}

.coal-nav__more summary {
  display: inline-flex;
  align-items: center;
  height: 40px;
  padding: 0 16px;
  border: 1px solid rgba(96, 183, 255, 0.14);
  border-radius: 999px;
  list-style: none;
  background: rgba(12, 25, 38, 0.9);
  color: #9ddbff;
  cursor: pointer;
}

.coal-nav__more summary.active,
.coal-nav__more summary:hover {
  border-color: rgba(87, 216, 255, 0.34);
  background: linear-gradient(135deg, rgba(72, 176, 255, 0.22), rgba(30, 216, 166, 0.16));
  color: #ffffff;
}

.coal-nav__more summary::-webkit-details-marker {
  display: none;
}

.coal-nav__panel {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  display: grid;
  gap: 10px;
  width: 320px;
  padding: 14px;
  border: 1px solid rgba(96, 183, 255, 0.14);
  border-radius: 18px;
  background: rgba(8, 18, 28, 0.98);
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.36);
}

.coal-nav__extra {
  display: grid;
  gap: 4px;
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(14, 27, 39, 0.94);
  color: #ecf8ff;
  text-align: left;
}

.coal-nav__extra strong {
  font-size: 14px;
}

.coal-nav__extra small {
  color: rgba(218, 236, 252, 0.62);
  line-height: 1.5;
}

.coal-nav__extra.active,
.coal-nav__extra:hover {
  border-color: rgba(87, 216, 255, 0.3);
  background: rgba(20, 41, 58, 0.96);
}

@media (max-width: 1260px) {
  .coal-nav-shell {
    padding: 0 14px;
  }

  .coal-nav {
    align-items: flex-start;
    flex-direction: column;
  }

  .coal-nav__links {
    width: 100%;
  }

  .coal-nav__more {
    width: 100%;
  }

  .coal-nav__panel {
    right: auto;
    left: 0;
    width: min(100%, 360px);
  }

}
</style>
