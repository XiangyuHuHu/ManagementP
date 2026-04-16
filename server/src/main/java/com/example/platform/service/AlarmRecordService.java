package com.example.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.AlarmRecord;
import java.util.List;
import java.util.Map;

public interface AlarmRecordService {

    AlarmRecord create(AlarmRecord alarmRecord);
    AlarmRecord update(AlarmRecord alarmRecord);
    void delete(Long id);
    AlarmRecord getById(Long id);
    IPage<AlarmRecord> page(Integer page, Integer size, String alarmNo, String alarmName, String level, String status, Long deviceId);
    List<AlarmRecord> list(String alarmNo, String alarmName, String level, String status, Long deviceId);
    AlarmRecord getByAlarmNo(String alarmNo);
    List<AlarmRecord> getByDeviceId(Long deviceId);
    List<AlarmRecord> getByLevel(String level);
    List<AlarmRecord> getByStatus(String status);
    List<AlarmRecord> getRecent(Integer limit);
    AlarmRecord process(Long id, String processRemark);
    AlarmRecord close(Long id, String closeRemark);
    long count();
    long countByStatus(String status);
    long countByLevel(String level);
    Map<String, Object> getTrendData(String timeRange);
    List<AlarmRecord> findAll();
    AlarmRecord findById(Long id);
    AlarmRecord findByAlarmId(String alarmId);
    List<AlarmRecord> findByDeviceId(String deviceId);
    List<AlarmRecord> findByPointId(String pointId);
    List<AlarmRecord> findByStatus(String status);
    List<AlarmRecord> findByAlarmLevel(String alarmLevel);
    List<AlarmRecord> findByTimeRange(String startTime, String endTime);
}
