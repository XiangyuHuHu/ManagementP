package com.example.platform.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/dispatch-log")
public class DispatchLogController {

    private final AtomicLong idGenerator = new AtomicLong(2000);
    private final List<Map<String, Object>> records = new ArrayList<>();

    public DispatchLogController() {
        records.add(record(1L, "启停车记录", "2026-04-14 07:55:00", "白班", "主洗系统开车", "张伟", "已完成"));
        records.add(record(2L, "停送电记录", "2026-04-14 09:40:00", "白班", "浮选机检修停电后恢复送电", "李超", "已完成"));
        records.add(record(3L, "当班记录", "2026-04-14 10:25:00", "白班", "精煤灰分波动，已通知化验复核", "王敏", "处理中"));
        records.add(record(4L, "调度日志", "2026-04-13 21:10:00", "夜班", "原煤仓上给料量调整至 430 吨/小时", "赵凯", "已完成"));
    }

    @GetMapping("/list")
    public List<Map<String, Object>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String shift
    ) {
        return records.stream()
                .filter(item -> category == null || category.isBlank() || category.equals(item.get("category")))
                .filter(item -> shift == null || shift.isBlank() || shift.equals(item.get("shift")))
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
            String category,
            String logTime,
            String shift,
            String content,
            String operator,
            String status
    ) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("category", category);
        map.put("logTime", logTime);
        map.put("shift", shift);
        map.put("content", content);
        map.put("operator", operator);
        map.put("status", status);
        return map;
    }
}
