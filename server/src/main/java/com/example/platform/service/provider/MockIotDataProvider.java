package com.example.platform.service.provider;

import com.example.platform.dto.iot.IotAlarmEventDto;
import com.example.platform.dto.iot.IotHistoryValueDto;
import com.example.platform.dto.iot.IotRealtimeValueDto;
import com.example.platform.dto.iot.IotTagDefinitionDto;
import com.example.platform.dto.iot.IotTagMappingDto;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MockIotDataProvider implements IotDataProvider {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final List<IotTagDefinitionDto> tags = new ArrayList<>(List.of(
            new IotTagDefinitionDto("coal.feed.rate", "原煤给料量", "MOCK", "mock.feed.rate", "FEEDER-01", "1号给料机", "feed-workshop", "DOUBLE", "t/h", 1000, 0.1, "GOOD_ONLY", true, "生产核心测点"),
            new IotTagDefinitionDto("crusher.current", "破碎机电流", "MOCK", "mock.crusher.current", "CRUSHER-01", "主破碎机", "crushing-workshop", "DOUBLE", "A", 1000, 0.2, "GOOD_ONLY", true, "设备运行测点"),
            new IotTagDefinitionDto("silo.level", "煤仓料位", "MOCK", "mock.silo.level", "SILO-01", "原煤仓", "storage-area", "DOUBLE", "%", 1500, 0.5, "GOOD_ONLY", true, "库存关键测点"),
            new IotTagDefinitionDto("belt.temperature", "皮带机温度", "MOCK", "mock.belt.temperature", "BELT-02", "2号皮带机", "transport-workshop", "DOUBLE", "℃", 1000, 0.1, "GOOD_ONLY", true, "温度监测点")
    ));

    private final List<IotRealtimeValueDto> realtimeValues = new ArrayList<>(List.of(
            realtime("coal.feed.rate", 428.6, "DOUBLE", "t/h", "FEEDER-01", "feed-workshop", "MOCK"),
            realtime("crusher.current", 83.2, "DOUBLE", "A", "CRUSHER-01", "crushing-workshop", "MOCK"),
            realtime("silo.level", 62.5, "DOUBLE", "%", "SILO-01", "storage-area", "MOCK"),
            realtime("belt.temperature", 46.3, "DOUBLE", "℃", "BELT-02", "transport-workshop", "MOCK")
    ));

    private final List<IotAlarmEventDto> alarms = new ArrayList<>(List.of(
            new IotAlarmEventDto("ALM-20260320-0001", "belt.temperature", "BELT_TEMP_HIGH", "皮带机温度偏高", "MEDIUM", "ACTIVE", 46.3, 45.0, "℃", "BELT-02", "2026-03-20T10:12:10+08:00", null, null, null, "建议检查托辊和润滑"),
            new IotAlarmEventDto("ALM-20260320-0002", "crusher.current", "CRUSHER_CURRENT_HIGH", "破碎机电流偏高", "HIGH", "ACKED", 83.2, 80.0, "A", "CRUSHER-01", "2026-03-20T11:08:15+08:00", "2026-03-20T11:10:05+08:00", null, "值班员张三", "已安排巡检")
    ));

    private final List<IotTagMappingDto> mappings = new ArrayList<>(List.of(
            new IotTagMappingDto("MAP-001", "coal.feed.rate", "dashboard.feedRate", "大屏-给料量", "mock.feed.rate", "identity", true, "大屏实时卡片"),
            new IotTagMappingDto("MAP-002", "crusher.current", "equipment.crusherCurrent", "设备-破碎机电流", "mock.crusher.current", "identity", true, "设备状态监测"),
            new IotTagMappingDto("MAP-003", "silo.level", "storage.siloLevel", "储装-煤仓料位", "mock.silo.level", "identity", true, "库存监测")
    ));

    @Override
    public String getProviderCode() {
        return "mock";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public Map<String, Object> getTags(String keyword, String deviceCode, String areaCode, Boolean enabled, String sourceType, Integer pageNum, Integer pageSize) {
        List<IotTagDefinitionDto> filtered = tags.stream()
                .filter(tag -> isBlank(keyword) || containsIgnoreCase(tag.tagCode(), keyword) || containsIgnoreCase(tag.tagName(), keyword))
                .filter(tag -> isBlank(deviceCode) || Objects.equals(tag.deviceCode(), deviceCode))
                .filter(tag -> isBlank(areaCode) || Objects.equals(tag.areaCode(), areaCode))
                .filter(tag -> enabled == null || Objects.equals(tag.enabled(), enabled))
                .filter(tag -> isBlank(sourceType) || Objects.equals(tag.sourceType(), sourceType))
                .toList();

        return buildPageResult(filtered, pageNum, pageSize);
    }

    @Override
    public Object getTag(String tagCode) {
        return tags.stream().filter(tag -> Objects.equals(tag.tagCode(), tagCode)).findFirst().orElse(null);
    }

    @Override
    public Object getDeviceTags(String deviceCode) {
        return tags.stream().filter(tag -> Objects.equals(tag.deviceCode(), deviceCode)).toList();
    }

    @Override
    public Object getRealtime(String tagCodes, String deviceCode, String areaCode) {
        List<String> codeList = splitCsv(tagCodes);
        return realtimeValues.stream()
                .filter(item -> codeList.isEmpty() || codeList.contains(item.tagCode()))
                .filter(item -> isBlank(deviceCode) || Objects.equals(item.deviceCode(), deviceCode))
                .filter(item -> isBlank(areaCode) || Objects.equals(item.areaCode(), areaCode))
                .toList();
    }

    @Override
    public Object getRealtimeSnapshot(String pageKey) {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("pageKey", isBlank(pageKey) ? "default" : pageKey);
        snapshot.put("generatedAt", OffsetDateTime.now().format(TIME_FORMATTER));
        snapshot.put("items", List.of(realtimeValues.get(0), realtimeValues.get(1), realtimeValues.get(2)));
        return snapshot;
    }

    @Override
    public Object getHistory(String tagCode, String startTime, String endTime, String interval, String aggregate) {
        String aggregateType = isBlank(aggregate) ? "RAW" : aggregate.toUpperCase(Locale.ROOT);
        Map<String, Object> result = new HashMap<>();
        result.put("tagCode", tagCode);
        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("interval", interval);
        result.put("aggregate", aggregateType);
        result.put("records", List.of(
                new IotHistoryValueDto(tagCode, "2026-03-20T10:00:00+08:00", 421.3, "GOOD", aggregateType),
                new IotHistoryValueDto(tagCode, "2026-03-20T10:05:00+08:00", 424.8, "GOOD", aggregateType),
                new IotHistoryValueDto(tagCode, "2026-03-20T10:10:00+08:00", 427.1, "GOOD", aggregateType),
                new IotHistoryValueDto(tagCode, "2026-03-20T10:15:00+08:00", 428.6, "GOOD", aggregateType)
        ));
        return result;
    }

    @Override
    public Object getAlarms(String alarmLevel, String alarmStatus, String deviceCode) {
        return alarms.stream()
                .filter(item -> isBlank(alarmLevel) || Objects.equals(item.alarmLevel(), alarmLevel))
                .filter(item -> isBlank(alarmStatus) || Objects.equals(item.alarmStatus(), alarmStatus))
                .filter(item -> isBlank(deviceCode) || Objects.equals(item.deviceCode(), deviceCode))
                .sorted(Comparator.comparing(IotAlarmEventDto::startTime).reversed())
                .toList();
    }

    @Override
    public Object ackAlarm(String alarmId, String operator, String remark) {
        return updateAlarmStatus(alarmId, "ACKED", operator, remark);
    }

    @Override
    public Object closeAlarm(String alarmId, String operator, String remark) {
        return updateAlarmStatus(alarmId, "CLOSED", operator, remark);
    }

    @Override
    public Object getTagMappings() {
        return mappings;
    }

    private IotAlarmEventDto updateAlarmStatus(String alarmId, String status, String operator, String remark) {
        for (int i = 0; i < alarms.size(); i++) {
            IotAlarmEventDto current = alarms.get(i);
            if (Objects.equals(current.alarmId(), alarmId)) {
                String now = OffsetDateTime.now().format(TIME_FORMATTER);
                IotAlarmEventDto updated = new IotAlarmEventDto(
                        current.alarmId(),
                        current.tagCode(),
                        current.alarmCode(),
                        current.alarmName(),
                        current.alarmLevel(),
                        status,
                        current.currentValue(),
                        current.thresholdValue(),
                        current.unit(),
                        current.deviceCode(),
                        current.startTime(),
                        "ACKED".equals(status) ? now : current.ackTime(),
                        "CLOSED".equals(status) ? now : current.recoverTime(),
                        operator,
                        isBlank(remark) ? current.remark() : remark
                );
                alarms.set(i, updated);
                return updated;
            }
        }
        return new IotAlarmEventDto(
                alarmId,
                "unknown.tag",
                "UNKNOWN",
                "未找到报警",
                "INFO",
                status,
                null,
                null,
                "",
                "",
                OffsetDateTime.now().format(TIME_FORMATTER),
                null,
                null,
                operator,
                "报警不存在，返回模拟结果"
        );
    }

    private static Map<String, Object> buildPageResult(List<IotTagDefinitionDto> filtered, Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int fromIndex = Math.min((safePageNum - 1) * safePageSize, filtered.size());
        int toIndex = Math.min(fromIndex + safePageSize, filtered.size());

        Map<String, Object> result = new HashMap<>();
        result.put("total", filtered.size());
        result.put("records", filtered.subList(fromIndex, toIndex));
        return result;
    }

    private static IotRealtimeValueDto realtime(String tagCode, Double value, String dataType, String unit, String deviceCode, String areaCode, String sourceType) {
        String now = OffsetDateTime.now().format(TIME_FORMATTER);
        return new IotRealtimeValueDto(tagCode, value, String.valueOf(value), dataType, unit, "GOOD", now, now, deviceCode, areaCode, sourceType);
    }

    private static List<String> splitCsv(String value) {
        if (isBlank(value)) {
            return List.of();
        }
        return List.of(value.split(","))
                .stream()
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    private static boolean containsIgnoreCase(String source, String keyword) {
        return source != null && keyword != null && source.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
