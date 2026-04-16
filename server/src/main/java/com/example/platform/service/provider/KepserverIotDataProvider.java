package com.example.platform.service.provider;

import com.example.platform.config.IotProperties;
import com.example.platform.dto.iot.*;
import com.example.platform.entity.IotHistoryEntity;
import com.example.platform.entity.IotRealtimeEntity;
import com.example.platform.entity.IotTagEntity;
import com.example.platform.repository.IotHistoryRepository;
import com.example.platform.repository.IotRealtimeRepository;
import com.example.platform.repository.IotTagRepository;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class KepserverIotDataProvider implements IotDataProvider {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final IotProperties iotProperties;
    private final IotTagRepository tagRepo;
    private final IotRealtimeRepository realtimeRepo;
    private final IotHistoryRepository historyRepo;

    public KepserverIotDataProvider(
            IotProperties iotProperties,
            IotTagRepository tagRepo,
            IotRealtimeRepository realtimeRepo,
            IotHistoryRepository historyRepo
    ) {
        this.iotProperties = iotProperties;
        this.tagRepo = tagRepo;
        this.realtimeRepo = realtimeRepo;
        this.historyRepo = historyRepo;
    }

    @Override
    public String getProviderCode() {
        return "kepserver";
    }

    @Override
    public boolean isAvailable() {
        IotProperties.Kepserver config = iotProperties.getKepserver();
        return config.isEnabled() && !isBlank(config.getEndpoint());
    }

    @Override
    public Map<String, Object> getTags(String keyword, String deviceCode, String areaCode, Boolean enabled, String sourceType, Integer pageNum, Integer pageSize) {
        List<IotTagEntity> allTags = tagRepo.findAll();
        List<IotTagDefinitionDto> records = allTags.stream()
                .filter(t -> isBlank(keyword) || containsIgnoreCase(t.getTagCode(), keyword) || containsIgnoreCase(t.getTagName(), keyword))
                .filter(t -> isBlank(deviceCode) || Objects.equals(t.getDeviceCode(), deviceCode))
                .filter(t -> isBlank(areaCode) || Objects.equals(t.getAreaCode(), areaCode))
                .filter(t -> enabled == null || Objects.equals(t.getEnabled(), enabled))
                .filter(t -> isBlank(sourceType) || Objects.equals(t.getSourceType(), sourceType))
                .map(this::toTagDto)
                .toList();

        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int fromIndex = Math.min((safePageNum - 1) * safePageSize, records.size());
        int toIndex = Math.min(fromIndex + safePageSize, records.size());

        Map<String, Object> result = new HashMap<>();
        result.put("total", records.size());
        result.put("records", records.subList(fromIndex, toIndex));
        result.put("provider", getProviderCode());
        return result;
    }

    @Override
    public Object getTag(String tagCode) {
        return tagRepo.findByTagCode(tagCode).map(this::toTagDto).orElse(null);
    }

    @Override
    public Object getDeviceTags(String deviceCode) {
        return tagRepo.findByDeviceCode(deviceCode).stream().map(this::toTagDto).toList();
    }

    @Override
    public Object getRealtime(String tagCodes, String deviceCode, String areaCode) {
        List<IotRealtimeEntity> entities;

        if (!isBlank(tagCodes)) {
            List<String> codes = Arrays.stream(tagCodes.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).toList();
            entities = realtimeRepo.findByTagCodeIn(codes);
        } else if (!isBlank(deviceCode)) {
            entities = realtimeRepo.findByDeviceCode(deviceCode);
        } else if (!isBlank(areaCode)) {
            entities = realtimeRepo.findByAreaCode(areaCode);
        } else {
            entities = realtimeRepo.findAll();
        }

        return entities.stream().map(e -> new IotRealtimeValueDto(
                e.getTagCode(),
                e.getValue(),
                e.getValue() != null ? String.valueOf(e.getValue()) : "",
                "DOUBLE",
                e.getUnit() != null ? e.getUnit() : "",
                e.getQuality() != null && e.getQuality() == 192 ? "GOOD" : "BAD",
                e.getUpdatedAt() != null ? e.getUpdatedAt().format(TIME_FORMATTER) : "",
                e.getUpdatedAt() != null ? e.getUpdatedAt().format(TIME_FORMATTER) : "",
                e.getDeviceCode() != null ? e.getDeviceCode() : "",
                e.getAreaCode() != null ? e.getAreaCode() : "",
                e.getSourceType() != null ? e.getSourceType() : "KEPSERVER"
        )).toList();
    }

    @Override
    public Object getRealtimeSnapshot(String pageKey) {
        List<?> items = (List<?>) getRealtime(null, null, null);
        Map<String, Object> result = new HashMap<>();
        result.put("pageKey", isBlank(pageKey) ? "default" : pageKey);
        result.put("generatedAt", OffsetDateTime.now().format(TIME_FORMATTER));
        result.put("items", items);
        result.put("provider", getProviderCode());
        result.put("endpoint", iotProperties.getKepserver().getEndpoint());
        return result;
    }

    @Override
    public Object getHistory(String tagCode, String startTime, String endTime, String interval, String aggregate) {
        OffsetDateTime start = startTime != null ? OffsetDateTime.parse(startTime) : OffsetDateTime.now().minusHours(1);
        OffsetDateTime end = endTime != null ? OffsetDateTime.parse(endTime) : OffsetDateTime.now();
        String aggregateType = isBlank(aggregate) ? "RAW" : aggregate.toUpperCase(Locale.ROOT);

        List<IotHistoryEntity> entities;
        if ("RAW".equals(aggregateType)) {
            entities = historyRepo.findByTagCodeAndTimeRange(tagCode, start, end);
        } else {
            String pgInterval = mapInterval(interval);
            entities = historyRepo.findAggregated(tagCode, start, end, pgInterval);
        }

        List<IotHistoryValueDto> records = entities.stream()
                .map(e -> new IotHistoryValueDto(
                        e.getTagCode(),
                        e.getTime() != null ? e.getTime().format(TIME_FORMATTER) : "",
                        e.getValue(),
                        e.getQuality() != null && e.getQuality() == 192 ? "GOOD" : "BAD",
                        aggregateType
                ))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("tagCode", tagCode);
        result.put("startTime", start.format(TIME_FORMATTER));
        result.put("endTime", end.format(TIME_FORMATTER));
        result.put("interval", interval);
        result.put("aggregate", aggregateType);
        result.put("provider", getProviderCode());
        result.put("records", records);
        return result;
    }

    @Override
    public Object getAlarms(String alarmLevel, String alarmStatus, String deviceCode) {
        return List.of();
    }

    @Override
    public Object ackAlarm(String alarmId, String operator, String remark) {
        return Map.of("alarmId", alarmId, "status", "ACKED");
    }

    @Override
    public Object closeAlarm(String alarmId, String operator, String remark) {
        return Map.of("alarmId", alarmId, "status", "CLOSED");
    }

    @Override
    public Object getTagMappings() {
        return List.of();
    }

    private IotTagDefinitionDto toTagDto(IotTagEntity tag) {
        return new IotTagDefinitionDto(
                tag.getTagCode(),
                tag.getTagName(),
                tag.getSourceType(),
                tag.getSourcePath(),
                tag.getDeviceCode(),
                tag.getDeviceName(),
                tag.getAreaCode(),
                tag.getDataType(),
                tag.getUnit(),
                tag.getScanRate(),
                tag.getDeadband(),
                tag.getQualityRule(),
                tag.getEnabled(),
                tag.getRemark()
        );
    }

    private String mapInterval(String interval) {
        if (interval == null) return "1 minute";
        return switch (interval.toLowerCase(Locale.ROOT)) {
            case "1s", "second" -> "1 second";
            case "1m", "minute" -> "1 minute";
            case "5m" -> "5 minutes";
            case "1h", "hour" -> "1 hour";
            case "1d", "day" -> "1 day";
            default -> "1 minute";
        };
    }

    private static boolean containsIgnoreCase(String source, String keyword) {
        return source != null && keyword != null && source.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
