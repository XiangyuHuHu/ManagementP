package com.example.platform.service.impl;

import com.example.platform.config.IotProperties;
import com.example.platform.dto.iot.IotTagDefinitionDto;
import com.example.platform.dto.iot.IotTagMappingDto;
import com.example.platform.service.IotService;
import com.example.platform.service.IotTagPersistenceService;
import com.example.platform.service.IotTagMappingPersistenceService;
import com.example.platform.service.provider.IotDataProvider;
import com.example.platform.service.provider.KepserverIotDataProvider;
import com.example.platform.service.provider.MockIotDataProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class IotServiceImpl implements IotService {

    private final IotProperties iotProperties;
    private final IotTagPersistenceService iotTagPersistenceService;
    private final IotTagMappingPersistenceService iotTagMappingPersistenceService;
    private final MockIotDataProvider mockIotDataProvider;
    private final KepserverIotDataProvider kepserverIotDataProvider;

    public IotServiceImpl(
            IotProperties iotProperties,
            IotTagPersistenceService iotTagPersistenceService,
            IotTagMappingPersistenceService iotTagMappingPersistenceService,
            MockIotDataProvider mockIotDataProvider,
            KepserverIotDataProvider kepserverIotDataProvider
    ) {
        this.iotProperties = iotProperties;
        this.iotTagPersistenceService = iotTagPersistenceService;
        this.iotTagMappingPersistenceService = iotTagMappingPersistenceService;
        this.mockIotDataProvider = mockIotDataProvider;
        this.kepserverIotDataProvider = kepserverIotDataProvider;
    }

    @PostConstruct
    public void initIotSeeds() {
        seedTags(mockIotDataProvider);
        seedMappings(mockIotDataProvider);
        if (kepserverIotDataProvider.isAvailable()) {
            seedTags(kepserverIotDataProvider);
            seedMappings(kepserverIotDataProvider);
        }
    }

    @Override
    public Map<String, Object> getTags(String keyword, String deviceCode, String areaCode, Boolean enabled, String sourceType, Integer pageNum, Integer pageSize) {
        Map<String, Object> persisted = iotTagPersistenceService.listTags(keyword, deviceCode, areaCode, enabled, sourceType, pageNum, pageSize);
        Object total = persisted.get("total");
        if (total instanceof Integer totalCount && totalCount > 0) {
            return persisted;
        }
        return activeProvider().getTags(keyword, deviceCode, areaCode, enabled, sourceType, pageNum, pageSize);
    }

    @Override
    public Object getTag(String tagCode) {
        IotTagDefinitionDto persisted = iotTagPersistenceService.getTag(tagCode);
        if (persisted != null) {
            return persisted;
        }
        return activeProvider().getTag(tagCode);
    }

    @Override
    public Object getDeviceTags(String deviceCode) {
        List<IotTagDefinitionDto> persisted = iotTagPersistenceService.getDeviceTags(deviceCode);
        if (!persisted.isEmpty()) {
            return persisted;
        }
        return activeProvider().getDeviceTags(deviceCode);
    }

    @Override
    public Object discoverTags(String provider, String keyword, String deviceCode, String areaCode, Boolean enabled, String sourceType, Integer pageNum, Integer pageSize) {
        return resolveProviderForSync(provider).getTags(keyword, deviceCode, areaCode, enabled, sourceType, pageNum, pageSize);
    }

    @Override
    public Object syncTags(String provider) {
        IotDataProvider targetProvider = resolveProviderForSync(provider);
        Map<String, Object> tags = targetProvider.getTags(null, null, null, null, null, 1, 1000);
        Object records = tags.get("records");
        if (!(records instanceof List<?> rawList)) {
            return Map.of("provider", targetProvider.getProviderCode(), "discovered", 0, "synced", 0);
        }

        @SuppressWarnings("unchecked")
        List<IotTagDefinitionDto> dtoList = (List<IotTagDefinitionDto>) rawList;
        iotTagPersistenceService.ensureSeedData(dtoList);
        return Map.of(
                "provider", targetProvider.getProviderCode(),
                "discovered", dtoList.size(),
                "synced", dtoList.size(),
                "available", targetProvider.isAvailable()
        );
    }

    @Override
    public Object createTag(String tagCode, String tagName, String sourceType, String sourcePath, String deviceCode, String deviceName, String areaCode, String dataType, String unit, Integer scanRate, Double deadband, String qualityRule, Boolean enabled, String remark) {
        return iotTagPersistenceService.createTag(new IotTagDefinitionDto(
                tagCode,
                tagName,
                sourceType,
                sourcePath,
                deviceCode,
                deviceName,
                areaCode,
                dataType,
                unit,
                scanRate,
                deadband,
                qualityRule,
                enabled,
                remark
        ));
    }

    @Override
    public Object updateTag(String tagCode, String tagName, String sourceType, String sourcePath, String deviceCode, String deviceName, String areaCode, String dataType, String unit, Integer scanRate, Double deadband, String qualityRule, Boolean enabled, String remark) {
        return iotTagPersistenceService.updateTag(tagCode, new IotTagDefinitionDto(
                tagCode,
                tagName,
                sourceType,
                sourcePath,
                deviceCode,
                deviceName,
                areaCode,
                dataType,
                unit,
                scanRate,
                deadband,
                qualityRule,
                enabled,
                remark
        ));
    }

    @Override
    public Object enableTag(String tagCode) {
        return iotTagPersistenceService.updateEnabled(tagCode, true);
    }

    @Override
    public Object disableTag(String tagCode) {
        return iotTagPersistenceService.updateEnabled(tagCode, false);
    }

    @Override
    public Object deleteTag(String tagCode) {
        iotTagPersistenceService.deleteTag(tagCode);
        return Map.of("tagCode", tagCode, "deleted", true);
    }

    @Override
    public Object getRealtime(String tagCodes, String deviceCode, String areaCode) {
        return activeProvider().getRealtime(tagCodes, deviceCode, areaCode);
    }

    @Override
    public Object getRealtimeSnapshot(String pageKey) {
        return activeProvider().getRealtimeSnapshot(pageKey);
    }

    @Override
    public Object getHistory(String tagCode, String startTime, String endTime, String interval, String aggregate) {
        return activeProvider().getHistory(tagCode, startTime, endTime, interval, aggregate);
    }

    @Override
    public Object getAlarms(String alarmLevel, String alarmStatus, String deviceCode) {
        return activeProvider().getAlarms(alarmLevel, alarmStatus, deviceCode);
    }

    @Override
    public Object ackAlarm(String alarmId, String operator, String remark) {
        return activeProvider().ackAlarm(alarmId, operator, remark);
    }

    @Override
    public Object closeAlarm(String alarmId, String operator, String remark) {
        return activeProvider().closeAlarm(alarmId, operator, remark);
    }

    @Override
    public Object getTagMappings() {
        List<IotTagMappingDto> mappings = iotTagMappingPersistenceService.listMappings();
        if (!mappings.isEmpty()) {
            return mappings;
        }
        Object providerMappings = activeProvider().getTagMappings();
        if (providerMappings instanceof List<?> rawList) {
            @SuppressWarnings("unchecked")
            List<IotTagMappingDto> seedMappings = (List<IotTagMappingDto>) rawList;
            iotTagMappingPersistenceService.ensureSeedData(seedMappings);
            return iotTagMappingPersistenceService.listMappings();
        }
        return providerMappings;
    }

    @Override
    public Object createTagMapping(String mappingId, String tagCode, String businessCode, String businessName, String sourcePath, String transformRule, Boolean enabled, String remark) {
        return iotTagMappingPersistenceService.createMapping(new IotTagMappingDto(
                mappingId,
                tagCode,
                businessCode,
                businessName,
                sourcePath,
                transformRule,
                enabled,
                remark
        ));
    }

    @Override
    public Object updateTagMapping(String mappingId, String tagCode, String businessCode, String businessName, String sourcePath, String transformRule, Boolean enabled, String remark) {
        return iotTagMappingPersistenceService.updateMapping(mappingId, new IotTagMappingDto(
                mappingId,
                tagCode,
                businessCode,
                businessName,
                sourcePath,
                transformRule,
                enabled,
                remark
        ));
    }

    @Override
    public Object enableTagMapping(String mappingId) {
        return iotTagMappingPersistenceService.updateEnabled(mappingId, true);
    }

    @Override
    public Object disableTagMapping(String mappingId) {
        return iotTagMappingPersistenceService.updateEnabled(mappingId, false);
    }

    @Override
    public Object deleteTagMapping(String mappingId) {
        iotTagMappingPersistenceService.deleteMapping(mappingId);
        return Map.of("mappingId", mappingId, "deleted", true);
    }

    @Override
    public Object getProviderStatus() {
        IotDataProvider preferredProvider = resolvePreferredProvider();
        IotDataProvider actualProvider = activeProvider();

        Map<String, Object> result = new HashMap<>();
        result.put("configuredProvider", iotProperties.getProvider());
        result.put("actualProvider", actualProvider.getProviderCode());
        result.put("fallbackToMock", iotProperties.isFallbackToMock());
        result.put("preferredProviderAvailable", preferredProvider.isAvailable());

        Map<String, Object> kepserver = new HashMap<>();
        kepserver.put("enabled", iotProperties.getKepserver().isEnabled());
        kepserver.put("endpoint", iotProperties.getKepserver().getEndpoint());
        kepserver.put("sourceType", iotProperties.getKepserver().getSourceType());
        kepserver.put("channelPrefix", iotProperties.getKepserver().getChannelPrefix());
        kepserver.put("timeoutMs", iotProperties.getKepserver().getTimeoutMs());
        result.put("kepserver", kepserver);

        return result;
    }

    private IotDataProvider activeProvider() {
        IotDataProvider preferredProvider = resolvePreferredProvider();
        if (preferredProvider.isAvailable()) {
            return preferredProvider;
        }
        if (iotProperties.isFallbackToMock()) {
            return mockIotDataProvider;
        }
        return preferredProvider;
    }

    private IotDataProvider resolvePreferredProvider() {
        String provider = iotProperties.getProvider();
        if (provider == null) {
            return mockIotDataProvider;
        }
        return switch (provider.toLowerCase(Locale.ROOT)) {
            case "kepserver" -> kepserverIotDataProvider;
            case "mock" -> mockIotDataProvider;
            default -> mockIotDataProvider;
        };
    }

    private void seedMappings(IotDataProvider provider) {
        Object mappings = provider.getTagMappings();
        if (mappings instanceof List<?> rawList) {
            @SuppressWarnings("unchecked")
            List<IotTagMappingDto> dtoList = (List<IotTagMappingDto>) rawList;
            iotTagMappingPersistenceService.ensureSeedData(dtoList);
        }
    }

    private IotDataProvider resolveProviderForSync(String provider) {
        if (provider == null || provider.isBlank()) {
            return activeProvider();
        }
        return switch (provider.toLowerCase(Locale.ROOT)) {
            case "kepserver" -> {
                if (!kepserverIotDataProvider.isAvailable()) {
                    throw new IllegalArgumentException("provider not available: kepserver");
                }
                yield kepserverIotDataProvider;
            }
            case "mock" -> mockIotDataProvider;
            default -> throw new IllegalArgumentException("unsupported provider: " + provider);
        };
    }

    private void seedTags(IotDataProvider provider) {
        Map<String, Object> tags = provider.getTags(null, null, null, null, null, 1, 1000);
        Object records = tags.get("records");
        if (records instanceof List<?> rawList) {
            @SuppressWarnings("unchecked")
            List<IotTagDefinitionDto> dtoList = (List<IotTagDefinitionDto>) rawList;
            iotTagPersistenceService.ensureSeedData(dtoList);
        }
    }
}
