package com.example.platform.mapper;

import com.example.platform.entity.Point;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PointMapper {

    List<Point> findAll();
    Point findById(Long id);
    Point findByPointId(String pointId);
    void insert(Point point);
    void update(Point point);
    void delete(Long id);
    List<Point> findByDeviceId(String deviceId);
    List<Point> findByPointType(String pointType);
    List<Point> findByStatus(String status);
}
