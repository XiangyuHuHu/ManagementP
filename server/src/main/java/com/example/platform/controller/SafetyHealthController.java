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
@RequestMapping("/api/safety-health")
public class SafetyHealthController {

    @GetMapping("/list")
    public List<Map<String, Object>> list(@RequestParam(required = false) String level) {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("主洗车间", "粉尘浓度", "高风险", "超阈值", "6.4 mg/m3", "连续 15 分钟高于阈值", "赵强", "AQZG-20260415-001", "2026-04-15 10:00", "整改中", "通风恢复后复测待通过", "2026-04-15 08:20"));
        rows.add(row("精煤仓上", "人员定位", "中风险", "需复核", "2 人滞留", "非作业时段停留超过 10 分钟", "王建", "AQZG-20260415-002", "2026-04-15 09:30", "待复核", "需补充现场说明和轨迹截图", "2026-04-15 08:15"));
        rows.add(row("压滤车间", "噪声强度", "低风险", "已恢复", "79 dB", "短时波动已回落", "李燕", "AQZG-20260415-003", "2026-04-15 12:00", "已闭环", "复测连续 3 次达标", "2026-04-15 08:08"));
        rows.add(row("原煤仓下", "安全帽识别", "高风险", "处理中", "1 次未佩戴", "AI 识别到未佩戴防护装备", "刘刚", "AQZG-20260415-004", "2026-04-15 09:00", "整改中", "已现场教育，待班组复核", "2026-04-15 08:02"));
        return rows.stream()
                .filter(item -> level == null || level.isBlank() || level.equals(item.get("level")))
                .toList();
    }

    private Map<String, Object> row(
            String area,
            String metricType,
            String level,
            String status,
            String value,
            String risk,
            String owner,
            String rectificationNo,
            String deadline,
            String closureStatus,
            String reviewConclusion,
            String updateTime
    ) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("area", area);
        map.put("metricType", metricType);
        map.put("level", level);
        map.put("status", status);
        map.put("value", value);
        map.put("risk", risk);
        map.put("owner", owner);
        map.put("rectificationNo", rectificationNo);
        map.put("deadline", deadline);
        map.put("closureStatus", closureStatus);
        map.put("reviewConclusion", reviewConclusion);
        map.put("updateTime", updateTime);
        return map;
    }
}
