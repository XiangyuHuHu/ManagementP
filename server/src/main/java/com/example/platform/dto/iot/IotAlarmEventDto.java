package com.example.platform.dto.iot;

public record IotAlarmEventDto(
        String alarmId,
        String tagCode,
        String alarmCode,
        String alarmName,
        String alarmLevel,
        String alarmStatus,
        Double currentValue,
        Double thresholdValue,
        String unit,
        String deviceCode,
        String startTime,
        String ackTime,
        String recoverTime,
        String ackBy,
        String remark
) {
}
