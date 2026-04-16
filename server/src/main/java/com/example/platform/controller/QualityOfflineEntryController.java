package com.example.platform.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/quality-offline")
public class QualityOfflineEntryController {

    private final AtomicLong idGenerator = new AtomicLong(1000);
    private final List<Map<String, Object>> records = new ArrayList<>();

    public QualityOfflineEntryController() {
        records.add(record(1L, "QH-20260414-01", "原煤", "原煤仓上", "2026-04-14 08:30:00", 28.42, 7.96, 0.46, 5150, "李敏", "已审核"));
        records.add(record(2L, "QH-20260414-02", "精煤", "精煤皮带", "2026-04-14 10:00:00", 8.54, 8.39, 0.42, 7012, "王超", "已审核"));
        records.add(record(3L, "QH-20260413-03", "洗混", "洗混仓", "2026-04-13 21:15:00", 16.20, 10.14, 0.51, 5620, "张岩", "待复核"));
    }

    @GetMapping("/list")
    public List<Map<String, Object>> list(
            @RequestParam(required = false) String sampleType,
            @RequestParam(required = false) String status
    ) {
        return records.stream()
                .filter(item -> sampleType == null || sampleType.isBlank() || sampleType.equals(item.get("sampleType")))
                .filter(item -> status == null || status.isBlank() || status.equals(item.get("status")))
                .toList();
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, Object> payload) {
        Map<String, Object> row = new LinkedHashMap<>(payload);
        row.put("id", idGenerator.incrementAndGet());
        records.add(0, row);
        return row;
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody Map<String, Object> payload) {
        Long id = ((Number) payload.get("id")).longValue();
        for (int i = 0; i < records.size(); i++) {
            if (id.equals(((Number) records.get(i).get("id")).longValue())) {
                Map<String, Object> row = new LinkedHashMap<>(records.get(i));
                row.putAll(payload);
                records.set(i, row);
                return row;
            }
        }
        return payload;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        records.removeIf(item -> id.equals(((Number) item.get("id")).longValue()));
    }

    private Map<String, Object> record(
            Long id,
            String testNo,
            String sampleType,
            String sampleName,
            String sampleTime,
            double ashContent,
            double moisture,
            double sulfur,
            int calorificValue,
            String tester,
            String status
    ) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("testNo", testNo);
        map.put("sampleType", sampleType);
        map.put("sampleName", sampleName);
        map.put("sampleTime", sampleTime);
        map.put("ashContent", ashContent);
        map.put("moisture", moisture);
        map.put("sulfur", sulfur);
        map.put("calorificValue", calorificValue);
        map.put("tester", tester);
        map.put("status", status);
        return map;
    }
}
