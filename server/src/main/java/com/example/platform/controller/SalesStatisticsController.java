package com.example.platform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sales-statistics")
public class SalesStatisticsController {

    private final List<Map<String, Object>> salesRows = new ArrayList<>();

    public SalesStatisticsController() {
        salesRows.add(row("2026-04-15", "汽运", "电厂", "精煤", 1280, 9.4, 7.6, 98.2, 96.5));
        salesRows.add(row("2026-04-15", "火运", "焦化", "块精煤", 860, 8.9, 7.1, 99.1, 97.8));
        salesRows.add(row("2026-04-14", "汽运", "贸易商", "混煤", 720, 12.8, 8.2, 95.6, 93.4));
        salesRows.add(row("2026-04-14", "火运", "电厂", "末煤", 930, 15.3, 9.4, 94.3, 92.7));
    }

    @GetMapping("/list")
    public List<Map<String, Object>> list(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) String transportMode
    ) {
        return salesRows.stream()
                .filter(item -> date == null || date.isBlank() || date.equals(item.get("statDate")))
                .filter(item -> productType == null || productType.isBlank() || productType.equals(item.get("productType")))
                .filter(item -> transportMode == null || transportMode.isBlank() || transportMode.equals(item.get("transportMode")))
                .collect(Collectors.toList());
    }

    private Map<String, Object> row(String date, String transportMode, String customerType, String productType, Integer quantity,
                                    Double ash, Double moisture, Double qualificationRate, Double stabilityRate) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("statDate", date);
        item.put("transportMode", transportMode);
        item.put("customerType", customerType);
        item.put("productType", productType);
        item.put("quantity", quantity);
        item.put("ash", ash);
        item.put("moisture", moisture);
        item.put("qualificationRate", qualificationRate);
        item.put("stabilityRate", stabilityRate);
        return item;
    }
}
