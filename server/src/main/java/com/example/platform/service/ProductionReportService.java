package com.example.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.ProductionReport;
import java.util.List;
import java.util.Map;

public interface ProductionReportService {

    ProductionReport create(ProductionReport productionReport);
    ProductionReport update(ProductionReport productionReport);
    void delete(Long id);
    ProductionReport getById(Long id);
    IPage<ProductionReport> page(Integer page, Integer size, String reportNo, String shift, String status, String startTime, String endTime);
    List<ProductionReport> list(String reportNo, String shift, String status, String startTime, String endTime);
    ProductionReport getByReportNo(String reportNo);
    List<ProductionReport> getByShift(String shift);
    List<ProductionReport> getByStatus(String status);
    List<ProductionReport> getByTimeRange(String startTime, String endTime);
    List<ProductionReport> getRecent(Integer limit);
    long count();
    long countByStatus(String status);
    List<ProductionReport> findAll();
    ProductionReport findById(Long id);
    ProductionReport findByReportId(String reportId);
    List<ProductionReport> findByReportType(String reportType);
    List<ProductionReport> findByReportDate(String reportDate);
    List<ProductionReport> findByShiftId(String shiftId);
    List<ProductionReport> findByTimeRange(String startDate, String endDate);
    List<ProductionReport> findByStatus(String status);
    double getTodayProduction();
    double getWeekProduction();
    double getMonthProduction();
    Map<String, Object> getTrendData(String timeRange);
}
