package com.example.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.platform.entity.EnergyConsumption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface EnergyConsumptionMapper extends BaseMapper<EnergyConsumption> {

    @Select("""
            SELECT COALESCE(SUM(consumption), 0)
            FROM energy_consumption
            WHERE record_date IS NOT NULL
              AND record_date >= #{startInclusive}
              AND record_date <= #{endInclusive}
            """)
    Double sumConsumptionBetween(
            @Param("startInclusive") LocalDate startInclusive,
            @Param("endInclusive") LocalDate endInclusive
    );
}
