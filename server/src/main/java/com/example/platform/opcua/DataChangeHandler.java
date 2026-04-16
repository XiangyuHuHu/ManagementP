package com.example.platform.opcua;

import com.example.platform.config.IotProperties;
import com.example.platform.entity.AlarmRecord;
import com.example.platform.entity.AlarmRule;
import com.example.platform.entity.IotHistoryEntity;
import com.example.platform.entity.IotTagEntity;
import com.example.platform.repository.IotHistoryRepository;
import com.example.platform.repository.IotRealtimeRepository;
import com.example.platform.repository.IotTagRepository;
import com.example.platform.service.AlarmRecordService;
import com.example.platform.service.AlarmRuleService;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 处理从 OPC-UA 订阅收到的每一条数据变化：
 * 1. 更新 iot_realtime 表（最新值）
 * 2. 缓冲到队列，定时批量写入 iot_history 表（历史数据）
 * 3. 检查报警规则，触发报警记录
 * 4. 通过 WebSocket 推送到前端
 */
@Component
public class DataChangeHandler {

    private static final Logger log = LoggerFactory.getLogger(DataChangeHandler.class);

    private final IotProperties iotProperties;
    private final IotRealtimeRepository realtimeRepo;
    private final IotHistoryRepository historyRepo;
    private final IotTagRepository tagRepo;
    private final AlarmRuleService alarmRuleService;
    private final AlarmRecordService alarmRecordService;
    private final IotWebSocketPusher webSocketPusher;

    private final ConcurrentLinkedQueue<IotHistoryEntity> historyBuffer = new ConcurrentLinkedQueue<>();

    private final Map<String, IotTagEntity> tagCache = new ConcurrentHashMap<>();

    public DataChangeHandler(
            IotProperties iotProperties,
            IotRealtimeRepository realtimeRepo,
            IotHistoryRepository historyRepo,
            IotTagRepository tagRepo,
            AlarmRuleService alarmRuleService,
            AlarmRecordService alarmRecordService,
            IotWebSocketPusher webSocketPusher
    ) {
        this.iotProperties = iotProperties;
        this.realtimeRepo = realtimeRepo;
        this.historyRepo = historyRepo;
        this.tagRepo = tagRepo;
        this.alarmRuleService = alarmRuleService;
        this.alarmRecordService = alarmRecordService;
        this.webSocketPusher = webSocketPusher;
    }

    public void refreshTagCache() {
        tagCache.clear();
        tagRepo.findAll().forEach(tag -> {
            String path = tag.getSourcePath();
            if (path != null && !path.isBlank()) {
                tagCache.put(path, tag);
            }
        });
        log.info("已加载 {} 个点位到标签缓存", tagCache.size());
    }

    /**
     * OPC-UA 数据变化回调入口。
     * nodeId 格式示例: "ns=2;s=Channel1.Device1.Tag001"
     */
    public void onDataChange(String nodeId, DataValue dataValue) {
        try {
            Double numericValue = extractDouble(dataValue.getValue());
            if (numericValue == null) return;

            OffsetDateTime now = OffsetDateTime.now();
            short quality = dataValue.getStatusCode() != null && dataValue.getStatusCode().isGood()
                    ? (short) 192 : (short) 0;

            IotTagEntity tag = resolveTag(nodeId);
            String tagCode = tag != null ? tag.getTagCode() : nodeId;
            String unit = tag != null ? tag.getUnit() : "";
            String deviceCode = tag != null ? tag.getDeviceCode() : "";
            String areaCode = tag != null ? tag.getAreaCode() : "";
            String sourceType = iotProperties.getKepserver().getSourceType();

            realtimeRepo.upsert(tagCode, numericValue, quality, unit, deviceCode, areaCode, sourceType, now);

            IotHistoryEntity historyEntry = new IotHistoryEntity();
            historyEntry.setTime(now);
            historyEntry.setTagCode(tagCode);
            historyEntry.setValue(numericValue);
            historyEntry.setQuality(quality);
            historyEntry.setSourceType(sourceType);
            historyBuffer.add(historyEntry);

            checkAlarmRules(tagCode, numericValue, deviceCode, now);

            webSocketPusher.pushRealtimeValue(tagCode, numericValue, quality, unit, deviceCode, now);

        } catch (Exception e) {
            log.error("处理数据变化出错 [{}]: {}", nodeId, e.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "${platform.iot.kepserver.batch-write-interval-ms:5000}")
    public void flushHistoryBuffer() {
        if (historyBuffer.isEmpty()) return;

        List<IotHistoryEntity> batch = new ArrayList<>();
        int batchSize = iotProperties.getKepserver().getBatchWriteSize();
        while (!historyBuffer.isEmpty() && batch.size() < batchSize) {
            IotHistoryEntity entry = historyBuffer.poll();
            if (entry != null) batch.add(entry);
        }

        if (!batch.isEmpty()) {
            try {
                historyRepo.saveAll(batch);
                log.debug("批量写入 {} 条历史数据", batch.size());
            } catch (Exception e) {
                log.error("批量写入历史数据失败: {}", e.getMessage());
                historyBuffer.addAll(batch);
            }
        }
    }

    private void checkAlarmRules(String tagCode, Double value, String deviceCode, OffsetDateTime now) {
        try {
            List<AlarmRule> rules = alarmRuleService.findByPointId(tagCode);
            if (rules == null || rules.isEmpty()) {
                rules = alarmRuleService.findByDeviceId(deviceCode);
            }
            if (rules == null) return;

            for (AlarmRule rule : rules) {
                if (!"ENABLED".equalsIgnoreCase(rule.getStatus())) continue;

                boolean triggered = false;
                if (rule.getMaxValue() != null && value > rule.getMaxValue()) triggered = true;
                if (rule.getMinValue() != null && value < rule.getMinValue()) triggered = true;

                if (triggered) {
                    AlarmRecord record = new AlarmRecord();
                    record.setAlarmId("ALM-" + System.currentTimeMillis());
                    record.setDeviceId(deviceCode);
                    record.setPointId(tagCode);
                    record.setRuleId(rule.getRuleId());
                    record.setAlarmType(rule.getRuleType());
                    record.setAlarmLevel(rule.getLevel());
                    record.setAlarmMessage(rule.getDescription() + " (当前值: " + value + ")");
                    record.setCurrentValue(value);
                    record.setStartTime(now.toString());
                    record.setStatus("ACTIVE");
                    alarmRecordService.create(record);

                    webSocketPusher.pushAlarm(record);
                    log.warn("触发报警 [{}] 规则={} 当前值={}", tagCode, rule.getRuleId(), value);
                }
            }
        } catch (Exception e) {
            log.error("检查报警规则出错 [{}]: {}", tagCode, e.getMessage());
        }
    }

    private IotTagEntity resolveTag(String nodeId) {
        String sourcePath = nodeId;
        if (nodeId.contains(";s=")) {
            sourcePath = nodeId.substring(nodeId.indexOf(";s=") + 3);
        }
        return tagCache.get(sourcePath);
    }

    private Double extractDouble(Variant variant) {
        if (variant == null || variant.getValue() == null) return null;
        Object val = variant.getValue();
        if (val instanceof Number num) return num.doubleValue();
        if (val instanceof Boolean bool) return bool ? 1.0 : 0.0;
        try {
            return Double.parseDouble(val.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
