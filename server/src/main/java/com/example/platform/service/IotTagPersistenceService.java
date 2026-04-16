package com.example.platform.service;

import com.example.platform.dto.iot.IotTagDefinitionDto;

import java.util.List;
import java.util.Map;

public interface IotTagPersistenceService {

    void ensureSeedData(List<IotTagDefinitionDto> seedTags);

    Map<String, Object> listTags(String keyword, String deviceCode, String areaCode, Boolean enabled, String sourceType, Integer pageNum, Integer pageSize);

    IotTagDefinitionDto getTag(String tagCode);

    List<IotTagDefinitionDto> getDeviceTags(String deviceCode);

    IotTagDefinitionDto createTag(IotTagDefinitionDto tag);

    IotTagDefinitionDto updateTag(String tagCode, IotTagDefinitionDto tag);

    IotTagDefinitionDto updateEnabled(String tagCode, boolean enabled);

    void deleteTag(String tagCode);
}
