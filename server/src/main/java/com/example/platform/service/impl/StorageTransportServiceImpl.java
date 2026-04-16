package com.example.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.platform.entity.StorageTransport;
import com.example.platform.mapper.StorageTransportMapper;
import com.example.platform.service.StorageTransportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StorageTransportServiceImpl implements StorageTransportService {

    private final StorageTransportMapper storageTransportMapper;

    public StorageTransportServiceImpl(StorageTransportMapper storageTransportMapper) {
        this.storageTransportMapper = storageTransportMapper;
    }

    @Override
    public StorageTransport create(StorageTransport storageTransport) {
        storageTransportMapper.insert(storageTransport);
        return storageTransport;
    }

    @Override
    public StorageTransport update(StorageTransport storageTransport) {
        storageTransportMapper.updateById(storageTransport);
        return storageTransport;
    }

    @Override
    public void delete(Long id) {
        storageTransportMapper.deleteById(id);
    }

    @Override
    public StorageTransport getById(Long id) {
        return storageTransportMapper.selectById(id);
    }

    @Override
    public IPage<StorageTransport> page(Integer page, Integer size, String recordType, String transportMode, String status, String date) {
        Page<StorageTransport> pageInfo = new Page<>(page, size);
        List<StorageTransport> filteredData = filter(recordType, transportMode, status, date);
        List<StorageTransport> pageData = filteredData.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        pageInfo.setRecords(pageData);
        pageInfo.setTotal(filteredData.size());
        return pageInfo;
    }

    @Override
    public List<StorageTransport> list(String recordType, String transportMode, String status, String date) {
        return filter(recordType, transportMode, status, date);
    }

    @Override
    public List<StorageTransport> getRecent(Integer limit) {
        return storageTransportMapper.selectList(null).stream()
                .sorted(Comparator.comparing(StorageTransport::getRecordTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<StorageTransport> filter(String recordType, String transportMode, String status, String date) {
        return storageTransportMapper.selectList(null).stream()
                .filter(item -> recordType == null || recordType.isBlank() || Objects.equals(item.getRecordType(), recordType))
                .filter(item -> transportMode == null || transportMode.isBlank() || Objects.equals(item.getTransportMode(), transportMode))
                .filter(item -> status == null || status.isBlank() || Objects.equals(item.getStatus(), status))
                .filter(item -> matchesDate(item.getRecordTime() == null ? null : item.getRecordTime().toLocalDate(), date))
                .sorted(Comparator.comparing(StorageTransport::getRecordTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private boolean matchesDate(LocalDate recordDate, String date) {
        if (date == null || date.isBlank()) {
            return true;
        }
        return recordDate != null && date.equals(recordDate.toString());
    }
}
