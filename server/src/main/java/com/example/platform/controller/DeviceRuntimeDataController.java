package com.example.platform.controller;

import com.example.platform.entity.DeviceRuntimeData;
import com.example.platform.service.DeviceRuntimeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/monitor/runtime")
public class DeviceRuntimeDataController {

    @Autowired
    private DeviceRuntimeDataService deviceRuntimeDataService;

    @GetMapping
    public List<DeviceRuntimeData> findAll() {
        return deviceRuntimeDataService.findAll();
    }

    @GetMapping("/{id}")
    public DeviceRuntimeData findById(@PathVariable Long id) {
        return deviceRuntimeDataService.findById(id);
    }

    @PostMapping
    public void create(@RequestBody DeviceRuntimeData data) {
        deviceRuntimeDataService.create(data);
    }

    @PutMapping
    public void update(@RequestBody DeviceRuntimeData data) {
        deviceRuntimeDataService.update(data);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        deviceRuntimeDataService.delete(id);
    }

    @GetMapping("/device/{deviceId}")
    public List<DeviceRuntimeData> findByDeviceId(@PathVariable String deviceId) {
        return deviceRuntimeDataService.findByDeviceId(deviceId);
    }

    @GetMapping("/point/{pointId}")
    public List<DeviceRuntimeData> findByPointId(@PathVariable String pointId) {
        return deviceRuntimeDataService.findByPointId(pointId);
    }

    @GetMapping("/device/{deviceId}/point/{pointId}")
    public List<DeviceRuntimeData> findByDeviceIdAndPointId(@PathVariable String deviceId, @PathVariable String pointId) {
        return deviceRuntimeDataService.findByDeviceIdAndPointId(deviceId, pointId);
    }

    @GetMapping("/device/{deviceId}/latest")
    public List<DeviceRuntimeData> findLatestByDeviceId(@PathVariable String deviceId) {
        return deviceRuntimeDataService.findLatestByDeviceId(deviceId);
    }

    @GetMapping("/point/{pointId}/latest")
    public List<DeviceRuntimeData> findLatestByPointId(@PathVariable String pointId) {
        return deviceRuntimeDataService.findLatestByPointId(pointId);
    }
}
