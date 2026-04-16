package com.example.platform.service.provider;

import java.util.Map;

public interface IotDataProvider {

    String getProviderCode();

    boolean isAvailable();

    Map<String, Object> getTags(String keyword, String deviceCode, String areaCode, Boolean enabled, String sourceType, Integer pageNum, Integer pageSize);

    Object getTag(String tagCode);

    Object getDeviceTags(String deviceCode);

    Object getRealtime(String tagCodes, String deviceCode, String areaCode);

    Object getRealtimeSnapshot(String pageKey);

    Object getHistory(String tagCode, String startTime, String endTime, String interval, String aggregate);

    Object getAlarms(String alarmLevel, String alarmStatus, String deviceCode);

    Object ackAlarm(String alarmId, String operator, String remark);

    Object closeAlarm(String alarmId, String operator, String remark);

    Object getTagMappings();
}
