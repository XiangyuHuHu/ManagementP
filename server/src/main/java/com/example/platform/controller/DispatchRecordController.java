package com.example.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.DispatchRecord;
import com.example.platform.service.DispatchRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatch-record")
public class DispatchRecordController {

    private final DispatchRecordService dispatchRecordService;

    public DispatchRecordController(DispatchRecordService dispatchRecordService) {
        this.dispatchRecordService = dispatchRecordService;
    }

    @PostMapping
    public DispatchRecord create(@RequestBody DispatchRecord dispatchRecord) {
        return dispatchRecordService.create(dispatchRecord);
    }

    @PutMapping
    public DispatchRecord update(@RequestBody DispatchRecord dispatchRecord) {
        return dispatchRecordService.update(dispatchRecord);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dispatchRecordService.delete(id);
    }

    @GetMapping("/{id}")
    public DispatchRecord getById(@PathVariable Long id) {
        return dispatchRecordService.getById(id);
    }

    @GetMapping("/page")
    public IPage<DispatchRecord> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String shift,
            @RequestParam(required = false) String recordType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date
    ) {
        return dispatchRecordService.page(page, size, shift, recordType, status, date);
    }

    @GetMapping("/list")
    public List<DispatchRecord> list(
            @RequestParam(required = false) String shift,
            @RequestParam(required = false) String recordType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date
    ) {
        return dispatchRecordService.list(shift, recordType, status, date);
    }

    @GetMapping("/recent")
    public List<DispatchRecord> getRecent(@RequestParam(defaultValue = "10") Integer limit) {
        return dispatchRecordService.getRecent(limit);
    }
}
