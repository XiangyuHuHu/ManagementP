package com.example.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.WorkOrder;
import com.example.platform.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-order")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    @PostMapping
    public WorkOrder create(@RequestBody WorkOrder workOrder) {
        return workOrderService.create(workOrder);
    }

    @PutMapping
    public WorkOrder update(@RequestBody WorkOrder workOrder) {
        return workOrderService.update(workOrder);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        workOrderService.delete(id);
    }

    @GetMapping("/{id}")
    public WorkOrder getById(@PathVariable Long id) {
        return workOrderService.getById(id);
    }

    @GetMapping("/page")
    public IPage<WorkOrder> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Long assigneeId
    ) {
        return workOrderService.page(page, size, orderNo, title, type, status, deviceId, assigneeId);
    }

    @GetMapping("/list")
    public List<WorkOrder> list(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Long assigneeId
    ) {
        return workOrderService.list(orderNo, title, type, status, deviceId, assigneeId);
    }

    @GetMapping("/by-order-no/{orderNo}")
    public WorkOrder getByOrderNo(@PathVariable String orderNo) {
        return workOrderService.getByOrderNo(orderNo);
    }

    @GetMapping("/by-device/{deviceId}")
    public List<WorkOrder> getByDeviceId(@PathVariable Long deviceId) {
        return workOrderService.getByDeviceId(deviceId);
    }

    @GetMapping("/by-assignee/{assigneeId}")
    public List<WorkOrder> getByAssigneeId(@PathVariable Long assigneeId) {
        return workOrderService.getByAssigneeId(assigneeId);
    }

    @GetMapping("/by-creator/{creatorId}")
    public List<WorkOrder> getByCreatorId(@PathVariable Long creatorId) {
        return workOrderService.getByCreatorId(creatorId);
    }

    @GetMapping("/pending")
    public List<WorkOrder> getPending() {
        return workOrderService.getPending();
    }

    @GetMapping("/in-progress")
    public List<WorkOrder> getInProgress() {
        return workOrderService.getInProgress();
    }

    @GetMapping("/recent")
    public List<WorkOrder> getRecent(@RequestParam(defaultValue = "10") Integer limit) {
        return workOrderService.getRecent(limit);
    }

    @PostMapping("/start/{id}")
    public WorkOrder startWork(@PathVariable Long id) {
        return workOrderService.startWork(id);
    }

    @PostMapping("/complete/{id}")
    public WorkOrder completeWork(@PathVariable Long id, @RequestParam String result) {
        return workOrderService.completeWork(id, result);
    }

    @PostMapping("/close/{id}")
    public WorkOrder closeWork(@PathVariable Long id, @RequestParam String remarks) {
        return workOrderService.closeWork(id, remarks);
    }
}
