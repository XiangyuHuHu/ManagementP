package com.example.platform.controller;

import com.example.platform.entity.Point;
import com.example.platform.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/asset/point")
public class PointController {

    @Autowired
    private PointService pointService;

    @GetMapping
    public List<Point> findAll() {
        return pointService.findAll();
    }

    @GetMapping("/{id}")
    public Point findById(@PathVariable Long id) {
        return pointService.findById(id);
    }

    @GetMapping("/pointId/{pointId}")
    public Point findByPointId(@PathVariable String pointId) {
        return pointService.findByPointId(pointId);
    }

    @PostMapping
    public void create(@RequestBody Point point) {
        pointService.create(point);
    }

    @PutMapping
    public void update(@RequestBody Point point) {
        pointService.update(point);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pointService.delete(id);
    }

    @GetMapping("/device/{deviceId}")
    public List<Point> findByDeviceId(@PathVariable String deviceId) {
        return pointService.findByDeviceId(deviceId);
    }

    @GetMapping("/type/{pointType}")
    public List<Point> findByPointType(@PathVariable String pointType) {
        return pointService.findByPointType(pointType);
    }

    @GetMapping("/status/{status}")
    public List<Point> findByStatus(@PathVariable String status) {
        return pointService.findByStatus(status);
    }
}
