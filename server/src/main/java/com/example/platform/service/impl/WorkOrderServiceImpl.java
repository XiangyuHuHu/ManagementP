package com.example.platform.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.platform.entity.WorkOrder;
import com.example.platform.mapper.WorkOrderMapper;
import com.example.platform.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Override
    public WorkOrder create(WorkOrder workOrder) {
        workOrder.setCreatedAt(LocalDateTime.now());
        workOrder.setUpdatedAt(LocalDateTime.now());
        workOrderMapper.insert(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder update(WorkOrder workOrder) {
        workOrder.setUpdatedAt(LocalDateTime.now());
        workOrderMapper.updateById(workOrder);
        return workOrder;
    }

    @Override
    public void delete(Long id) {
        workOrderMapper.deleteById(id);
    }

    @Override
    public WorkOrder getById(Long id) {
        return workOrderMapper.selectById(id);
    }

    @Override
    public IPage<WorkOrder> page(Integer page, Integer size, String orderNo, String title, String type, String status, Long deviceId, Long assigneeId) {
        Page<WorkOrder> pageInfo = new Page<>(page, size);
        List<WorkOrder> allData = workOrderMapper.selectList(null);
        
        List<WorkOrder> filteredData = allData.stream()
                .filter(order -> {
                    boolean match = true;
                    if (orderNo != null && !order.getOrderNo().contains(orderNo)) match = false;
                    if (title != null && !order.getTitle().contains(title)) match = false;
                    if (type != null && !order.getType().equals(type)) match = false;
                    if (status != null && !order.getStatus().equals(status)) match = false;
                    if (deviceId != null && !order.getDeviceId().equals(deviceId)) match = false;
                    if (assigneeId != null && !order.getAssigneeId().equals(assigneeId)) match = false;
                    return match;
                })
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
        
        int total = filteredData.size();
        List<WorkOrder> pageData = filteredData.stream()
                .skip((page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
        
        pageInfo.setRecords(pageData);
        pageInfo.setTotal(total);
        return pageInfo;
    }

    @Override
    public List<WorkOrder> list(String orderNo, String title, String type, String status, Long deviceId, Long assigneeId) {
        return workOrderMapper.selectList(null).stream()
                .filter(order -> {
                    boolean match = true;
                    if (orderNo != null && !order.getOrderNo().contains(orderNo)) match = false;
                    if (title != null && !order.getTitle().contains(title)) match = false;
                    if (type != null && !order.getType().equals(type)) match = false;
                    if (status != null && !order.getStatus().equals(status)) match = false;
                    if (deviceId != null && !order.getDeviceId().equals(deviceId)) match = false;
                    if (assigneeId != null && !order.getAssigneeId().equals(assigneeId)) match = false;
                    return match;
                })
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    @Override
    public WorkOrder getByOrderNo(String orderNo) {
        return workOrderMapper.selectList(null).stream()
                .filter(order -> order.getOrderNo().equals(orderNo))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<WorkOrder> getByDeviceId(Long deviceId) {
        return workOrderMapper.selectList(null).stream()
                .filter(order -> order.getDeviceId() != null && order.getDeviceId().equals(deviceId))
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkOrder> getByAssigneeId(Long assigneeId) {
        return workOrderMapper.selectList(null).stream()
                .filter(order -> order.getAssigneeId() != null && order.getAssigneeId().equals(assigneeId))
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkOrder> getByCreatorId(Long creatorId) {
        return workOrderMapper.selectList(null).stream()
                .filter(order -> order.getCreatorId() != null && order.getCreatorId().equals(creatorId))
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkOrder> getPending() {
        return workOrderMapper.selectList(null).stream()
                .filter(order -> "待处理".equals(order.getStatus()))
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkOrder> getInProgress() {
        return workOrderMapper.selectList(null).stream()
                .filter(order -> "处理中".equals(order.getStatus()))
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkOrder> getRecent(Integer limit) {
        return workOrderMapper.selectList(null).stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null || b.getCreatedAt() == null) return 0;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public WorkOrder startWork(Long id) {
        WorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder != null) {
            workOrder.setStatus("处理中");
            workOrder.setActualStartTime(LocalDateTime.now());
            workOrder.setUpdatedAt(LocalDateTime.now());
            workOrderMapper.updateById(workOrder);
        }
        return workOrder;
    }

    @Override
    public WorkOrder completeWork(Long id, String result) {
        WorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder != null) {
            workOrder.setStatus("已完成");
            workOrder.setResult(result);
            workOrder.setActualEndTime(LocalDateTime.now());
            workOrder.setUpdatedAt(LocalDateTime.now());
            workOrderMapper.updateById(workOrder);
        }
        return workOrder;
    }

    @Override
    public WorkOrder closeWork(Long id, String remarks) {
        WorkOrder workOrder = workOrderMapper.selectById(id);
        if (workOrder != null) {
            workOrder.setStatus("已关闭");
            workOrder.setRemarks(remarks);
            workOrder.setUpdatedAt(LocalDateTime.now());
            workOrderMapper.updateById(workOrder);
        }
        return workOrder;
    }
}
