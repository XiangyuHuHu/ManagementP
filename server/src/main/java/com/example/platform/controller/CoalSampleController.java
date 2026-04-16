package com.example.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.CoalSample;
import com.example.platform.service.CoalSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coal-sample")
public class CoalSampleController {

    @Autowired
    private CoalSampleService coalSampleService;

    @PostMapping
    public CoalSample create(@RequestBody CoalSample coalSample) {
        return coalSampleService.create(coalSample);
    }

    @PutMapping
    public CoalSample update(@RequestBody CoalSample coalSample) {
        return coalSampleService.update(coalSample);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        coalSampleService.delete(id);
    }

    @GetMapping("/{id}")
    public CoalSample getById(@PathVariable Long id) {
        return coalSampleService.getById(id);
    }

    @GetMapping("/page")
    public IPage<CoalSample> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sampleNo,
            @RequestParam(required = false) String sampleName,
            @RequestParam(required = false) String status
    ) {
        return coalSampleService.page(page, size, sampleNo, sampleName, status);
    }

    @GetMapping("/list")
    public List<CoalSample> list(
            @RequestParam(required = false) String sampleNo,
            @RequestParam(required = false) String sampleName,
            @RequestParam(required = false) String status
    ) {
        return coalSampleService.list(sampleNo, sampleName, status);
    }

    @GetMapping("/by-sample-no/{sampleNo}")
    public CoalSample getBySampleNo(@PathVariable String sampleNo) {
        return coalSampleService.getBySampleNo(sampleNo);
    }

    @GetMapping("/by-batch/{batchNo}")
    public List<CoalSample> getByBatchNo(@PathVariable String batchNo) {
        return coalSampleService.getByBatchNo(batchNo);
    }

    @GetMapping("/recent")
    public List<CoalSample> getRecent(@RequestParam(defaultValue = "10") Integer limit) {
        return coalSampleService.getRecent(limit);
    }
}
