package com.example.platform.repository;

import com.example.platform.entity.IotHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface IotHistoryRepository extends JpaRepository<IotHistoryEntity, Long> {

    @Query(value = """
        SELECT * FROM iot_history
        WHERE tag_code = :tagCode
          AND time >= :startTime
          AND time <= :endTime
        ORDER BY time ASC
        """, nativeQuery = true)
    List<IotHistoryEntity> findByTagCodeAndTimeRange(
            @Param("tagCode") String tagCode,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime
    );

    @Query(value = """
        SELECT
            date_trunc(:interval, time) AS time,
            tag_code,
            AVG(value) AS value,
            MIN(quality) AS quality,
            MAX(source_type) AS source_type,
            NULL AS id
        FROM iot_history
        WHERE tag_code = :tagCode
          AND time >= :startTime
          AND time <= :endTime
        GROUP BY date_trunc(:interval, time), tag_code
        ORDER BY time ASC
        """, nativeQuery = true)
    List<IotHistoryEntity> findAggregated(
            @Param("tagCode") String tagCode,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime,
            @Param("interval") String interval
    );
}
