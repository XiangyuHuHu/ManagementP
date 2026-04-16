package com.example.platform.service;

import com.example.platform.entity.Point;
import java.util.List;

public interface PointService {

    List<Point> findAll();
    Point findById(Long id);
    Point findByPointId(String pointId);
    void create(Point point);
    void update(Point point);
    void delete(Long id);
    List<Point> findByDeviceId(String deviceId);
    List<Point> findByPointType(String pointType);
    List<Point> findByStatus(String status);
}
