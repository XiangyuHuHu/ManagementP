import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getIotRealtimeSnapshot, type IotRealtimeValue } from '../api/iot'

type SubscriptionOptions = {
  pageKey: string
  intervalMs?: number
}

export const useIotStore = defineStore('iot', () => {
  const realtimeMap = ref<Record<string, IotRealtimeValue>>({})
  const generatedAt = ref('')
  const loading = ref(false)
  const lastError = ref('')

  const pollingContext = ref<Record<string, number>>({})
  const pollingTimer = ref<number | null>(null)

  const subscriptionKeys = computed(() => Object.keys(pollingContext.value))

  const activeIntervalMs = computed(() => {
    const list = Object.values(pollingContext.value)
    if (!list.length) return 5000
    return Math.max(1000, Math.min(...list))
  })

  const activePageKey = computed(() => {
    if (!subscriptionKeys.value.length) return 'global'
    return subscriptionKeys.value.slice().sort().join(',')
  })

  async function fetchSnapshot(pageKey = activePageKey.value) {
    loading.value = true
    try {
      const response = await getIotRealtimeSnapshot(pageKey)
      generatedAt.value = response.data.generatedAt
      lastError.value = ''
      const next: Record<string, IotRealtimeValue> = { ...realtimeMap.value }
      response.data.items.forEach((item) => {
        next[item.tagCode] = item
      })
      realtimeMap.value = next
    } catch (error) {
      lastError.value = error instanceof Error ? error.message : 'IOT snapshot failed'
    } finally {
      loading.value = false
    }
  }

  function startPolling() {
    stopPolling()
    void fetchSnapshot()
    pollingTimer.value = window.setInterval(() => {
      void fetchSnapshot()
    }, activeIntervalMs.value)
  }

  function stopPolling() {
    if (pollingTimer.value) {
      clearInterval(pollingTimer.value)
      pollingTimer.value = null
    }
  }

  function subscribe(options: SubscriptionOptions) {
    pollingContext.value = {
      ...pollingContext.value,
      [options.pageKey]: options.intervalMs || 5000,
    }
    startPolling()
  }

  function unsubscribe(pageKey: string) {
    const next = { ...pollingContext.value }
    delete next[pageKey]
    pollingContext.value = next
    if (!Object.keys(next).length) {
      stopPolling()
      return
    }
    startPolling()
  }

  function getTagValue(tagCode: string) {
    return realtimeMap.value[tagCode]
  }

  return {
    realtimeMap,
    generatedAt,
    loading,
    lastError,
    activePageKey,
    subscribe,
    unsubscribe,
    fetchSnapshot,
    getTagValue,
  }
})
