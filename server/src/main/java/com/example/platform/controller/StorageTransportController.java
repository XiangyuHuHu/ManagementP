package com.example.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.StorageTransport;
import com.example.platform.service.StorageTransportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/storage-transport")
public class StorageTransportController {

    private final StorageTransportService storageTransportService;

    public StorageTransportController(StorageTransportService storageTransportService) {
        this.storageTransportService = storageTransportService;
    }

    @PostMapping
    public StorageTransport create(@RequestBody StorageTransport storageTransport) {
        return storageTransportService.create(storageTransport);
    }

    @PutMapping
    public StorageTransport update(@RequestBody StorageTransport storageTransport) {
        return storageTransportService.update(storageTransport);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        storageTransportService.delete(id);
    }

    @GetMapping("/{id}")
    public StorageTransport getById(@PathVariable Long id) {
        return storageTransportService.getById(id);
    }

    @GetMapping("/page")
    public IPage<StorageTransport> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String recordType,
            @RequestParam(required = false) String transportMode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date
    ) {
        return storageTransportService.page(page, size, recordType, transportMode, status, date);
    }

    @GetMapping("/list")
    public List<StorageTransport> list(
            @RequestParam(required = false) String recordType,
            @RequestParam(required = false) String transportMode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date
    ) {
        return storageTransportService.list(recordType, transportMode, status, date);
    }

    @GetMapping("/recent")
    public List<StorageTransport> getRecent(@RequestParam(defaultValue = "10") Integer limit) {
        return storageTransportService.getRecent(limit);
    }
}
