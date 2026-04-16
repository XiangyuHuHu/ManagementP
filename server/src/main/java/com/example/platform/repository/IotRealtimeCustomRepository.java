package com.example.platform.repository;

import java.time.OffsetDateTime;

public interface IotRealtimeCustomRepository {

    void upsert(String tagCode, Double value, Short quality, String unit,
                String deviceCode, String areaCode, String sourceType, OffsetDateTime updatedAt);
}
