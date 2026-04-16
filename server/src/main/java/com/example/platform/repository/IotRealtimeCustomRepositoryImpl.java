package com.example.platform.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

public class IotRealtimeCustomRepositoryImpl implements IotRealtimeCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void upsert(String tagCode, Double value, Short quality, String unit,
                       String deviceCode, String areaCode, String sourceType, OffsetDateTime updatedAt) {
        em.createNativeQuery("""
            INSERT INTO iot_realtime (tag_code, value, quality, unit, device_code, area_code, source_type, updated_at)
            VALUES (:tagCode, :value, :quality, :unit, :deviceCode, :areaCode, :sourceType, :updatedAt)
            ON CONFLICT (tag_code) DO UPDATE SET
                value = EXCLUDED.value,
                quality = EXCLUDED.quality,
                unit = EXCLUDED.unit,
                device_code = EXCLUDED.device_code,
                area_code = EXCLUDED.area_code,
                source_type = EXCLUDED.source_type,
                updated_at = EXCLUDED.updated_at
            """)
            .setParameter("tagCode", tagCode)
            .setParameter("value", value)
            .setParameter("quality", quality)
            .setParameter("unit", unit)
            .setParameter("deviceCode", deviceCode)
            .setParameter("areaCode", areaCode)
            .setParameter("sourceType", sourceType)
            .setParameter("updatedAt", updatedAt)
            .executeUpdate();
    }
}
