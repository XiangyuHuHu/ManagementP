package com.example.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.CoalSample;
import java.util.List;

public interface CoalSampleService {

    CoalSample create(CoalSample coalSample);
    CoalSample update(CoalSample coalSample);
    void delete(Long id);
    CoalSample getById(Long id);
    IPage<CoalSample> page(Integer page, Integer size, String sampleNo, String sampleName, String status);
    List<CoalSample> list(String sampleNo, String sampleName, String status);
    CoalSample getBySampleNo(String sampleNo);
    List<CoalSample> getByBatchNo(String batchNo);
    List<CoalSample> getRecent(Integer limit);
    List<CoalSample> findAll();
    CoalSample findById(Long id);
    CoalSample findBySampleId(String sampleId);
    List<CoalSample> findBySampleType(String sampleType);
    List<CoalSample> findBySampleTime(String sampleTime);
    List<CoalSample> findByTimeRange(String startTime, String endTime);
    List<CoalSample> findByStatus(String status);
}
