package com.example.platform.repository;

import com.example.platform.entity.IotRealtimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface IotRealtimeRepository extends JpaRepository<IotRealtimeEntity, String>, IotRealtimeCustomRepository {

    List<IotRealtimeEntity> findByTagCodeIn(Collection<String> tagCodes);

    List<IotRealtimeEntity> findByDeviceCode(String deviceCode);

    List<IotRealtimeEntity> findByAreaCode(String areaCode);
}
