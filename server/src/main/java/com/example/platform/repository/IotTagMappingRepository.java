package com.example.platform.repository;

import com.example.platform.entity.IotTagMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IotTagMappingRepository extends JpaRepository<IotTagMappingEntity, Long> {

    Optional<IotTagMappingEntity> findByMappingId(String mappingId);

    boolean existsByMappingId(String mappingId);
}
