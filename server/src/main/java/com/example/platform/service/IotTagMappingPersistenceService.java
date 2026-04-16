package com.example.platform.service;

import com.example.platform.dto.iot.IotTagMappingDto;

import java.util.List;

public interface IotTagMappingPersistenceService {

    List<IotTagMappingDto> listMappings();

    void ensureSeedData(List<IotTagMappingDto> seedMappings);

    IotTagMappingDto createMapping(IotTagMappingDto mapping);

    IotTagMappingDto updateMapping(String mappingId, IotTagMappingDto mapping);

    IotTagMappingDto updateEnabled(String mappingId, boolean enabled);

    void deleteMapping(String mappingId);
}
