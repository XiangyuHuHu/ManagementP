package com.example.platform.controller;

import com.example.platform.entity.AssetDevice;
import com.example.platform.service.AssetDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/asset/device")
public class AssetDeviceController {

    @Autowired
    private AssetDeviceService assetDeviceService;

    @GetMapping
    public List<AssetDevice> findAll() {
        return assetDeviceService.findAll();
    }

    @GetMapping("/{id}")
    public AssetDevice findById(@PathVariable Long id) {
        return assetDeviceService.findById(id);
    }

    @GetMapping("/deviceId/{deviceId}")
    public AssetDevice findByDeviceId(@PathVariable String deviceId) {
        return assetDeviceService.findByDeviceId(deviceId);
    }

    @PostMapping
    public void create(@RequestBody AssetDevice device) {
        assetDeviceService.create(device);
    }

    @PutMapping
    public void update(@RequestBody AssetDevice device) {
        assetDeviceService.update(device);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        assetDeviceService.delete(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<AssetDevice> findByCategoryId(@PathVariable String categoryId) {
        return assetDeviceService.findByCategoryId(categoryId);
    }

    @GetMapping("/location/{locationId}")
    public List<AssetDevice> findByLocationId(@PathVariable String locationId) {
        return assetDeviceService.findByLocationId(locationId);
    }

    @GetMapping("/status/{status}")
    public List<AssetDevice> findByStatus(@PathVariable String status) {
        return assetDeviceService.findByStatus(status);
    }
}
