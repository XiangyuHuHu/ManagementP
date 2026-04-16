package com.example.platform.dto.iot;

public record IotHistoryValueDto(
        String tagCode,
        String time,
        Double value,
        String quality,
        String aggregateType
) {
}
