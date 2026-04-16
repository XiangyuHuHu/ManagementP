package com.example.platform.mapper;

import com.example.platform.entity.AlarmRule;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AlarmRuleMapper {

    List<AlarmRule> findAll();
    AlarmRule findById(Long id);
    AlarmRule findByRuleId(String ruleId);
    void insert(AlarmRule rule);
    void update(AlarmRule rule);
    void delete(Long id);
    List<AlarmRule> findByDeviceId(String deviceId);
    List<AlarmRule> findByPointId(String pointId);
    List<AlarmRule> findByStatus(String status);
}
