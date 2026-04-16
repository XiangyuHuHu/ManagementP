package com.example.platform.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/production-plan")
public class ProductionPlanController {

    private final AtomicLong idGenerator = new AtomicLong(1000);
    private final List<Map<String, Object>> plans = new ArrayList<>();

    public ProductionPlanController() {
        plans.add(plan(1L, 2026, 4, "2026-04-15", "早班", 2900, 2180, 320, 180, 220, 9.5, 7.8, 12.1, "执行中", "与月计划一致"));
        plans.add(plan(2L, 2026, 4, "2026-04-15", "中班", 3000, 2250, 330, 175, 245, 9.6, 7.7, 12.0, "待执行", "根据原煤波动可调整"));
        plans.add(plan(3L, 2026, 4, "2026-04-15", "夜班", 2800, 2100, 300, 190, 210, 9.7, 7.9, 12.3, "待执行", "注意矸石产量控制"));
    }

    @GetMapping("/list")
    public List<Map<String, Object>> list(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String shift
    ) {
        return plans.stream()
                .filter(item -> year == null || year.equals(item.get("planYear")))
                .filter(item -> month == null || month.equals(item.get("planMonth")))
                .filter(item -> shift == null || shift.isBlank() || shift.equals(item.get("shift")))
                .collect(Collectors.toList());
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, Object> payload) {
        payload.put("id", idGenerator.incrementAndGet());
        plans.add(payload);
        return payload;
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody Map<String, Object> payload) {
        Long id = Long.valueOf(String.valueOf(payload.get("id")));
        delete(id);
        plans.add(payload);
        return payload;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        plans.removeIf(item -> id.equals(Long.valueOf(String.valueOf(item.get("id")))));
    }

    private Map<String, Object> plan(Long id, Integer year, Integer month, String date, String shift, Integer rawCoalTarget,
                                     Integer cleanCoalTarget, Integer middlingTarget, Integer slimeTarget, Integer gangueTarget,
                                     Double ashTarget, Double moistureTarget, Double energyTarget, String status, String remark) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("planYear", year);
        item.put("planMonth", month);
        item.put("planDate", date);
        item.put("shift", shift);
        item.put("rawCoalTarget", rawCoalTarget);
        item.put("cleanCoalTarget", cleanCoalTarget);
        item.put("middlingTarget", middlingTarget);
        item.put("slimeTarget", slimeTarget);
        item.put("gangueTarget", gangueTarget);
        item.put("ashTarget", ashTarget);
        item.put("moistureTarget", moistureTarget);
        item.put("energyTarget", energyTarget);
        item.put("status", status);
        item.put("remark", remark);
        return item;
    }
}
