package com.example.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.CoalQuality;
import com.example.platform.service.CoalQualityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coal-quality")
public class CoalQualityController {

    private final CoalQualityService coalQualityService;

    public CoalQualityController(CoalQualityService coalQualityService) {
        this.coalQualityService = coalQualityService;
    }

    @PostMapping
    public CoalQuality create(@RequestBody CoalQuality coalQuality) {
        return coalQualityService.create(coalQuality);
    }

    @PutMapping
    public CoalQuality update(@RequestBody CoalQuality coalQuality) {
        return coalQualityService.update(coalQuality);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        coalQualityService.delete(id);
    }

    @GetMapping("/{id}")
    public CoalQuality getById(@PathVariable Long id) {
        return coalQualityService.getById(id);
    }

    @GetMapping("/page")
    public IPage<CoalQuality> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sampleType,
            @RequestParam(required = false) String date
    ) {
        return coalQualityService.page(page, size, sampleType, date);
    }

    @GetMapping("/list")
    public List<CoalQuality> list(
            @RequestParam(required = false) String sampleType,
            @RequestParam(required = false) String date
    ) {
        return coalQualityService.list(sampleType, date);
    }

    @GetMapping("/recent")
    public List<CoalQuality> getRecent(@RequestParam(defaultValue = "10") Integer limit) {
        return coalQualityService.getRecent(limit);
    }
}
