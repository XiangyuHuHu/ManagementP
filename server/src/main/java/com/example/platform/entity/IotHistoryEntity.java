package com.example.platform.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "iot_history")
public class IotHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time", nullable = false)
    private OffsetDateTime time;

    @Column(name = "tag_code", nullable = false, length = 128)
    private String tagCode;

    @Column(name = "value")
    private Double value;

    @Column(name = "quality")
    private Short quality;

    @Column(name = "source_type", length = 64)
    private String sourceType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OffsetDateTime getTime() { return time; }
    public void setTime(OffsetDateTime time) { this.time = time; }

    public String getTagCode() { return tagCode; }
    public void setTagCode(String tagCode) { this.tagCode = tagCode; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public Short getQuality() { return quality; }
    public void setQuality(Short quality) { this.quality = quality; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
}
