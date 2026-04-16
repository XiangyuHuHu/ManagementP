package com.example.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.platform.entity.ProductionReport;
import com.example.platform.mapper.ProductionReportMapper;
import com.example.platform.service.ProductionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductionReportServiceImpl implements ProductionReportService {

    @Autowired
    private ProductionReportMapper productionReportMapper;

    @Override
    public ProductionReport create(ProductionReport productionReport) {
        productionReportMapper.insert(productionReport);
        return productionReport;
    }

    @Override
    public ProductionReport update(ProductionReport productionReport) {
        productionReportMapper.updateById(productionReport);
        return productionReport;
    }

    @Override
    public void delete(Long id) {
        productionReportMapper.deleteById(id);
    }

    @Override
    public ProductionReport getById(Long id) {
        return productionReportMapper.selectById(id);
    }

    @Override
    public IPage<ProductionReport> page(Integer page, Integer size, String reportNo, String shift, String status, String startTime, String endTime) {
        Page<ProductionReport> pageInfo = new Page<>(page, size);
        List<ProductionReport> allData = productionReportMapper.selectList(null);
        
        List<ProductionReport> filteredData = allData.stream()
                .filter(report -> {
                    boolean match = true;
                    if (reportNo != null && report.getReportId() != null && !report.getReportId().contains(reportNo)) match = false;
                    if (status != null && report.getStatus() != null && !report.getStatus().equals(status)) match = false;
                    return match;
                })
                .collect(Collectors.toList());
        
        int total = filteredData.size();
        List<ProductionReport> pageData = filteredData.stream()
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        
        pageInfo.setRecords(pageData);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public List<ProductionReport> list(String reportNo, String shift, String status, String startTime, String endTime) {
        return productionReportMapper.selectList(null).stream()
                .filter(report -> {
                    boolean match = true;
                    if (reportNo != null && report.getReportId() != null && !report.getReportId().contains(reportNo)) match = false;
                    if (status != null && report.getStatus() != null && !report.getStatus().equals(status)) match = false;
                    return match;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProductionReport getByReportNo(String reportNo) {
        return productionReportMapper.selectList(null).stream()
                .filter(r -> r.getReportId() != null && r.getReportId().equals(reportNo))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ProductionReport> getByShift(String shift) {
        return productionReportMapper.selectList(null).stream()
                .filter(r -> r.getShiftId() != null && r.getShiftId().equals(shift))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionReport> getByStatus(String status) {
        return productionReportMapper.selectList(null).stream()
                .filter(r -> r.getStatus() != null && r.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionReport> getByTimeRange(String startTime, String endTime) {
        return productionReportMapper.selectList(null).stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionReport> getRecent(Integer limit) {
        return productionReportMapper.selectList(null).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return productionReportMapper.selectList(null).size();
    }

    @Override
    public long countByStatus(String status) {
        return productionReportMapper.selectList(null).stream()
                .filter(r -> r.getStatus() != null && r.getStatus().equals(status))
                .count();
    }

    @Override
    public List<ProductionReport> findAll() {
        return productionReportMapper.selectList(null);
    }

    @Override
    public ProductionReport findById(Long id) {
        return productionReportMapper.selectById(id);
    }

    @Override
    public ProductionReport findByReportId(String reportId) {
        return productionReportMapper.selectList(null).stream()
                .filter(r -> r.getReportId() != null && r.getReportId().equals(reportId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ProductionReport> findByReportType(String reportType) {
        return productionReportMapper.selectList(null).stream()
                .filter(r -> r.getReportType() != null && r.getReportType().equals(reportType))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionReport> findByReportDate(String reportDate) {
        return productionReportMapper.selectList(null).stream()
                .filter(r -> r.getReportDate() != null && r.getReportDate().equals(reportDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionReport> findByShiftId(String shiftId) {
        return productionReportMapper.selectList(null).stream()
                .filter(r -> r.getShiftId() != null && r.getShiftId().equals(shiftId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionReport> findByTimeRange(String startDate, String endDate) {
        return productionReportMapper.selectList(null).stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductionReport> findByStatus(String status) {
        return productionReportMapper.selectList(null).stream()
                .filter(r -> r.getStatus() != null && r.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public double getTodayProduction() {
        return productionReportMapper.selectList(null).stream()
                .mapToDouble(r -> r.getProductionAmount() != null ? r.getProductionAmount() : 0)
                .sum();
    }

    @Override
    public double getWeekProduction() {
        return getTodayProduction() * 7;
    }

    @Override
    public double getMonthProduction() {
        return getTodayProduction() * 30;
    }

    @Override
    public Map<String, Object> getTrendData(String timeRange) {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("data", new java.util.ArrayList<>());
        result.put("labels", new java.util.ArrayList<>());
        return result;
    }
}
