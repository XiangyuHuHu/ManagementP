package com.example.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.platform.entity.CoalSample;
import com.example.platform.mapper.CoalSampleMapper;
import com.example.platform.service.CoalSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoalSampleServiceImpl implements CoalSampleService {

    @Autowired
    private CoalSampleMapper coalSampleMapper;

    @Override
    public CoalSample create(CoalSample coalSample) {
        coalSampleMapper.insert(coalSample);
        return coalSample;
    }

    @Override
    public CoalSample update(CoalSample coalSample) {
        coalSampleMapper.updateById(coalSample);
        return coalSample;
    }

    @Override
    public void delete(Long id) {
        coalSampleMapper.deleteById(id);
    }

    @Override
    public CoalSample getById(Long id) {
        return coalSampleMapper.selectById(id);
    }

    @Override
    public IPage<CoalSample> page(Integer page, Integer size, String sampleNo, String sampleName, String status) {
        Page<CoalSample> pageInfo = new Page<>(page, size);
        List<CoalSample> allData = coalSampleMapper.selectList(null);
        
        List<CoalSample> filteredData = allData.stream()
                .filter(sample -> {
                    boolean match = true;
                    if (sampleNo != null && sample.getSampleId() != null && !sample.getSampleId().contains(sampleNo)) match = false;
                    if (sampleName != null && sample.getSampleName() != null && !sample.getSampleName().contains(sampleName)) match = false;
                    if (status != null && sample.getStatus() != null && !sample.getStatus().equals(status)) match = false;
                    return match;
                })
                .collect(Collectors.toList());
        
        int total = filteredData.size();
        List<CoalSample> pageData = filteredData.stream()
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        
        pageInfo.setRecords(pageData);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public List<CoalSample> list(String sampleNo, String sampleName, String status) {
        return coalSampleMapper.selectList(null).stream()
                .filter(sample -> {
                    boolean match = true;
                    if (sampleNo != null && sample.getSampleId() != null && !sample.getSampleId().contains(sampleNo)) match = false;
                    if (sampleName != null && sample.getSampleName() != null && !sample.getSampleName().contains(sampleName)) match = false;
                    if (status != null && sample.getStatus() != null && !sample.getStatus().equals(status)) match = false;
                    return match;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CoalSample getBySampleNo(String sampleNo) {
        return coalSampleMapper.selectList(null).stream()
                .filter(s -> s.getSampleId() != null && s.getSampleId().equals(sampleNo))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CoalSample> getByBatchNo(String batchNo) {
        return coalSampleMapper.selectList(null).stream()
                .filter(s -> s.getBatchNo() != null && s.getBatchNo().equals(batchNo))
                .collect(Collectors.toList());
    }

    @Override
    public List<CoalSample> getRecent(Integer limit) {
        return coalSampleMapper.selectList(null).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<CoalSample> findAll() {
        return coalSampleMapper.selectList(null);
    }

    @Override
    public CoalSample findById(Long id) {
        return coalSampleMapper.selectById(id);
    }

    @Override
    public CoalSample findBySampleId(String sampleId) {
        return coalSampleMapper.selectList(null).stream()
                .filter(s -> s.getSampleId() != null && s.getSampleId().equals(sampleId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<CoalSample> findBySampleType(String sampleType) {
        return coalSampleMapper.selectList(null).stream()
                .filter(s -> s.getSampleType() != null && s.getSampleType().equals(sampleType))
                .collect(Collectors.toList());
    }

    @Override
    public List<CoalSample> findBySampleTime(String sampleTime) {
        return coalSampleMapper.selectList(null).stream()
                .filter(s -> s.getSampleTime() != null && s.getSampleTime().equals(sampleTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<CoalSample> findByTimeRange(String startTime, String endTime) {
        return coalSampleMapper.selectList(null).stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<CoalSample> findByStatus(String status) {
        return coalSampleMapper.selectList(null).stream()
                .filter(s -> s.getStatus() != null && s.getStatus().equals(status))
                .collect(Collectors.toList());
    }
}
