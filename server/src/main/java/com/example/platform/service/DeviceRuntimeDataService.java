package com.example.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.DeviceRuntimeData;
import java.util.List;

public interface DeviceRuntimeDataService {

    DeviceRuntimeData create(DeviceRuntimeData deviceRuntimeData);
    DeviceRuntimeData update(DeviceRuntimeData deviceRuntimeData);
    void delete(Long id);
    DeviceRuntimeData getById(Long id);
    IPage<DeviceRuntimeData> page(Integer page, Integer size, Long deviceId, Long pointId, String startTime, String endTime);
    List<DeviceRuntimeData> list(Long deviceId, Long pointId, String startTime, String endTime);
    List<DeviceRuntimeData> getByDeviceId(Long deviceId);
    List<DeviceRuntimeData> getByPointId(Long pointId);
    List<DeviceRuntimeData> getRecent(Integer limit);
    List<DeviceRuntimeData> getLatestData();
    List<DeviceRuntimeData> findAll();
    DeviceRuntimeData findById(Long id);
    List<DeviceRuntimeData> findByDeviceId(String deviceId);
    List<DeviceRuntimeData> findByPointId(String pointId);
    List<DeviceRuntimeData> findByDeviceIdAndPointId(String deviceId, String pointId);
    List<DeviceRuntimeData> findLatestByDeviceId(String deviceId);
    List<DeviceRuntimeData> findLatestByPointId(String pointId);
}
