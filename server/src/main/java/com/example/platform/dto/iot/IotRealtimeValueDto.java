package com.example.platform.dto.iot;

public record IotRealtimeValueDto(
        String tagCode,
        Double value,
        String valueText,
        String dataType,
        String unit,
        String quality,
        String timestamp,
        String sourceTime,
        String deviceCode,
        String areaCode,
        String sourceType
) {
}
