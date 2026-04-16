package com.example.platform.service.impl;

import com.example.platform.entity.Shift;
import com.example.platform.mapper.ShiftMapper;
import com.example.platform.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShiftServiceImpl implements ShiftService {

    @Autowired
    private ShiftMapper shiftMapper;

    @Override
    public List<Shift> findAll() {
        return shiftMapper.findAll();
    }

    @Override
    public Shift findById(Long id) {
        return shiftMapper.findById(id);
    }

    @Override
    public Shift findByShiftId(String shiftId) {
        return shiftMapper.findByShiftId(shiftId);
    }

    @Override
    public void create(Shift shift) {
        shiftMapper.insert(shift);
    }

    @Override
    public void update(Shift shift) {
        shiftMapper.update(shift);
    }

    @Override
    public void delete(Long id) {
        shiftMapper.delete(id);
    }

    @Override
    public List<Shift> findByStatus(String status) {
        return shiftMapper.findByStatus(status);
    }
}
