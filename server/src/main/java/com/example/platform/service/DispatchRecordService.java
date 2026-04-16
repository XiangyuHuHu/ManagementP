package com.example.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.DispatchRecord;

import java.util.List;

public interface DispatchRecordService {

    DispatchRecord create(DispatchRecord dispatchRecord);

    DispatchRecord update(DispatchRecord dispatchRecord);

    void delete(Long id);

    DispatchRecord getById(Long id);

    IPage<DispatchRecord> page(Integer page, Integer size, String shift, String recordType, String status, String date);

    List<DispatchRecord> list(String shift, String recordType, String status, String date);

    List<DispatchRecord> getRecent(Integer limit);
}
