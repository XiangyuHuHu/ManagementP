package com.example.platform.service.impl;

import com.example.platform.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    private AssetDeviceService assetDeviceService;

    @Autowired
    private DeviceRuntimeDataService deviceRuntimeDataService;

    @Autowired
    private AlarmRecordService alarmRecordService;

    @Autowired
    private ProductionReportService productionReportService;

    @Autowired
    private WorkOrderService workOrderService;

    @Autowired
    private EnergyConsumptionService energyConsumptionService;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        try {
            long totalDevices = assetDeviceService.count();
            overview.put("totalDevices", totalDevices);
            long runningDevices = assetDeviceService.countByStatus("运行中");
            overview.put("runningDevices", runningDevices);
            long totalAlarms = alarmRecordService.count();
            overview.put("totalAlarms", totalAlarms);
            long unhandledAlarms = alarmRecordService.countByStatus("未处理");
            overview.put("unhandledAlarms", unhandledAlarms);
            long pendingWorkOrders = workOrderService.getPending().size();
            overview.put("pendingWorkOrders", pendingWorkOrders);
        } catch (Exception e) {
            log.warn("看板概况查询失败（库表未齐时返回 0）: {}", e.getMessage());
            overview.put("totalDevices", 0L);
            overview.put("runningDevices", 0L);
            overview.put("totalAlarms", 0L);
            overview.put("unhandledAlarms", 0L);
            overview.put("pendingWorkOrders", 0L);
        }
        return overview;
    }

    @Override
    public Map<String, Object> getDeviceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 设备状态分布
        Map<String, Long> statusDistribution = new HashMap<>();
        statusDistribution.put("运行中", assetDeviceService.countByStatus("运行中"));
        statusDistribution.put("待机", assetDeviceService.countByStatus("待机"));
        statusDistribution.put("故障", assetDeviceService.countByStatus("故障"));
        statusDistribution.put("维护", assetDeviceService.countByStatus("维护"));
        stats.put("statusDistribution", statusDistribution);
        
        // 设备类型分布
        Map<String, Long> typeDistribution = assetDeviceService.countByType();
        stats.put("typeDistribution", typeDistribution);
        
        return stats;
    }

    @Override
    public Map<String, Object> getAlarmStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 报警级别分布
        Map<String, Long> levelDistribution = new HashMap<>();
        levelDistribution.put("紧急", alarmRecordService.countByLevel("紧急"));
        levelDistribution.put("重要", alarmRecordService.countByLevel("重要"));
        levelDistribution.put("一般", alarmRecordService.countByLevel("一般"));
        stats.put("levelDistribution", levelDistribution);
        
        // 报警状态分布
        Map<String, Long> statusDistribution = new HashMap<>();
        statusDistribution.put("未处理", alarmRecordService.countByStatus("未处理"));
        statusDistribution.put("处理中", alarmRecordService.countByStatus("处理中"));
        statusDistribution.put("已处理", alarmRecordService.countByStatus("已处理"));
        stats.put("statusDistribution", statusDistribution);
        
        return stats;
    }

    @Override
    public Map<String, Object> getProductionStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 今日产量
        double todayProduction = productionReportService.getTodayProduction();
        stats.put("todayProduction", todayProduction);
        
        // 本周产量
        double weekProduction = productionReportService.getWeekProduction();
        stats.put("weekProduction", weekProduction);
        
        // 本月产量
        double monthProduction = productionReportService.getMonthProduction();
        stats.put("monthProduction", monthProduction);
        
        return stats;
    }

    @Override
    public Map<String, Object> getEnergyStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate monthStart = today.with(TemporalAdjusters.firstDayOfMonth());
            stats.put("todayEnergy", energyConsumptionService.sumConsumptionBetween(today, today));
            stats.put("weekEnergy", energyConsumptionService.sumConsumptionBetween(weekStart, today));
            stats.put("monthEnergy", energyConsumptionService.sumConsumptionBetween(monthStart, today));
        } catch (Exception e) {
            log.warn("能耗汇总查询失败（表未建或库未就绪时返回 0）: {}", e.getMessage());
            stats.put("todayEnergy", 0.0);
            stats.put("weekEnergy", 0.0);
            stats.put("monthEnergy", 0.0);
        }
        return stats;
    }

    @Override
    public Map<String, Object> getRecentAlarms(Integer limit) {
        Map<String, Object> result = new HashMap<>();
        result.put("alarms", alarmRecordService.getRecent(limit));
        return result;
    }

    @Override
    public Map<String, Object> getRecentWorkOrders(Integer limit) {
        Map<String, Object> result = new HashMap<>();
        result.put("workOrders", workOrderService.getRecent(limit));
        return result;
    }

    @Override
    public Map<String, Object> getRealTimeData() {
        Map<String, Object> result = new HashMap<>();
        result.put("deviceData", deviceRuntimeDataService.getLatestData());
        return result;
    }

    @Override
    public Map<String, Object> getTrendData(String type, String timeRange) {
        Map<String, Object> result = new HashMap<>();
        
        // 根据类型和时间范围获取趋势数据
        if ("production".equals(type)) {
            result.put("trend", productionReportService.getTrendData(timeRange));
        } else if ("alarm".equals(type)) {
            result.put("trend", alarmRecordService.getTrendData(timeRange));
        } else if ("energy".equals(type)) {
            // 能源趋势数据
            result.put("trend", new HashMap<>());
        }
        
        return result;
    }
}
