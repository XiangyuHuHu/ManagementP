package com.example.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.CoalQuality;

import java.util.List;

public interface CoalQualityService {

    CoalQuality create(CoalQuality coalQuality);

    CoalQuality update(CoalQuality coalQuality);

    void delete(Long id);

    CoalQuality getById(Long id);

    IPage<CoalQuality> page(Integer page, Integer size, String sampleType, String date);

    List<CoalQuality> list(String sampleType, String date);

    List<CoalQuality> getRecent(Integer limit);
}
