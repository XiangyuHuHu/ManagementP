export interface DispatchRecordDto {
  id?: number
  recordNo: string
  shift: string
  recordType: string
  content: string
  handling?: string
  recorder?: string
  recordTime: string
  handler?: string
  handleTime?: string
  status: string
  priority: string
  attachmentUrl?: string
}

export interface EnergyConsumptionDto {
  id?: number
  recordDate: string
  shift?: string
  energyType: string
  consumption: number
  unit: string
  unitPrice: number
  cost: number
  relatedOutput: number
  unitConsumption: number
  meterReading?: number
  meterNo?: string
  recorder?: string
  dataSource?: string
  remark?: string
}

export interface StorageTransportDto {
  id?: number
  recordType: string
  recordNo: string
  coalType: string
  quantity: number
  recordTime: string
  sourceOrDest?: string
  transportMode: string
  vehicleNo?: string
  loadingStation?: string
  trainNo?: string
  carriageCount?: number
  customerName?: string
  contractNo?: string
  qualityIndex?: string
  operator?: string
  status?: string
  remark?: string
}

export interface ProductionReportDto {
  id?: number
  reportId: string
  reportType: string
  reportDate: string
  shiftId?: string
  productionAmount?: number
  productionRate?: number
  equipmentAvailability?: number
  energyConsumption?: number
  materialConsumption?: number
  operator?: string
  remark?: string
  status?: string
}

export interface CoalQualityDto {
  id?: number
  testNo: string
  sampleType: string
  sampleName: string
  sampleTime?: string
  shift?: string
  systemName?: string
  ashContent?: number
  moisture?: number
  sulfur?: number
  calorificValue?: number
  volatileMatter?: number
  fixedCarbon?: number
  density?: number
  particleSize?: string
  tester?: string
  testTime?: string
  dataSource?: string
  remark?: string
}

export interface AssetDeviceDto {
  id?: number
  deviceId: string
  name: string
  model?: string
  specification?: string
  manufacturer?: string
  supplierId?: string
  purchaseDate?: string
  installDate?: string
  commissioningDate?: string
  status?: string
  locationId?: string
  categoryId?: string
  description?: string
  technicalParameters?: string
  serialNumber?: string
  assetNumber?: string
  responsiblePerson?: string
  maintenancePerson?: string
}

export interface ProductionPlanDto {
  id?: number
  planYear: number
  planMonth?: number
  planDate?: string
  shift?: string
  rawCoalTarget?: number
  cleanCoalTarget?: number
  middlingTarget?: number
  slimeTarget?: number
  gangueTarget?: number
  ashTarget?: number
  moistureTarget?: number
  energyTarget?: number
  status?: string
  remark?: string
}

export interface SalesStatisticDto {
  statDate: string
  transportMode: string
  customerType: string
  productType: string
  quantity: number
  ash?: number
  moisture?: number
  qualificationRate?: number
  stabilityRate?: number
}

export interface ShiftScheduleDto {
  id?: number
  teamName: string
  shiftName: string
  shiftType: string
  startTime: string
  endTime: string
  productionShift: boolean
  effectiveDate: string
  status: string
  remark?: string
}

export interface ProductionOperationDto {
  statDate: string
  shift: string
  rawCoal: number
  cleanCoal: number
  middling: number
  gangue: number
  runHours: number
  completionRate: number
  remark?: string
}

export interface QualityOfflineDto {
  id?: number
  testNo: string
  sampleType: string
  sampleName: string
  sampleTime: string
  ashContent: number
  moisture: number
  sulfur: number
  calorificValue: number
  tester: string
  status: string
}

export interface DispatchLogDto {
  id?: number
  category: string
  logTime: string
  shift: string
  content: string
  operator: string
  status: string
}

export interface ModelAnalysisDto {
  modelName: string
  status: string
  category: string
  conclusion: string
  suggestion: string
  confidence: string
}

export interface QualityReportCenterDto {
  cycle: string
  reportDate: string
  reportName: string
  status: string
  owner: string
  summary: string
}

