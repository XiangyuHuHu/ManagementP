package com.example.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.EnergyConsumption;
import com.example.platform.service.EnergyConsumptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/energy-consumption")
public class EnergyConsumptionController {

    private final EnergyConsumptionService energyConsumptionService;

    public EnergyConsumptionController(EnergyConsumptionService energyConsumptionService) {
        this.energyConsumptionService = energyConsumptionService;
    }

    @PostMapping
    public EnergyConsumption create(@RequestBody EnergyConsumption energyConsumption) {
        return energyConsumptionService.create(energyConsumption);
    }

    @PutMapping
    public EnergyConsumption update(@RequestBody EnergyConsumption energyConsumption) {
        return energyConsumptionService.update(energyConsumption);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        energyConsumptionService.delete(id);
    }

    @GetMapping("/{id}")
    public EnergyConsumption getById(@PathVariable Long id) {
        return energyConsumptionService.getById(id);
    }

    @GetMapping("/page")
    public IPage<EnergyConsumption> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String energyType,
            @RequestParam(required = false) String shift,
            @RequestParam(required = false) String date
    ) {
        return energyConsumptionService.page(page, size, energyType, shift, date);
    }

    @GetMapping("/list")
    public List<EnergyConsumption> list(
            @RequestParam(required = false) String energyType,
            @RequestParam(required = false) String shift,
            @RequestParam(required = false) String date
    ) {
        return energyConsumptionService.list(energyType, shift, date);
    }

    @GetMapping("/recent")
    public List<EnergyConsumption> getRecent(@RequestParam(defaultValue = "10") Integer limit) {
        return energyConsumptionService.getRecent(limit);
    }
}
