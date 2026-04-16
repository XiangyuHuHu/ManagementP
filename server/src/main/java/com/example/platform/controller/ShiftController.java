package com.example.platform.controller;

import com.example.platform.entity.Shift;
import com.example.platform.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/report/shift")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping
    public List<Shift> findAll() {
        return shiftService.findAll();
    }

    @GetMapping("/{id}")
    public Shift findById(@PathVariable Long id) {
        return shiftService.findById(id);
    }

    @GetMapping("/shiftId/{shiftId}")
    public Shift findByShiftId(@PathVariable String shiftId) {
        return shiftService.findByShiftId(shiftId);
    }

    @PostMapping
    public void create(@RequestBody Shift shift) {
        shiftService.create(shift);
    }

    @PutMapping
    public void update(@RequestBody Shift shift) {
        shiftService.update(shift);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        shiftService.delete(id);
    }

    @GetMapping("/status/{status}")
    public List<Shift> findByStatus(@PathVariable String status) {
        return shiftService.findByStatus(status);
    }
}