export interface MechanicalLedgerDto {
  name: string
  model: string
  deviceType: string
  status: string
  location: string
  ownerDept: string
  qrCode: string
}

export interface SafetyHealthDto {
  area: string
  metricType: string
  level: string
  status: string
  value: string
  risk: string
  owner: string
  rectificationNo: string
  deadline: string
  closureStatus: string
  reviewConclusion: string
  updateTime: string
}

export interface ProcessFlowDto {
  nodeName: string
  section: string
  status: string
  load: string
  alarm: string
  suggestion: string
  ticketNo: string
  deadline: string
  closureStatus: string
  reviewConclusion: string
  updateTime: string
}

const buildQuery = (params: Record<string, string | number | boolean | undefined | null>) => {
  const query = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      query.set(key, String(value))
    }
  })
  const queryString = query.toString()
  return queryString ? `?${queryString}` : ''
}

const request = async <T>(path: string, init?: RequestInit): Promise<T> => {
  const response = await fetch(path, {
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers || {}),
    },
    ...init,
  })

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`)
  }

  return response.json()
}

export const listDispatchRecords = (params: {
  shift?: string
  recordType?: string
  status?: string
  date?: string
} = {}) => request<DispatchRecordDto[]>(`/api/dispatch-record/list${buildQuery(params)}`)

export const createDispatchRecord = (payload: DispatchRecordDto) =>
  request<DispatchRecordDto>('/api/dispatch-record', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const updateDispatchRecord = (payload: DispatchRecordDto) =>
  request<DispatchRecordDto>('/api/dispatch-record', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })

export const deleteDispatchRecord = (id: number) =>
  request<void>(`/api/dispatch-record/${id}`, {
    method: 'DELETE',
  })

export const listEnergyConsumptions = (params: {
  energyType?: string
  shift?: string
  date?: string
} = {}) => request<EnergyConsumptionDto[]>(`/api/energy-consumption/list${buildQuery(params)}`)

export const createEnergyConsumption = (payload: EnergyConsumptionDto) =>
  request<EnergyConsumptionDto>('/api/energy-consumption', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const updateEnergyConsumption = (payload: EnergyConsumptionDto) =>
  request<EnergyConsumptionDto>('/api/energy-consumption', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })

export const deleteEnergyConsumption = (id: number) =>
  request<void>(`/api/energy-consumption/${id}`, {
    method: 'DELETE',
  })

export const listStorageTransports = (params: {
  recordType?: string
  transportMode?: string
  status?: string
  date?: string
} = {}) => request<StorageTransportDto[]>(`/api/storage-transport/list${buildQuery(params)}`)

export const createStorageTransport = (payload: StorageTransportDto) =>
  request<StorageTransportDto>('/api/storage-transport', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const updateStorageTransport = (payload: StorageTransportDto) =>
  request<StorageTransportDto>('/api/storage-transport', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })

export const deleteStorageTransport = (id: number) =>
  request<void>(`/api/storage-transport/${id}`, {
    method: 'DELETE',
  })

export const listProductionReports = () => request<ProductionReportDto[]>('/api/report/production')

export const createProductionReport = (payload: ProductionReportDto) =>
  request<void>('/api/report/production', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const updateProductionReport = (payload: ProductionReportDto) =>
  request<void>('/api/report/production', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })

export const deleteProductionReport = (id: number) =>
  request<void>(`/api/report/production/${id}`, {
    method: 'DELETE',
  })

export const listCoalQuality = (params: { sampleType?: string; date?: string } = {}) =>
  request<CoalQualityDto[]>(`/api/coal-quality/list${buildQuery(params)}`)

export const createCoalQuality = (payload: CoalQualityDto) =>
  request<CoalQualityDto>('/api/coal-quality', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const updateCoalQuality = (payload: CoalQualityDto) =>
  request<CoalQualityDto>('/api/coal-quality', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })

export const deleteCoalQuality = (id: number) =>
  request<void>(`/api/coal-quality/${id}`, {
    method: 'DELETE',
  })

export const listAssetDevices = () => request<AssetDeviceDto[]>('/api/asset/device')

export const listProductionPlans = (params: {
  year?: number
  month?: number
  shift?: string
} = {}) => request<ProductionPlanDto[]>(`/api/production-plan/list${buildQuery(params)}`)

export const createProductionPlan = (payload: ProductionPlanDto) =>
  request<ProductionPlanDto>('/api/production-plan', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const updateProductionPlan = (payload: ProductionPlanDto) =>
  request<ProductionPlanDto>('/api/production-plan', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })

export const deleteProductionPlan = (id: number) =>
  request<void>(`/api/production-plan/${id}`, {
    method: 'DELETE',
  })

export const listSalesStatistics = (params: {
  date?: string
  productType?: string
  transportMode?: string
} = {}) => request<SalesStatisticDto[]>(`/api/sales-statistics/list${buildQuery(params)}`)

export const listShiftSchedules = (params: {
  effectiveDate?: string
  teamName?: string
  status?: string
} = {}) => request<ShiftScheduleDto[]>(`/api/shift-schedule/list${buildQuery(params)}`)

export const createShiftSchedule = (payload: ShiftScheduleDto) =>
  request<ShiftScheduleDto>('/api/shift-schedule', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const updateShiftSchedule = (payload: ShiftScheduleDto) =>
  request<ShiftScheduleDto>('/api/shift-schedule', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })

export const deleteShiftSchedule = (id: number) =>
  request<void>(`/api/shift-schedule/${id}`, {
    method: 'DELETE',
  })

export const listProductionOperations = (params: {
  date?: string
  shift?: string
} = {}) => request<ProductionOperationDto[]>(`/api/production-operation/list${buildQuery(params)}`)

export const listQualityOffline = (params: {
  sampleType?: string
  status?: string
} = {}) => request<QualityOfflineDto[]>(`/api/quality-offline/list${buildQuery(params)}`)

export const createQualityOffline = (payload: QualityOfflineDto) =>
  request<QualityOfflineDto>('/api/quality-offline', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const updateQualityOffline = (payload: QualityOfflineDto) =>
  request<QualityOfflineDto>('/api/quality-offline', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })

export const deleteQualityOffline = (id: number) =>
  request<void>(`/api/quality-offline/${id}`, {
    method: 'DELETE',
  })

export const listDispatchLogs = (params: {
  category?: string
  shift?: string
} = {}) => request<DispatchLogDto[]>(`/api/dispatch-log/list${buildQuery(params)}`)

export const createDispatchLog = (payload: DispatchLogDto) =>
  request<DispatchLogDto>('/api/dispatch-log', {
    method: 'POST',
    body: JSON.stringify(payload),
  })

export const updateDispatchLog = (payload: DispatchLogDto) =>
  request<DispatchLogDto>('/api/dispatch-log', {
    method: 'PUT',
    body: JSON.stringify(payload),
  })

export const deleteDispatchLog = (id: number) =>
  request<void>(`/api/dispatch-log/${id}`, {
    method: 'DELETE',
  })

export const listModelAnalyses = () =>
  request<ModelAnalysisDto[]>('/api/model-analysis/list')

export const listQualityReportCenter = (params: {
  cycle?: string
} = {}) => request<QualityReportCenterDto[]>(`/api/quality-report-center/list${buildQuery(params)}`)

export const listMechanicalLedgers = (params: {
  status?: string
} = {}) => request<MechanicalLedgerDto[]>(`/api/mechanical-ledger/list${buildQuery(params)}`)

export const listSafetyHealth = (params: {
  level?: string
} = {}) => request<SafetyHealthDto[]>(`/api/safety-health/list${buildQuery(params)}`)

export const listProcessFlow = (params: {
  status?: string
} = {}) => request<ProcessFlowDto[]>(`/api/process-flow/list${buildQuery(params)}`)
