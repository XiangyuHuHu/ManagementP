package com.example.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.platform.entity.CoalQuality;
import com.example.platform.mapper.CoalQualityMapper;
import com.example.platform.service.CoalQualityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CoalQualityServiceImpl implements CoalQualityService {

    private static final Logger log = LoggerFactory.getLogger(CoalQualityServiceImpl.class);

    private final CoalQualityMapper coalQualityMapper;

    public CoalQualityServiceImpl(CoalQualityMapper coalQualityMapper) {
        this.coalQualityMapper = coalQualityMapper;
    }

    @Override
    public CoalQuality create(CoalQuality coalQuality) {
        coalQualityMapper.insert(coalQuality);
        return coalQuality;
    }

    @Override
    public CoalQuality update(CoalQuality coalQuality) {
        coalQualityMapper.updateById(coalQuality);
        return coalQuality;
    }

    @Override
    public void delete(Long id) {
        coalQualityMapper.deleteById(id);
    }

    @Override
    public CoalQuality getById(Long id) {
        try {
            return coalQualityMapper.selectById(id);
        } catch (Exception ex) {
            log.warn("读取煤质记录失败，返回空结果，id={}", id, ex);
            return null;
        }
    }

    @Override
    public IPage<CoalQuality> page(Integer page, Integer size, String sampleType, String date) {
        Page<CoalQuality> pageInfo = new Page<>(page, size);
        List<CoalQuality> filteredData = filter(sampleType, date);
        List<CoalQuality> pageData = filteredData.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        pageInfo.setRecords(pageData);
        pageInfo.setTotal(filteredData.size());
        return pageInfo;
    }

    @Override
    public List<CoalQuality> list(String sampleType, String date) {
        return filter(sampleType, date);
    }

    @Override
    public List<CoalQuality> getRecent(Integer limit) {
        return safeSelectAll().stream()
                .sorted(Comparator.comparing(CoalQuality::getTestTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<CoalQuality> filter(String sampleType, String date) {
        return safeSelectAll().stream()
                .filter(item -> sampleType == null || sampleType.isBlank() || Objects.equals(item.getSampleType(), sampleType))
                .filter(item -> matchesDate(item.getTestTime() == null ? null : item.getTestTime().toLocalDate(), date))
                .sorted(Comparator.comparing(CoalQuality::getTestTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private List<CoalQuality> safeSelectAll() {
        try {
            return coalQualityMapper.selectList(null);
        } catch (Exception ex) {
            log.warn("煤质分析表不可用，返回空列表", ex);
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
