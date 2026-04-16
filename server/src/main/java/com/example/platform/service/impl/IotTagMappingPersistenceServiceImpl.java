package com.example.platform.service.impl;

import com.example.platform.dto.iot.IotTagMappingDto;
import com.example.platform.entity.IotTagMappingEntity;
import com.example.platform.repository.IotTagMappingRepository;
import com.example.platform.service.IotTagMappingPersistenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class IotTagMappingPersistenceServiceImpl implements IotTagMappingPersistenceService {

    private final IotTagMappingRepository repository;

    public IotTagMappingPersistenceServiceImpl(IotTagMappingRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IotTagMappingDto> listMappings() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void ensureSeedData(List<IotTagMappingDto> seedMappings) {
        for (IotTagMappingDto mapping : seedMappings) {
            repository.findByMappingId(mapping.mappingId()).orElseGet(() -> repository.save(toEntity(mapping)));
        }
    }

    @Override
    @Transactional
    public IotTagMappingDto createMapping(IotTagMappingDto mapping) {
        String mappingId = hasText(mapping.mappingId()) ? mapping.mappingId() : "MAP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        if (repository.existsByMappingId(mappingId)) {
            throw new IllegalArgumentException("mappingId already exists: " + mappingId);
        }

        IotTagMappingEntity entity = toEntity(new IotTagMappingDto(
                mappingId,
                mapping.tagCode(),
                mapping.businessCode(),
                mapping.businessName(),
                mapping.sourcePath(),
                mapping.transformRule(),
                mapping.enabled() == null ? Boolean.TRUE : mapping.enabled(),
                mapping.remark()
        ));
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public IotTagMappingDto updateMapping(String mappingId, IotTagMappingDto mapping) {
        IotTagMappingEntity entity = repository.findByMappingId(mappingId)
                .orElseThrow(() -> new IllegalArgumentException("mapping not found: " + mappingId));

        entity.setTagCode(mapping.tagCode());
        entity.setBusinessCode(mapping.businessCode());
        entity.setBusinessName(mapping.businessName());
        entity.setSourcePath(mapping.sourcePath());
        entity.setTransformRule(mapping.transformRule());
        entity.setEnabled(mapping.enabled() == null ? entity.getEnabled() : mapping.enabled());
        entity.setRemark(mapping.remark());

        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public IotTagMappingDto updateEnabled(String mappingId, boolean enabled) {
        IotTagMappingEntity entity = repository.findByMappingId(mappingId)
                .orElseThrow(() -> new IllegalArgumentException("mapping not found: " + mappingId));
        entity.setEnabled(enabled);
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void deleteMapping(String mappingId) {
        IotTagMappingEntity entity = repository.findByMappingId(mappingId)
                .orElseThrow(() -> new IllegalArgumentException("mapping not found: " + mappingId));
        repository.delete(entity);
    }

    private IotTagMappingDto toDto(IotTagMappingEntity entity) {
        return new IotTagMappingDto(
                entity.getMappingId(),
                entity.getTagCode(),
                entity.getBusinessCode(),
                entity.getBusinessName(),
                entity.getSourcePath(),
                entity.getTransformRule(),
                entity.getEnabled(),
                entity.getRemark()
        );
    }

    private IotTagMappingEntity toEntity(IotTagMappingDto dto) {
        IotTagMappingEntity entity = new IotTagMappingEntity();
        entity.setMappingId(dto.mappingId());
        entity.setTagCode(dto.tagCode());
        entity.setBusinessCode(dto.businessCode());
        entity.setBusinessName(dto.businessName());
        entity.setSourcePath(dto.sourcePath());
        entity.setTransformRule(dto.transformRule());
        entity.setEnabled(dto.enabled());
        entity.setRemark(dto.remark());
        return entity;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
