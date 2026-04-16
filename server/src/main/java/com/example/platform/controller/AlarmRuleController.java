package com.example.platform.controller;

import com.example.platform.entity.AlarmRule;
import com.example.platform.service.AlarmRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alarm/rule")
public class AlarmRuleController {

    @Autowired
    private AlarmRuleService alarmRuleService;

    @GetMapping
    public List<AlarmRule> findAll() {
        return alarmRuleService.findAll();
    }

    @GetMapping("/{id}")
    public AlarmRule findById(@PathVariable Long id) {
        return alarmRuleService.findById(id);
    }

    @GetMapping("/ruleId/{ruleId}")
    public AlarmRule findByRuleId(@PathVariable String ruleId) {
        return alarmRuleService.findByRuleId(ruleId);
    }

    @PostMapping
    public void create(@RequestBody AlarmRule rule) {
        alarmRuleService.create(rule);
    }

    @PutMapping
    public void update(@RequestBody AlarmRule rule) {
        alarmRuleService.update(rule);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        alarmRuleService.delete(id);
    }

    @GetMapping("/device/{deviceId}")
    public List<AlarmRule> findByDeviceId(@PathVariable String deviceId) {
        return alarmRuleService.findByDeviceId(deviceId);
    }

    @GetMapping("/point/{pointId}")
    public List<AlarmRule> findByPointId(@PathVariable String pointId) {
        return alarmRuleService.findByPointId(pointId);
    }

    @GetMapping("/status/{status}")
    public List<AlarmRule> findByStatus(@PathVariable String status) {
        return alarmRuleService.findByStatus(status);
    }
}
