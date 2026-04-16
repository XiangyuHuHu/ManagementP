package com.example.platform.service;

import com.example.platform.entity.Shift;
import java.util.List;

public interface ShiftService {

    List<Shift> findAll();
    Shift findById(Long id);
    Shift findByShiftId(String shiftId);
    void create(Shift shift);
    void update(Shift shift);
    void delete(Long id);
    List<Shift> findByStatus(String status);
}
