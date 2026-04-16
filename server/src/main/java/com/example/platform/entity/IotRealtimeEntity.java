package com.example.platform.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "iot_realtime")
public class IotRealtimeEntity {

    @Id
    @Column(name = "tag_code", nullable = false, length = 128)
    private String tagCode;

    @Column(name = "value")
    private Double value;

    @Column(name = "quality")
    private Short quality;

    @Column(name = "unit", length = 32)
    private String unit;

    @Column(name = "device_code", length = 128)
    private String deviceCode;

    @Column(name = "area_code", length = 128)
    private String areaCode;

    @Column(name = "source_type", length = 64)
    private String sourceType;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void onSave() {
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
    }

    public String getTagCode() { return tagCode; }
    public void setTagCode(String tagCode) { this.tagCode = tagCode; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public Short getQuality() { return quality; }
    public void setQuality(Short quality) { this.quality = quality; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDeviceCode() { return deviceCode; }
    public void setDeviceCode(String deviceCode) { this.deviceCode = deviceCode; }

    public String getAreaCode() { return areaCode; }
    public void setAreaCode(String areaCode) { this.areaCode = areaCode; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
