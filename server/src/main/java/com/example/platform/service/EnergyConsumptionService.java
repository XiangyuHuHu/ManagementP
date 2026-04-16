package com.example.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.EnergyConsumption;

import java.time.LocalDate;
import java.util.List;

public interface EnergyConsumptionService {

    EnergyConsumption create(EnergyConsumption energyConsumption);

    EnergyConsumption update(EnergyConsumption energyConsumption);

    void delete(Long id);

    EnergyConsumption getById(Long id);

    IPage<EnergyConsumption> page(Integer page, Integer size, String energyType, String shift, String date);

    List<EnergyConsumption> list(String energyType, String shift, String date);

    List<EnergyConsumption> getRecent(Integer limit);

    /** 按记录日期汇总用量（含首尾两天） */
    double sumConsumptionBetween(LocalDate startInclusive, LocalDate endInclusive);
}
