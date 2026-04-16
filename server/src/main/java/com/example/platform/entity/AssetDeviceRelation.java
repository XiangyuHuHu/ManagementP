package com.example.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "asset_device_relation")
public class AssetDeviceRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String parentDeviceId;
    private String childDeviceId;
    private Integer level;
    private Integer sort;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentDeviceId() {
        return parentDeviceId;
    }

    public void setParentDeviceId(String parentDeviceId) {
        this.parentDeviceId = parentDeviceId;
    }

    public String getChildDeviceId() {
        return childDeviceId;
    }

    public void setChildDeviceId(String childDeviceId) {
        this.childDeviceId = childDeviceId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
