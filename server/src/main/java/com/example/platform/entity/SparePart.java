package com.example.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 备品备件实体
 */
@Data
@TableName("spare_part")
public class SparePart {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 备件编号
     */
    private String partNo;
    
    /**
     * 备件名称
     */
    private String partName;
    
    /**
     * 规格型号
     */
    private String specification;
    
    /**
     * 所属设备ID
     */
    private Long deviceId;
    
    /**
     * 所属设备名称
     */
    private String deviceName;
    
    /**
     * 库存数量
     */
    private Integer stockQuantity;
    
    /**
     * 安全库存
     */
    private Integer safetyStock;
    
    /**
     * 单位
     */
    private String unit;
    
    /**
     * 存放位置
     */
    private String storageLocation;
    
    /**
     * 供应商
     */
    private String supplier;
    
    /**
     * 单价
     */
    private Double unitPrice;
    
    /**
     * 使用寿命(小时/天)
     */
    private Integer serviceLife;
    
    /**
     * 上次更换日期
     */
    private LocalDate lastReplaceDate;
    
    /**
     * 下次更换日期
     */
    private LocalDate nextReplaceDate;
    
    /**
     * 状态：NORMAL-正常, LOW_STOCK-库存不足, OBSOLETE-报废
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
