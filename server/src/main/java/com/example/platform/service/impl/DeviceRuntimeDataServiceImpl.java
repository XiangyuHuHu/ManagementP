package com.example.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.platform.entity.DeviceRuntimeData;
import com.example.platform.mapper.DeviceRuntimeDataMapper;
import com.example.platform.service.DeviceRuntimeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceRuntimeDataServiceImpl implements DeviceRuntimeDataService {

    @Autowired
    private DeviceRuntimeDataMapper deviceRuntimeDataMapper;

    @Override
    public DeviceRuntimeData create(DeviceRuntimeData deviceRuntimeData) {
        deviceRuntimeData.setCollectionTime(LocalDateTime.now());
        deviceRuntimeDataMapper.insert(deviceRuntimeData);
        return deviceRuntimeData;
    }

    @Override
    public DeviceRuntimeData update(DeviceRuntimeData deviceRuntimeData) {
        deviceRuntimeDataMapper.updateById(deviceRuntimeData);
        return deviceRuntimeData;
    }

    @Override
    public void delete(Long id) {
        deviceRuntimeDataMapper.deleteById(id);
    }

    @Override
    public DeviceRuntimeData getById(Long id) {
        return deviceRuntimeDataMapper.selectById(id);
    }

    @Override
    public IPage<DeviceRuntimeData> page(Integer page, Integer size, Long deviceId, Long pointId, String startTime, String endTime) {
        Page<DeviceRuntimeData> pageInfo = new Page<>(page, size);
        List<DeviceRuntimeData> allData = deviceRuntimeDataMapper.selectList(null);
        
        List<DeviceRuntimeData> filteredData = allData.stream()
                .filter(data -> {
                    boolean match = true;
                    if (deviceId != null && data.getDeviceId() != null && !data.getDeviceId().equals(String.valueOf(deviceId))) match = false;
                    if (pointId != null && data.getPointId() != null && !data.getPointId().equals(String.valueOf(pointId))) match = false;
                    return match;
                })
                .sorted((a, b) -> {
                    if (a.getCollectionTime() == null || b.getCollectionTime() == null) return 0;
                    return b.getCollectionTime().compareTo(a.getCollectionTime());
                })
                .collect(Collectors.toList());
        
        int total = filteredData.size();
        List<DeviceRuntimeData> pageData = filteredData.stream()
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        
        pageInfo.setRecords(pageData);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public List<DeviceRuntimeData> list(Long deviceId, Long pointId, String startTime, String endTime) {
        return deviceRuntimeDataMapper.selectList(null).stream()
                .filter(data -> {
                    boolean match = true;
                    if (deviceId != null && data.getDeviceId() != null && !data.getDeviceId().equals(String.valueOf(deviceId))) match = false;
                    if (pointId != null && data.getPointId() != null && !data.getPointId().equals(String.valueOf(pointId))) match = false;
                    return match;
                })
                .sorted((a, b) -> {
                    if (a.getCollectionTime() == null || b.getCollectionTime() == null) return 0;
                    return b.getCollectionTime().compareTo(a.getCollectionTime());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceRuntimeData> getByDeviceId(Long deviceId) {
        return deviceRuntimeDataMapper.selectList(null).stream()
                .filter(d -> d.getDeviceId() != null && d.getDeviceId().equals(String.valueOf(deviceId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceRuntimeData> getByPointId(Long pointId) {
        return deviceRuntimeDataMapper.selectList(null).stream()
                .filter(d -> d.getPointId() != null && d.getPointId().equals(String.valueOf(pointId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceRuntimeData> getRecent(Integer limit) {
        return deviceRuntimeDataMapper.selectList(null).stream()
                .sorted((a, b) -> {
                    if (a.getCollectionTime() == null || b.getCollectionTime() == null) return 0;
                    return b.getCollectionTime().compareTo(a.getCollectionTime());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceRuntimeData> getLatestData() {
        return getRecent(10);
    }

    @Override
    public List<DeviceRuntimeData> findAll() {
        return deviceRuntimeDataMapper.selectList(null);
    }

    @Override
    public DeviceRuntimeData findById(Long id) {
        return deviceRuntimeDataMapper.selectById(id);
    }

    @Override
    public List<DeviceRuntimeData> findByDeviceId(String deviceId) {
        return deviceRuntimeDataMapper.selectList(null).stream()
                .filter(d -> d.getDeviceId() != null && d.getDeviceId().equals(deviceId))
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceRuntimeData> findByPointId(String pointId) {
        return deviceRuntimeDataMapper.selectList(null).stream()
                .filter(d -> d.getPointId() != null && d.getPointId().equals(pointId))
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceRuntimeData> findByDeviceIdAndPointId(String deviceId, String pointId) {
        return deviceRuntimeDataMapper.selectList(null).stream()
                .filter(d -> d.getDeviceId() != null && d.getDeviceId().equals(deviceId) 
                          && d.getPointId() != null && d.getPointId().equals(pointId))
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceRuntimeData> findLatestByDeviceId(String deviceId) {
        return deviceRuntimeDataMapper.selectList(null).stream()
                .filter(d -> d.getDeviceId() != null && d.getDeviceId().equals(deviceId))
                .sorted((a, b) -> {
                    if (a.getCollectionTime() == null || b.getCollectionTime() == null) return 0;
                    return b.getCollectionTime().compareTo(a.getCollectionTime());
                })
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceRuntimeData> findLatestByPointId(String pointId) {
        return deviceRuntimeDataMapper.selectList(null).stream()
                .filter(d -> d.getPointId() != null && d.getPointId().equals(pointId))
                .sorted((a, b) -> {
                    if (a.getCollectionTime() == null || b.getCollectionTime() == null) return 0;
                    return b.getCollectionTime().compareTo(a.getCollectionTime());
                })
                .limit(10)
                .collect(Collectors.toList());
    }
}
