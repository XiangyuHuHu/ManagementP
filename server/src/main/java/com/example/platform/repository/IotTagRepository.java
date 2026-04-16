package com.example.platform.repository;

import com.example.platform.entity.IotTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IotTagRepository extends JpaRepository<IotTagEntity, Long> {

    Optional<IotTagEntity> findByTagCode(String tagCode);

    boolean existsByTagCode(String tagCode);

    List<IotTagEntity> findByEnabled(Boolean enabled);

    List<IotTagEntity> findByDeviceCode(String deviceCode);
}
