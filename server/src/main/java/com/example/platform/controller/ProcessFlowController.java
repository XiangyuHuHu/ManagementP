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
@RequestMapping("/api/process-flow")
public class ProcessFlowController {

    @GetMapping("/list")
    public List<Map<String, Object>> list(@RequestParam(required = false) String status) {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("原煤仓给料", "入洗段", "正常", "71%", "无", "维持当前给料频率", "GYGD-20260415-001", "2026-04-15 10:30", "已闭环", "运行参数连续 2 小时稳定", "2026-04-15 08:20"));
        rows.add(row("主破碎机", "破碎段", "告警", "93%", "电流接近上限", "建议下调 8% 给料并复核筛前堆煤", "GYGD-20260415-002", "2026-04-15 09:20", "整改中", "已下发调整指令，等待回传", "2026-04-15 08:18"));
        rows.add(row("洗选回路", "分选段", "关注", "84%", "介质密度波动", "检查介质桶补液阀门响应", "GYGD-20260415-003", "2026-04-15 09:40", "待复核", "密度恢复后需复盘班报", "2026-04-15 08:12"));
        rows.add(row("产品仓", "储运段", "正常", "66%", "无", "按既定发运计划执行", "GYGD-20260415-004", "2026-04-15 11:00", "已闭环", "流程节点状态正常", "2026-04-15 08:05"));
        return rows.stream()
                .filter(item -> status == null || status.isBlank() || status.equals(item.get("status")))
                .toList();
    }

    private Map<String, Object> row(
            String nodeName,
            String section,
            String status,
            String load,
            String alarm,
            String suggestion,
            String ticketNo,
            String deadline,
            String closureStatus,
            String reviewConclusion,
            String updateTime
    ) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("nodeName", nodeName);
        map.put("section", section);
        map.put("status", status);
        map.put("load", load);
        map.put("alarm", alarm);
        map.put("suggestion", suggestion);
        map.put("ticketNo", ticketNo);
        map.put("deadline", deadline);
        map.put("closureStatus", closureStatus);
        map.put("reviewConclusion", reviewConclusion);
        map.put("updateTime", updateTime);
        return map;
    }
}
