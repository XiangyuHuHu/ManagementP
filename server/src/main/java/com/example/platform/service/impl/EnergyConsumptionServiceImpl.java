package com.example.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.platform.entity.EnergyConsumption;
import com.example.platform.mapper.EnergyConsumptionMapper;
import com.example.platform.service.EnergyConsumptionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EnergyConsumptionServiceImpl implements EnergyConsumptionService {

    private final EnergyConsumptionMapper energyConsumptionMapper;

    public EnergyConsumptionServiceImpl(EnergyConsumptionMapper energyConsumptionMapper) {
        this.energyConsumptionMapper = energyConsumptionMapper;
    }

    @Override
    public EnergyConsumption create(EnergyConsumption energyConsumption) {
        energyConsumptionMapper.insert(energyConsumption);
        return energyConsumption;
    }

    @Override
    public EnergyConsumption update(EnergyConsumption energyConsumption) {
        energyConsumptionMapper.updateById(energyConsumption);
        return energyConsumption;
    }

    @Override
    public void delete(Long id) {
        energyConsumptionMapper.deleteById(id);
    }

    @Override
    public EnergyConsumption getById(Long id) {
        return energyConsumptionMapper.selectById(id);
    }

    @Override
    public IPage<EnergyConsumption> page(Integer page, Integer size, String energyType, String shift, String date) {
        Page<EnergyConsumption> pageInfo = new Page<>(page, size);
        List<EnergyConsumption> filteredData = filter(energyType, shift, date);
        List<EnergyConsumption> pageData = filteredData.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        pageInfo.setRecords(pageData);
        pageInfo.setTotal(filteredData.size());
        return pageInfo;
    }

    @Override
    public List<EnergyConsumption> list(String energyType, String shift, String date) {
        return filter(energyType, shift, date);
    }

    @Override
    public List<EnergyConsumption> getRecent(Integer limit) {
        return energyConsumptionMapper.selectList(null).stream()
                .sorted(Comparator.comparing(EnergyConsumption::getRecordDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<EnergyConsumption> filter(String energyType, String shift, String date) {
        return energyConsumptionMapper.selectList(null).stream()
                .filter(item -> energyType == null || energyType.isBlank() || Objects.equals(item.getEnergyType(), energyType))
                .filter(item -> shift == null || shift.isBlank() || Objects.equals(item.getShift(), shift))
                .filter(item -> matchesDate(item.getRecordDate(), date))
                .sorted(Comparator.comparing(EnergyConsumption::getRecordDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private boolean matchesDate(LocalDate recordDate, String date) {
        if (date == null || date.isBlank()) {
            return true;
        }
        return recordDate != null && date.equals(recordDate.toString());
    }

    @Override
    public double sumConsumptionBetween(LocalDate startInclusive, LocalDate endInclusive) {
        Double v = energyConsumptionMapper.sumConsumptionBetween(startInclusive, endInclusive);
        return v != null ? v : 0.0;
    }
}
