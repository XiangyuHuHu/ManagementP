package com.example.platform.service;

import com.example.platform.entity.AlarmRule;
import java.util.List;

public interface AlarmRuleService {

    List<AlarmRule> findAll();
    AlarmRule findById(Long id);
    AlarmRule findByRuleId(String ruleId);
    void create(AlarmRule rule);
    void update(AlarmRule rule);
    void delete(Long id);
    List<AlarmRule> findByDeviceId(String deviceId);
    List<AlarmRule> findByPointId(String pointId);
    List<AlarmRule> findByStatus(String status);
}
