package com.example.platform.service;

import java.util.Map;

public interface IotService {

    Map<String, Object> getTags(String keyword, String deviceCode, String areaCode, Boolean enabled, String sourceType, Integer pageNum, Integer pageSize);

    Object getTag(String tagCode);

    Object getDeviceTags(String deviceCode);

    Object discoverTags(String provider, String keyword, String deviceCode, String areaCode, Boolean enabled, String sourceType, Integer pageNum, Integer pageSize);

    Object syncTags(String provider);

    Object createTag(String tagCode, String tagName, String sourceType, String sourcePath, String deviceCode, String deviceName, String areaCode, String dataType, String unit, Integer scanRate, Double deadband, String qualityRule, Boolean enabled, String remark);

    Object updateTag(String tagCode, String tagName, String sourceType, String sourcePath, String deviceCode, String deviceName, String areaCode, String dataType, String unit, Integer scanRate, Double deadband, String qualityRule, Boolean enabled, String remark);

    Object enableTag(String tagCode);

    Object disableTag(String tagCode);

    Object deleteTag(String tagCode);

    Object getRealtime(String tagCodes, String deviceCode, String areaCode);

    Object getRealtimeSnapshot(String pageKey);

    Object getHistory(String tagCode, String startTime, String endTime, String interval, String aggregate);

    Object getAlarms(String alarmLevel, String alarmStatus, String deviceCode);

    Object ackAlarm(String alarmId, String operator, String remark);

    Object closeAlarm(String alarmId, String operator, String remark);

    Object getTagMappings();

    Object createTagMapping(String mappingId, String tagCode, String businessCode, String businessName, String sourcePath, String transformRule, Boolean enabled, String remark);

    Object updateTagMapping(String mappingId, String tagCode, String businessCode, String businessName, String sourcePath, String transformRule, Boolean enabled, String remark);

    Object enableTagMapping(String mappingId);

    Object disableTagMapping(String mappingId);

    Object deleteTagMapping(String mappingId);

    Object getProviderStatus();
}
