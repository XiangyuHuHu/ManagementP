package com.example.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.platform.entity.AlarmRecord;
import com.example.platform.mapper.AlarmRecordMapper;
import com.example.platform.service.AlarmRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlarmRecordServiceImpl implements AlarmRecordService {

    @Autowired
    private AlarmRecordMapper alarmRecordMapper;

    @Override
    public AlarmRecord create(AlarmRecord alarmRecord) {
        alarmRecordMapper.insert(alarmRecord);
        return alarmRecord;
    }

    @Override
    public AlarmRecord update(AlarmRecord alarmRecord) {
        alarmRecordMapper.updateById(alarmRecord);
        return alarmRecord;
    }

    @Override
    public void delete(Long id) {
        alarmRecordMapper.deleteById(id);
    }

    @Override
    public AlarmRecord getById(Long id) {
        return alarmRecordMapper.selectById(id);
    }

    @Override
    public IPage<AlarmRecord> page(Integer page, Integer size, String alarmNo, String alarmName, String level, String status, Long deviceId) {
        Page<AlarmRecord> pageInfo = new Page<>(page, size);
        List<AlarmRecord> allData = alarmRecordMapper.selectList(null);
        
        List<AlarmRecord> filteredData = allData.stream()
                .filter(record -> {
                    boolean match = true;
                    if (alarmNo != null && record.getAlarmId() != null && !record.getAlarmId().contains(alarmNo)) match = false;
                    if (alarmName != null && record.getAlarmMessage() != null && !record.getAlarmMessage().contains(alarmName)) match = false;
                    if (level != null && record.getAlarmLevel() != null && !record.getAlarmLevel().equals(level)) match = false;
                    if (status != null && record.getStatus() != null && !record.getStatus().equals(status)) match = false;
                    return match;
                })
                .collect(Collectors.toList());
        
        int total = filteredData.size();
        List<AlarmRecord> pageData = filteredData.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
        pageInfo.setRecords(pageData);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public List<AlarmRecord> list(String alarmNo, String alarmName, String level, String status, Long deviceId) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(record -> {
                    boolean match = true;
                    if (alarmNo != null && record.getAlarmId() != null && !record.getAlarmId().contains(alarmNo)) match = false;
                    if (alarmName != null && record.getAlarmMessage() != null && !record.getAlarmMessage().contains(alarmName)) match = false;
                    if (level != null && record.getAlarmLevel() != null && !record.getAlarmLevel().equals(level)) match = false;
                    if (status != null && record.getStatus() != null && !record.getStatus().equals(status)) match = false;
                    return match;
                })
                .collect(Collectors.toList());
    }

    @Override
    public AlarmRecord getByAlarmNo(String alarmNo) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getAlarmId() != null && r.getAlarmId().equals(alarmNo))
                .findFirst().orElse(null);
    }

    @Override
    public List<AlarmRecord> getByDeviceId(Long deviceId) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getDeviceId() != null && r.getDeviceId().equals(String.valueOf(deviceId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<AlarmRecord> getByLevel(String level) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getAlarmLevel() != null && r.getAlarmLevel().equals(level))
                .collect(Collectors.toList());
    }

    @Override
    public List<AlarmRecord> getByStatus(String status) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getStatus() != null && r.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<AlarmRecord> getRecent(Integer limit) {
        return alarmRecordMapper.selectList(null).stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public AlarmRecord process(Long id, String processRemark) {
        AlarmRecord alarmRecord = alarmRecordMapper.selectById(id);
        if (alarmRecord != null) {
            alarmRecord.setStatus("处理中");
            alarmRecord.setHandleTime(new Date().toString());
            alarmRecord.setHandleResult(processRemark);
            alarmRecordMapper.updateById(alarmRecord);
        }
        return alarmRecord;
    }

    @Override
    public AlarmRecord close(Long id, String closeRemark) {
        AlarmRecord alarmRecord = alarmRecordMapper.selectById(id);
        if (alarmRecord != null) {
            alarmRecord.setStatus("已处理");
            alarmRecord.setHandleResult(closeRemark);
            alarmRecordMapper.updateById(alarmRecord);
        }
        return alarmRecord;
    }

    @Override
    public long count() {
        return alarmRecordMapper.selectList(null).size();
    }

    @Override
    public long countByStatus(String status) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getStatus() != null && r.getStatus().equals(status)).count();
    }

    @Override
    public long countByLevel(String level) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getAlarmLevel() != null && r.getAlarmLevel().equals(level)).count();
    }

    @Override
    public Map<String, Object> getTrendData(String timeRange) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", new ArrayList<>());
        result.put("labels", new ArrayList<>());
        return result;
    }

    @Override
    public List<AlarmRecord> findAll() {
        return alarmRecordMapper.selectList(null);
    }

    @Override
    public AlarmRecord findById(Long id) {
        return alarmRecordMapper.selectById(id);
    }

    @Override
    public AlarmRecord findByAlarmId(String alarmId) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getAlarmId() != null && r.getAlarmId().equals(alarmId))
                .findFirst().orElse(null);
    }

    @Override
    public List<AlarmRecord> findByDeviceId(String deviceId) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getDeviceId() != null && r.getDeviceId().equals(deviceId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AlarmRecord> findByPointId(String pointId) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getPointId() != null && r.getPointId().equals(pointId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AlarmRecord> findByStatus(String status) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getStatus() != null && r.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<AlarmRecord> findByAlarmLevel(String alarmLevel) {
        return alarmRecordMapper.selectList(null).stream()
                .filter(r -> r.getAlarmLevel() != null && r.getAlarmLevel().equals(alarmLevel))
                .collect(Collectors.toList());
    }

    @Override
    public List<AlarmRecord> findByTimeRange(String startTime, String endTime) {
        return alarmRecordMapper.selectList(null).stream().collect(Collectors.toList());
    }
}
