package com.example.platform.controller;

import com.example.platform.entity.ProductionReport;
import com.example.platform.service.ProductionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/report/production")
public class ProductionReportController {

    @Autowired
    private ProductionReportService productionReportService;

    @GetMapping
    public List<ProductionReport> findAll() {
        return productionReportService.findAll();
    }

    @GetMapping("/{id}")
    public ProductionReport findById(@PathVariable Long id) {
        return productionReportService.findById(id);
    }

    @GetMapping("/reportId/{reportId}")
    public ProductionReport findByReportId(@PathVariable String reportId) {
        return productionReportService.findByReportId(reportId);
    }

    @PostMapping
    public void create(@RequestBody ProductionReport report) {
        productionReportService.create(report);
    }

    @PutMapping
    public void update(@RequestBody ProductionReport report) {
        productionReportService.update(report);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productionReportService.delete(id);
    }

    @GetMapping("/type/{reportType}")
    public List<ProductionReport> findByReportType(@PathVariable String reportType) {
        return productionReportService.findByReportType(reportType);
    }

    @GetMapping("/date/{reportDate}")
    public List<ProductionReport> findByReportDate(@PathVariable String reportDate) {
        return productionReportService.findByReportDate(reportDate);
    }

    @GetMapping("/shift/{shiftId}")
    public List<ProductionReport> findByShiftId(@PathVariable String shiftId) {
        return productionReportService.findByShiftId(shiftId);
    }

    @GetMapping("/timeRange")
    public List<ProductionReport> findByTimeRange(@RequestParam String startDate, @RequestParam String endDate) {
        return productionReportService.findByTimeRange(startDate, endDate);
    }

    @GetMapping("/status/{status}")
    public List<ProductionReport> findByStatus(@PathVariable String status) {
        return productionReportService.findByStatus(status);
    }
}
