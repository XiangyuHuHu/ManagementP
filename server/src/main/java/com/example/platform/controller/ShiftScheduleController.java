package com.example.platform.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shift-schedule")
public class ShiftScheduleController {

    private final AtomicLong idGenerator = new AtomicLong(2000);
    private final List<Map<String, Object>> schedules = new ArrayList<>();

    public ShiftScheduleController() {
        schedules.add(schedule(1L, "一班", "早班", "生产班", "06:00", "14:00", true, "2026-04-01", "生效中", "主洗系统生产班"));
        schedules.add(schedule(2L, "二班", "中班", "生产班", "14:00", "22:00", true, "2026-04-01", "生效中", "筛分与压滤联动"));
        schedules.add(schedule(3L, "三班", "夜班", "生产班", "22:00", "06:00", true, "2026-04-01", "生效中", "夜间巡检加强"));
        schedules.add(schedule(4L, "机电班", "白班", "值守班", "08:00", "17:30", false, "2026-04-01", "生效中", "设备保障与检修"));
    }

    @GetMapping("/list")
    public List<Map<String, Object>> list(
            @RequestParam(required = false) String effectiveDate,
            @RequestParam(required = false) String teamName,
            @RequestParam(required = false) String status
    ) {
        return schedules.stream()
                .filter(item -> effectiveDate == null || effectiveDate.isBlank() || effectiveDate.equals(item.get("effectiveDate")))
                .filter(item -> teamName == null || teamName.isBlank() || teamName.equals(item.get("teamName")))
                .filter(item -> status == null || status.isBlank() || status.equals(item.get("status")))
                .collect(Collectors.toList());
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, Object> payload) {
        payload.put("id", idGenerator.incrementAndGet());
        schedules.add(payload);
        return payload;
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody Map<String, Object> payload) {
        Long id = Long.valueOf(String.valueOf(payload.get("id")));
        delete(id);
        schedules.add(payload);
        return payload;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        schedules.removeIf(item -> id.equals(Long.valueOf(String.valueOf(item.get("id")))));
    }

    private Map<String, Object> schedule(Long id, String teamName, String shiftName, String shiftType, String startTime,
                                         String endTime, boolean productionShift, String effectiveDate, String status,
                                         String remark) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("teamName", teamName);
        item.put("shiftName", shiftName);
        item.put("shiftType", shiftType);
        item.put("startTime", startTime);
        item.put("endTime", endTime);
        item.put("productionShift", productionShift);
        item.put("effectiveDate", effectiveDate);
        item.put("status", status);
        item.put("remark", remark);
        return item;
    }
}
