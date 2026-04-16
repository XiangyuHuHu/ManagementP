package com.example.platform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/model-analysis")
public class ModelAnalysisController {

    @GetMapping("/list")
    public List<Map<String, Object>> list() {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("产品质量稳定控制模型", "运行中", "质量预测", "精煤灰分预测偏差 0.22%", "建议维持介质密度 1.43", "92.8%"));
        rows.add(row("精煤产品质量预测", "运行中", "质量预测", "未来 4 小时灰分将维持 9.1%~9.4%", "关注夜班原煤波动", "90.4%"));
        rows.add(row("质量变化趋势分析", "待复核", "趋势分析", "洗混水分波动偏高", "建议复核脱介筛前喷淋", "84.6%"));
        rows.add(row("产品仓质量分析", "运行中", "仓储分析", "503 块精煤仓质量稳定", "可直接进入发运队列", "95.1%"));
        return rows;
    }

    private Map<String, Object> row(
            String modelName,
            String status,
            String category,
            String conclusion,
            String suggestion,
            String confidence
    ) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("modelName", modelName);
        map.put("status", status);
        map.put("category", category);
        map.put("conclusion", conclusion);
        map.put("suggestion", suggestion);
        map.put("confidence", confidence);
        return map;
    }
}
