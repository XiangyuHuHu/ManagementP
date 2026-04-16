package com.example.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.StorageTransport;

import java.util.List;

public interface StorageTransportService {

    StorageTransport create(StorageTransport storageTransport);

    StorageTransport update(StorageTransport storageTransport);

    void delete(Long id);

    StorageTransport getById(Long id);

    IPage<StorageTransport> page(Integer page, Integer size, String recordType, String transportMode, String status, String date);

    List<StorageTransport> list(String recordType, String transportMode, String status, String date);

    List<StorageTransport> getRecent(Integer limit);
}
