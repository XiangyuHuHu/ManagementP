package com.example.platform.service.impl;

import com.example.platform.entity.Point;
import com.example.platform.mapper.PointMapper;
import com.example.platform.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private PointMapper pointMapper;

    @Override
    public List<Point> findAll() {
        return pointMapper.findAll();
    }

    @Override
    public Point findById(Long id) {
        return pointMapper.findById(id);
    }

    @Override
    public Point findByPointId(String pointId) {
        return pointMapper.findByPointId(pointId);
    }

    @Override
    public void create(Point point) {
        pointMapper.insert(point);
    }

    @Override
    public void update(Point point) {
        pointMapper.update(point);
    }

    @Override
    public void delete(Long id) {
        pointMapper.delete(id);
    }

    @Override
    public List<Point> findByDeviceId(String deviceId) {
        return pointMapper.findByDeviceId(deviceId);
    }

    @Override
    public List<Point> findByPointType(String pointType) {
        return pointMapper.findByPointType(pointType);
    }

    @Override
    public List<Point> findByStatus(String status) {
        return pointMapper.findByStatus(status);
    }
}
