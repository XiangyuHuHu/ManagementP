package com.example.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 储装运记录实体
 */
@Data
@TableName("storage_transport")
public class StorageTransport {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 记录类型：RAW_IN-原料煤入厂, PRODUCT_OUT-产品煤销售, LOADING-装车
     */
    private String recordType;
    
    /**
     * 记录编号
     */
    private String recordNo;
    
    /**
     * 煤种名称
     */
    private String coalType;
    
    /**
     * 数量(吨)
     */
    private Double quantity;
    
    /**
     * 时间
     */
    private LocalDateTime recordTime;
    
    /**
     * 来源/去向
     */
    private String sourceOrDest;
    
    /**
     * 运输方式：TRUCK-汽车, TRAIN-火车, BELT-皮带
     */
    private String transportMode;
    
    /**
     * 车号/船号
     */
    private String vehicleNo;
    
    /**
     * 装车站/煤仓
     */
    private String loadingStation;
    
    /**
     * 车次号(火车)
     */
    private String trainNo;
    
    /**
     * 节数(火车)
     */
    private Integer carriageCount;
    
    /**
     * 客户名称
     */
    private String customerName;
    
    /**
     * 合同号
     */
    private String contractNo;
    
    /**
     * 质量指标
     */
    private String qualityIndex;
    
    /**
     * 操作人员
     */
    private String operator;
    
    /**
     * 状态：PENDING-待处理, IN_PROGRESS-进行中, COMPLETED-已完成
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建人
     */
    private String createBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新人
     */
    private String updateBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
