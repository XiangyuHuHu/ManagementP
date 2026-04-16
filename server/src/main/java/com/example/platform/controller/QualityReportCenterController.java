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
@RequestMapping("/api/quality-report-center")
public class QualityReportCenterController {

    @GetMapping("/list")
    public List<Map<String, Object>> list(@RequestParam(required = false) String cycle) {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("日报", "2026-04-14", "精煤质量日报", "已生成", "李敏", "灰分 9.24%，水分 7.10%"));
        rows.add(row("周报", "2026-04-13", "商品煤周报", "待审核", "王超", "周合格率 97.8%"));
        rows.add(row("月报", "2026-04-01", "月度质量分析", "编制中", "张岩", "质量稳定率 95.6%"));
        return rows.stream()
                .filter(item -> cycle == null || cycle.isBlank() || cycle.equals(item.get("cycle")))
                .toList();
    }

    private Map<String, Object> row(
            String cycle,
            String reportDate,
            String reportName,
            String status,
            String owner,
            String summary
    ) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("cycle", cycle);
        map.put("reportDate", reportDate);
        map.put("reportName", reportName);
        map.put("status", status);
        map.put("owner", owner);
        map.put("summary", summary);
        return map;
    }
}
