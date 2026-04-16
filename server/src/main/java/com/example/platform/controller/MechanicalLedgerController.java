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
@RequestMapping("/api/mechanical-ledger")
public class MechanicalLedgerController {

    @GetMapping("/list")
    public List<Map<String, Object>> list(@RequestParam(required = false) String status) {
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("100扫地泵", "Y225M-6", "离心泵", "在用", "原煤仓上", "滨海金地", "EQR-100"));
        rows.add(row("101带式输送机", "DTII-800", "输送机", "在用", "原煤仓下", "选煤车间", "EQR-101"));
        rows.add(row("311离心机", "LW530", "离心机", "检修", "脱介车间", "机电班", "EQR-311"));
        rows.add(row("503刮板机", "GB-500", "刮板机", "备用", "产品仓上", "机修班", "EQR-503"));
        return rows.stream()
                .filter(item -> status == null || status.isBlank() || status.equals(item.get("status")))
                .toList();
    }

    private Map<String, Object> row(
            String name,
            String model,
            String deviceType,
            String status,
            String location,
            String ownerDept,
            String qrCode
    ) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("model", model);
        map.put("deviceType", deviceType);
        map.put("status", status);
        map.put("location", location);
        map.put("ownerDept", ownerDept);
        map.put("qrCode", qrCode);
        return map;
    }
}
