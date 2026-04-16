package com.example.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.platform.entity.WorkOrder;

import java.util.List;

public interface WorkOrderService {

    WorkOrder create(WorkOrder workOrder);

    WorkOrder update(WorkOrder workOrder);

    void delete(Long id);

    WorkOrder getById(Long id);

    IPage<WorkOrder> page(Integer page, Integer size, String orderNo, String title, String type, String status, Long deviceId, Long assigneeId);

    List<WorkOrder> list(String orderNo, String title, String type, String status, Long deviceId, Long assigneeId);

    WorkOrder getByOrderNo(String orderNo);

    List<WorkOrder> getByDeviceId(Long deviceId);

    List<WorkOrder> getByAssigneeId(Long assigneeId);

    List<WorkOrder> getByCreatorId(Long creatorId);

    List<WorkOrder> getPending();

    List<WorkOrder> getInProgress();

    List<WorkOrder> getRecent(Integer limit);

    WorkOrder startWork(Long id);

    WorkOrder completeWork(Long id, String result);

    WorkOrder closeWork(Long id, String remarks);
}
