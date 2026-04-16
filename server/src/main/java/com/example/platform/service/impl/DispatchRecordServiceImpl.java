package com.example.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.platform.entity.DispatchRecord;
import com.example.platform.mapper.DispatchRecordMapper;
import com.example.platform.service.DispatchRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DispatchRecordServiceImpl implements DispatchRecordService {

    private static final Logger log = LoggerFactory.getLogger(DispatchRecordServiceImpl.class);

    private final DispatchRecordMapper dispatchRecordMapper;

    public DispatchRecordServiceImpl(DispatchRecordMapper dispatchRecordMapper) {
        this.dispatchRecordMapper = dispatchRecordMapper;
    }

    @Override
    public DispatchRecord create(DispatchRecord dispatchRecord) {
        dispatchRecordMapper.insert(dispatchRecord);
        return dispatchRecord;
    }

    @Override
    public DispatchRecord update(DispatchRecord dispatchRecord) {
        dispatchRecordMapper.updateById(dispatchRecord);
        return dispatchRecord;
    }

    @Override
    public void delete(Long id) {
        dispatchRecordMapper.deleteById(id);
    }

    @Override
    public DispatchRecord getById(Long id) {
        try {
            return dispatchRecordMapper.selectById(id);
        } catch (Exception ex) {
            log.warn("读取调度记录失败，返回空结果，id={}", id, ex);
            return null;
        }
    }

    @Override
    public IPage<DispatchRecord> page(Integer page, Integer size, String shift, String recordType, String status, String date) {
        Page<DispatchRecord> pageInfo = new Page<>(page, size);
        List<DispatchRecord> filteredData = filter(shift, recordType, status, date);
        List<DispatchRecord> pageData = filteredData.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        pageInfo.setRecords(pageData);
        pageInfo.setTotal(filteredData.size());
        return pageInfo;
    }

    @Override
    public List<DispatchRecord> list(String shift, String recordType, String status, String date) {
        return filter(shift, recordType, status, date);
    }

    @Override
    public List<DispatchRecord> getRecent(Integer limit) {
        return safeSelectAll().stream()
                .sorted(Comparator.comparing(DispatchRecord::getRecordTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<DispatchRecord> filter(String shift, String recordType, String status, String date) {
        return safeSelectAll().stream()
                .filter(item -> shift == null || shift.isBlank() || Objects.equals(item.getShift(), shift))
                .filter(item -> recordType == null || recordType.isBlank() || Objects.equals(item.getRecordType(), recordType))
                .filter(item -> status == null || status.isBlank() || Objects.equals(item.getStatus(), status))
                .filter(item -> matchesDate(item.getRecordTime() == null ? null : item.getRecordTime().toLocalDate(), date))
                .sorted(Comparator.comparing(DispatchRecord::getRecordTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private List<DispatchRecord> safeSelectAll() {
        try {
            return dispatchRecordMapper.selectList(null);
        } catch (Exception ex) {
            log.warn("调度记录表不可用，返回空列表", ex);
            return List.of();
        }
    }

    private boolean matchesDate(LocalDate recordDate, String date) {
        if (date == null || date.isBlank()) {
            return true;
        }
        return recordDate != null && date.equals(recordDate.toString());
    }
}
