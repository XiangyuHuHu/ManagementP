package com.example.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.platform.entity.AssetDevice;
import com.example.platform.mapper.AssetDeviceMapper;
import com.example.platform.service.AssetDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssetDeviceServiceImpl implements AssetDeviceService {

    @Autowired
    private AssetDeviceMapper assetDeviceMapper;

    @Override
    public AssetDevice create(AssetDevice assetDevice) {
        assetDeviceMapper.insert(assetDevice);
        return assetDevice;
    }

    @Override
    public AssetDevice update(AssetDevice assetDevice) {
        assetDeviceMapper.updateById(assetDevice);
        return assetDevice;
    }

    @Override
    public void delete(Long id) {
        assetDeviceMapper.deleteById(id);
    }

    @Override
    public AssetDevice getById(Long id) {
        return assetDeviceMapper.selectById(id);
    }

    @Override
    public AssetDevice findById(Long id) {
        return assetDeviceMapper.selectById(id);
    }

    @Override
    public IPage<AssetDevice> page(Integer page, Integer size, String deviceNo, String deviceName, String status, String type, Long locationId) {
        Page<AssetDevice> pageInfo = new Page<>(page, size);
        List<AssetDevice> allData = assetDeviceMapper.selectList(null);
        
        List<AssetDevice> filteredData = allData.stream()
                .filter(device -> {
                    boolean match = true;
                    if (deviceNo != null && device.getDeviceId() != null && !device.getDeviceId().contains(deviceNo)) match = false;
                    if (deviceName != null && device.getName() != null && !device.getName().contains(deviceName)) match = false;
                    if (status != null && device.getStatus() != null && !device.getStatus().equals(status)) match = false;
                    if (type != null && device.getModel() != null && !device.getModel().equals(type)) match = false;
                    if (locationId != null && device.getLocationId() != null && !device.getLocationId().equals(String.valueOf(locationId))) match = false;
                    return match;
                })
                .collect(Collectors.toList());
        
        int total = filteredData.size();
        List<AssetDevice> pageData = filteredData.stream()
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        
        pageInfo.setRecords(pageData);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public List<AssetDevice> list(String deviceNo, String deviceName, String status, String type, Long locationId) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(device -> {
                    boolean match = true;
                    if (deviceNo != null && device.getDeviceId() != null && !device.getDeviceId().contains(deviceNo)) match = false;
                    if (deviceName != null && device.getName() != null && !device.getName().contains(deviceName)) match = false;
                    if (status != null && device.getStatus() != null && !device.getStatus().equals(status)) match = false;
                    if (type != null && device.getModel() != null && !device.getModel().equals(type)) match = false;
                    if (locationId != null && device.getLocationId() != null && !device.getLocationId().equals(String.valueOf(locationId))) match = false;
                    return match;
                })
                .collect(Collectors.toList());
    }

    @Override
    public AssetDevice getByDeviceNo(String deviceNo) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(device -> device.getDeviceId() != null && device.getDeviceId().equals(deviceNo))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<AssetDevice> getByLocationId(Long locationId) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(device -> device.getLocationId() != null && device.getLocationId().equals(String.valueOf(locationId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetDevice> getByCategoryId(Long categoryId) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(device -> device.getCategoryId() != null && device.getCategoryId().equals(String.valueOf(categoryId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetDevice> getByStatus(String status) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(device -> device.getStatus() != null && device.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetDevice> getByType(String type) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(device -> device.getModel() != null && device.getModel().equals(type))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetDevice> getRecent(Integer limit) {
        return assetDeviceMapper.selectList(null).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return assetDeviceMapper.selectList(null).size();
    }

    @Override
    public long countByStatus(String status) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(device -> device.getStatus() != null && device.getStatus().equals(status))
                .count();
    }

    @Override
    public Map<String, Long> countByType() {
        List<AssetDevice> devices = assetDeviceMapper.selectList(null);
        return devices.stream()
                .filter(d -> d.getModel() != null)
                .collect(Collectors.groupingBy(AssetDevice::getModel, Collectors.counting()));
    }

    @Override
    public List<AssetDevice> findAll() {
        return assetDeviceMapper.selectList(null);
    }

    @Override
    public AssetDevice findByDeviceId(String deviceId) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(d -> d.getDeviceId() != null && d.getDeviceId().equals(deviceId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<AssetDevice> findByCategoryId(String categoryId) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(d -> d.getCategoryId() != null && d.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetDevice> findByLocationId(String locationId) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(d -> d.getLocationId() != null && d.getLocationId().equals(locationId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetDevice> findByStatus(String status) {
        return assetDeviceMapper.selectList(null).stream()
                .filter(d -> d.getStatus() != null && d.getStatus().equals(status))
                .collect(Collectors.toList());
    }
}
