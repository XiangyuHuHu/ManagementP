package com.example.platform.dto.iot;

public record IotTagUpsertRequest(
        String tagCode,
        String tagName,
        String sourceType,
        String sourcePath,
        String deviceCode,
        String deviceName,
        String areaCode,
        String dataType,
        String unit,
        Integer scanRate,
        Double deadband,
        String qualityRule,
        Boolean enabled,
        String remark
) {
}
