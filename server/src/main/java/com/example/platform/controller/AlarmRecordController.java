package com.example.platform.controller;

import com.example.platform.entity.AlarmRecord;
import com.example.platform.service.AlarmRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alarm/record")
public class AlarmRecordController {

    @Autowired
    private AlarmRecordService alarmRecordService;

    @GetMapping
    public List<AlarmRecord> findAll() {
        return alarmRecordService.findAll();
    }

    @GetMapping("/{id}")
    public AlarmRecord findById(@PathVariable Long id) {
        return alarmRecordService.findById(id);
    }

    @GetMapping("/alarmId/{alarmId}")
    public AlarmRecord findByAlarmId(@PathVariable String alarmId) {
        return alarmRecordService.findByAlarmId(alarmId);
    }

    @PostMapping
    public void create(@RequestBody AlarmRecord record) {
        alarmRecordService.create(record);
    }

    @PutMapping
    public void update(@RequestBody AlarmRecord record) {
        alarmRecordService.update(record);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        alarmRecordService.delete(id);
    }

    @GetMapping("/device/{deviceId}")
    public List<AlarmRecord> findByDeviceId(@PathVariable String deviceId) {
        return alarmRecordService.findByDeviceId(deviceId);
    }

    @GetMapping("/point/{pointId}")
    public List<AlarmRecord> findByPointId(@PathVariable String pointId) {
        return alarmRecordService.findByPointId(pointId);
    }

    @GetMapping("/status/{status}")
    public List<AlarmRecord> findByStatus(@PathVariable String status) {
        return alarmRecordService.findByStatus(status);
    }

    @GetMapping("/level/{alarmLevel}")
    public List<AlarmRecord> findByAlarmLevel(@PathVariable String alarmLevel) {
        return alarmRecordService.findByAlarmLevel(alarmLevel);
    }

    @GetMapping("/timeRange")
    public List<AlarmRecord> findByTimeRange(@RequestParam String startTime, @RequestParam String endTime) {
        return alarmRecordService.findByTimeRange(startTime, endTime);
    }
}
