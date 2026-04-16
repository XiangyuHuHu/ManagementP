package com.example.platform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/production-operation")
public class ProductionOperationController {

    @GetMapping("/list")
    public List<Map<String, Object>> list(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String shift
    ) {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("2026-04-14", "白班", 2860, 2180, 415, 174, 14.5, 92.4, "洗选系统稳定"));
        rows.add(row("2026-04-13", "夜班", 2740, 2105, 398, 166, 14.2, 91.8, "主洗车间短停 20 分钟"));
        rows.add(row("2026-04-13", "白班", 2960, 2220, 445, 178, 14.8, 93.1, "原煤入选稳定"));
        rows.add(row("2026-04-12", "夜班", 2680, 2050, 392, 160, 13.9, 90.7, "精煤灰分略高"));
        rows.add(row("2026-04-12", "白班", 2900, 2180, 419, 181, 14.3, 92.7, "综合指标达标"));
        return rows.stream()
                .filter(item -> date == null || date.isBlank() || date.equals(item.get("statDate")))
                .filter(item -> shift == null || shift.isBlank() || shift.equals(item.get("shift")))
                .toList();
    }

    private Map<String, Object> row(
            String statDate,
            String shift,
            int rawCoal,
            int cleanCoal,
            int middling,
            int gangue,
            double runHours,
            double completionRate,
            String remark
    ) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statDate", statDate);
        map.put("shift", shift);
        map.put("rawCoal", rawCoal);
        map.put("cleanCoal", cleanCoal);
        map.put("middling", middling);
        map.put("gangue", gangue);
        map.put("runHours", runHours);
        map.put("completionRate", completionRate);
        map.put("remark", remark);
        return map;
    }
}
