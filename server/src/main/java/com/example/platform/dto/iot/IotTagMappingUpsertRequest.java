package com.example.platform.dto.iot;

public record IotTagMappingUpsertRequest(
        String mappingId,
        String tagCode,
        String businessCode,
        String businessName,
        String sourcePath,
        String transformRule,
        Boolean enabled,
        String remark
) {
}
