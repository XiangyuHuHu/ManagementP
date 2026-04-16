package com.example.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.AssetDevice;
import java.util.List;
import java.util.Map;

public interface AssetDeviceService {

    AssetDevice create(AssetDevice assetDevice);

    AssetDevice update(AssetDevice assetDevice);

    void delete(Long id);

    AssetDevice getById(Long id);

    IPage<AssetDevice> page(Integer page, Integer size, String deviceNo, String deviceName, String status, String type, Long locationId);

    List<AssetDevice> list(String deviceNo, String deviceName, String status, String type, Long locationId);

    AssetDevice getByDeviceNo(String deviceNo);

    List<AssetDevice> getByLocationId(Long locationId);

    List<AssetDevice> getByCategoryId(Long categoryId);

    List<AssetDevice> getByStatus(String status);

    List<AssetDevice> getByType(String type);

    List<AssetDevice> getRecent(Integer limit);

    long count();

    long countByStatus(String status);

    Map<String, Long> countByType();

    List<AssetDevice> findAll();

    AssetDevice findByDeviceId(String deviceId);

    List<AssetDevice> findByCategoryId(String categoryId);

    List<AssetDevice> findByLocationId(String locationId);

    List<AssetDevice> findByStatus(String status);

    AssetDevice findById(Long id);
}
