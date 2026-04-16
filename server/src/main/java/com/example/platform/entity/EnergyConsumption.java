package com.example.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 能耗记录实体
 */
@Data
@TableName("energy_consumption")
public class EnergyConsumption {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 记录日期
     */
    private LocalDate recordDate;
    
    /**
     * 班次：DAY-白班, NIGHT-夜班
     */
    private String shift;
    
    /**
     * 能耗类型：WATER-水, ELECTRICITY-电, MEDICINE-药, MEDIUM-介, OIL-油
     */
    private String energyType;
    
    /**
     * 用量
     */
    private Double consumption;
    
    /**
     * 单位
     */
    private String unit;
    
    /**
     * 单价
     */
    private Double unitPrice;
    
    /**
     * 费用
     */
    private Double cost;
    
    /**
     * 关联产量(吨)
     */
    private Double relatedOutput;
    
    /**
     * 单耗(单位用量/吨煤)
     */
    private Double unitConsumption;
    
    /**
     * 计量表读数
     */
    private Double meterReading;
    
    /**
     * 计量表号
     */
    private String meterNo;
    
    /**
     * 记录人
     */
    private String recorder;
    
    /**
     * 数据来源：MANUAL-手动录入, AUTO-自动采集
     */
    private String dataSource;
    
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
