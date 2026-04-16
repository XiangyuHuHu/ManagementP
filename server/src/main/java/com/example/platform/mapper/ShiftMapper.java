package com.example.platform.mapper;

import com.example.platform.entity.Shift;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ShiftMapper {

    List<Shift> findAll();
    Shift findById(Long id);
    Shift findByShiftId(String shiftId);
    void insert(Shift shift);
    void update(Shift shift);
    void delete(Long id);
    List<Shift> findByStatus(String status);
}
