package com.example.platform.service.impl;

import com.example.platform.entity.AlarmRule;
import com.example.platform.mapper.AlarmRuleMapper;
import com.example.platform.service.AlarmRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlarmRuleServiceImpl implements AlarmRuleService {

    @Autowired
    private AlarmRuleMapper alarmRuleMapper;

    @Override
    public List<AlarmRule> findAll() {
        return alarmRuleMapper.findAll();
    }

    @Override
    public AlarmRule findById(Long id) {
        return alarmRuleMapper.findById(id);
    }

    @Override
    public AlarmRule findByRuleId(String ruleId) {
        return alarmRuleMapper.findByRuleId(ruleId);
    }

    @Override
    public void create(AlarmRule rule) {
        alarmRuleMapper.insert(rule);
    }

    @Override
    public void update(AlarmRule rule) {
        alarmRuleMapper.update(rule);
    }

    @Override
    public void delete(Long id) {
        alarmRuleMapper.delete(id);
    }

    @Override
    public List<AlarmRule> findByDeviceId(String deviceId) {
        return alarmRuleMapper.findByDeviceId(deviceId);
    }

    @Override
    public List<AlarmRule> findByPointId(String pointId) {
        return alarmRuleMapper.findByPointId(pointId);
    }

    @Override
    public List<AlarmRule> findByStatus(String status) {
        return alarmRuleMapper.findByStatus(status);
    }
}
