const request = async <T>(path: string): Promise<T> => {
  const response = await fetch(path, { headers: { Accept: 'application/json' } })
  if (!response.ok) throw new Error(`Dashboard request failed: ${response.status}`)
  return response.json()
}

export type DashboardOverview = {
  totalDevices: number
  runningDevices: number
  totalAlarms: number
  unhandledAlarms: number
  pendingWorkOrders: number
}

export const getDashboardOverview = () => request<DashboardOverview>('/api/dashboard/overview')

export const getDashboardProductionStats = () =>
  request<{ todayProduction: number; weekProduction: number; monthProduction: number }>('/api/dashboard/production-stats')

export const getDashboardEnergyStats = () =>
  request<{ todayEnergy: number; weekEnergy: number; monthEnergy: number }>('/api/dashboard/energy-stats')

export const getDashboardRealTimeData = () => request<{ deviceData: unknown[] }>('/api/dashboard/real-time-data')
