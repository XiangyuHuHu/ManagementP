package com.example.platform.service.impl;

import com.example.platform.dto.iot.IotTagDefinitionDto;
import com.example.platform.entity.IotTagEntity;
import com.example.platform.repository.IotTagRepository;
import com.example.platform.service.IotTagPersistenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class IotTagPersistenceServiceImpl implements IotTagPersistenceService {

    private final IotTagRepository repository;

    public IotTagPersistenceServiceImpl(IotTagRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void ensureSeedData(List<IotTagDefinitionDto> seedTags) {
        for (IotTagDefinitionDto tag : seedTags) {
            IotTagEntity entity = repository.findByTagCode(tag.tagCode()).orElseGet(IotTagEntity::new);
            applyDto(entity, tag);
            repository.save(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> listTags(String keyword, String deviceCode, String areaCode, Boolean enabled, String sourceType, Integer pageNum, Integer pageSize) {
        List<IotTagDefinitionDto> filtered = repository.findAll().stream()
                .map(this::toDto)
                .filter(tag -> isBlank(keyword) || containsIgnoreCase(tag.tagCode(), keyword) || containsIgnoreCase(tag.tagName(), keyword))
                .filter(tag -> isBlank(deviceCode) || Objects.equals(tag.deviceCode(), deviceCode))
                .filter(tag -> isBlank(areaCode) || Objects.equals(tag.areaCode(), areaCode))
                .filter(tag -> enabled == null || Objects.equals(tag.enabled(), enabled))
                .filter(tag -> isBlank(sourceType) || Objects.equals(tag.sourceType(), sourceType))
                .toList();

        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int fromIndex = Math.min((safePageNum - 1) * safePageSize, filtered.size());
        int toIndex = Math.min(fromIndex + safePageSize, filtered.size());

        Map<String, Object> result = new HashMap<>();
        result.put("total", filtered.size());
        result.put("records", filtered.subList(fromIndex, toIndex));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public IotTagDefinitionDto getTag(String tagCode) {
        return repository.findByTagCode(tagCode).map(this::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IotTagDefinitionDto> getDeviceTags(String deviceCode) {
        return repository.findAll().stream()
                .map(this::toDto)
                .filter(tag -> Objects.equals(tag.deviceCode(), deviceCode))
                .toList();
    }

    @Override
    @Transactional
    public IotTagDefinitionDto createTag(IotTagDefinitionDto tag) {
        if (isBlank(tag.tagCode())) {
            throw new IllegalArgumentException("tagCode is required");
        }
        if (repository.existsByTagCode(tag.tagCode())) {
            throw new IllegalArgumentException("tagCode already exists: " + tag.tagCode());
        }
        IotTagEntity entity = toEntity(tag);
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public IotTagDefinitionDto updateTag(String tagCode, IotTagDefinitionDto tag) {
        IotTagEntity entity = repository.findByTagCode(tagCode)
                .orElseThrow(() -> new IllegalArgumentException("tag not found: " + tagCode));
        applyDto(entity, new IotTagDefinitionDto(
                tagCode,
                tag.tagName(),
                tag.sourceType(),
                tag.sourcePath(),
                tag.deviceCode(),
                tag.deviceName(),
                tag.areaCode(),
                tag.dataType(),
                tag.unit(),
                tag.scanRate(),
                tag.deadband(),
                tag.qualityRule(),
                tag.enabled(),
                tag.remark()
        ));
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public IotTagDefinitionDto updateEnabled(String tagCode, boolean enabled) {
        IotTagEntity entity = repository.findByTagCode(tagCode)
                .orElseThrow(() -> new IllegalArgumentException("tag not found: " + tagCode));
        entity.setEnabled(enabled);
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public void deleteTag(String tagCode) {
        IotTagEntity entity = repository.findByTagCode(tagCode)
                .orElseThrow(() -> new IllegalArgumentException("tag not found: " + tagCode));
        repository.delete(entity);
    }

    private IotTagDefinitionDto toDto(IotTagEntity entity) {
        return new IotTagDefinitionDto(
                entity.getTagCode(),
                entity.getTagName(),
                entity.getSourceType(),
                entity.getSourcePath(),
                entity.getDeviceCode(),
                entity.getDeviceName(),
                entity.getAreaCode(),
                entity.getDataType(),
                entity.getUnit(),
                entity.getScanRate(),
                entity.getDeadband(),
                entity.getQualityRule(),
                entity.getEnabled(),
                entity.getRemark()
        );
    }

    private IotTagEntity toEntity(IotTagDefinitionDto dto) {
        IotTagEntity entity = new IotTagEntity();
        applyDto(entity, dto);
        return entity;
    }

    private void applyDto(IotTagEntity entity, IotTagDefinitionDto dto) {
        entity.setTagCode(dto.tagCode());
        entity.setTagName(dto.tagName());
        entity.setSourceType(dto.sourceType());
        entity.setSourcePath(dto.sourcePath());
        entity.setDeviceCode(dto.deviceCode());
        entity.setDeviceName(dto.deviceName());
        entity.setAreaCode(dto.areaCode());
        entity.setDataType(dto.dataType());
        entity.setUnit(dto.unit());
        entity.setScanRate(dto.scanRate());
        entity.setDeadband(dto.deadband());
        entity.setQualityRule(dto.qualityRule());
        entity.setEnabled(dto.enabled() == null ? Boolean.TRUE : dto.enabled());
        entity.setRemark(dto.remark());
    }

    private static boolean containsIgnoreCase(String source, String keyword) {
        return source != null && keyword != null && source.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
