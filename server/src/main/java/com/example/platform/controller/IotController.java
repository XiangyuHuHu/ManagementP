package com.example.platform.controller;

import com.example.platform.dto.ApiResponse;
import com.example.platform.dto.iot.IotAlarmActionRequest;
import com.example.platform.dto.iot.IotTagUpsertRequest;
import com.example.platform.dto.iot.IotTagMappingUpsertRequest;
import com.example.platform.service.IotService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/iot")
public class IotController {

    private final IotService iotService;

    public IotController(IotService iotService) {
        this.iotService = iotService;
    }

    @GetMapping("/tags")
    public ApiResponse<Map<String, Object>> getTags(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String deviceCode,
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String sourceType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ApiResponse.success(iotService.getTags(keyword, deviceCode, areaCode, enabled, sourceType, pageNum, pageSize));
    }

    @GetMapping("/tags/{tagCode}")
    public ApiResponse<Object> getTag(@PathVariable String tagCode) {
        return ApiResponse.success(iotService.getTag(tagCode));
    }

    @GetMapping("/devices/{deviceCode}/tags")
    public ApiResponse<Object> getDeviceTags(@PathVariable String deviceCode) {
        return ApiResponse.success(iotService.getDeviceTags(deviceCode));
    }

    @GetMapping("/tags/discovery")
    public ApiResponse<Object> discoverTags(
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String deviceCode,
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String sourceType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return ApiResponse.success(iotService.discoverTags(provider, keyword, deviceCode, areaCode, enabled, sourceType, pageNum, pageSize));
    }

    @PostMapping("/tags/sync")
    public ApiResponse<Object> syncTags(@RequestParam(required = false) String provider) {
        return ApiResponse.success(iotService.syncTags(provider));
    }

    @PostMapping("/tags")
    public ApiResponse<Object> createTag(@RequestBody IotTagUpsertRequest request) {
        return ApiResponse.success(iotService.createTag(
                request.tagCode(),
                request.tagName(),
                request.sourceType(),
                request.sourcePath(),
                request.deviceCode(),
                request.deviceName(),
                request.areaCode(),
                request.dataType(),
                request.unit(),
                request.scanRate(),
                request.deadband(),
                request.qualityRule(),
                request.enabled(),
                request.remark()
        ));
    }

    @PutMapping("/tags/{tagCode}")
    public ApiResponse<Object> updateTag(@PathVariable String tagCode, @RequestBody IotTagUpsertRequest request) {
        return ApiResponse.success(iotService.updateTag(
                tagCode,
                request.tagName(),
                request.sourceType(),
                request.sourcePath(),
                request.deviceCode(),
                request.deviceName(),
                request.areaCode(),
                request.dataType(),
                request.unit(),
                request.scanRate(),
                request.deadband(),
                request.qualityRule(),
                request.enabled(),
                request.remark()
        ));
    }

    @PostMapping("/tags/{tagCode}/enable")
    public ApiResponse<Object> enableTag(@PathVariable String tagCode) {
        return ApiResponse.success(iotService.enableTag(tagCode));
    }

    @PostMapping("/tags/{tagCode}/disable")
    public ApiResponse<Object> disableTag(@PathVariable String tagCode) {
        return ApiResponse.success(iotService.disableTag(tagCode));
    }

    @DeleteMapping("/tags/{tagCode}")
    public ApiResponse<Object> deleteTag(@PathVariable String tagCode) {
        return ApiResponse.success(iotService.deleteTag(tagCode));
    }

    @GetMapping("/realtime")
    public ApiResponse<Object> getRealtime(
            @RequestParam(required = false) String tagCodes,
            @RequestParam(required = false) String deviceCode,
            @RequestParam(required = false) String areaCode
    ) {
        return ApiResponse.success(iotService.getRealtime(tagCodes, deviceCode, areaCode));
    }

    @GetMapping("/realtime/snapshot")
    public ApiResponse<Object> getRealtimeSnapshot(@RequestParam(required = false) String pageKey) {
        return ApiResponse.success(iotService.getRealtimeSnapshot(pageKey));
    }

    @GetMapping("/history")
    public ApiResponse<Object> getHistory(
            @RequestParam String tagCode,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "5m") String interval,
            @RequestParam(defaultValue = "raw") String aggregate
    ) {
        return ApiResponse.success(iotService.getHistory(tagCode, startTime, endTime, interval, aggregate));
    }

    @GetMapping("/alarms")
    public ApiResponse<Object> getAlarms(
            @RequestParam(required = false) String alarmLevel,
            @RequestParam(required = false) String alarmStatus,
            @RequestParam(required = false) String deviceCode
    ) {
        return ApiResponse.success(iotService.getAlarms(alarmLevel, alarmStatus, deviceCode));
    }

    @PostMapping("/alarms/ack")
    public ApiResponse<Object> ackAlarm(@RequestBody IotAlarmActionRequest request) {
        return ApiResponse.success(iotService.ackAlarm(request.alarmId(), request.operator(), request.remark()));
    }

    @PostMapping("/alarms/close")
    public ApiResponse<Object> closeAlarm(@RequestBody IotAlarmActionRequest request) {
        return ApiResponse.success(iotService.closeAlarm(request.alarmId(), request.operator(), request.remark()));
    }

    @GetMapping("/tag-mappings")
    public ApiResponse<Object> getTagMappings() {
        return ApiResponse.success(iotService.getTagMappings());
    }

    @PostMapping("/tag-mappings")
    public ApiResponse<Object> createTagMapping(@RequestBody IotTagMappingUpsertRequest request) {
        return ApiResponse.success(iotService.createTagMapping(
                request.mappingId(),
                request.tagCode(),
                request.businessCode(),
                request.businessName(),
                request.sourcePath(),
                request.transformRule(),
                request.enabled(),
                request.remark()
        ));
    }

    @PutMapping("/tag-mappings/{mappingId}")
    public ApiResponse<Object> updateTagMapping(@PathVariable String mappingId, @RequestBody IotTagMappingUpsertRequest request) {
        return ApiResponse.success(iotService.updateTagMapping(
                mappingId,
                request.tagCode(),
                request.businessCode(),
                request.businessName(),
                request.sourcePath(),
                request.transformRule(),
                request.enabled(),
                request.remark()
        ));
    }

    @PostMapping("/tag-mappings/{mappingId}/enable")
    public ApiResponse<Object> enableTagMapping(@PathVariable String mappingId) {
        return ApiResponse.success(iotService.enableTagMapping(mappingId));
    }

    @PostMapping("/tag-mappings/{mappingId}/disable")
    public ApiResponse<Object> disableTagMapping(@PathVariable String mappingId) {
        return ApiResponse.success(iotService.disableTagMapping(mappingId));
    }

    @DeleteMapping("/tag-mappings/{mappingId}")
    public ApiResponse<Object> deleteTagMapping(@PathVariable String mappingId) {
        return ApiResponse.success(iotService.deleteTagMapping(mappingId));
    }

    @GetMapping("/provider/status")
    public ApiResponse<Object> getProviderStatus() {
        return ApiResponse.success(iotService.getProviderStatus());
    }
}
