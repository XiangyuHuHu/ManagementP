package com.example.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 煤质检测数据实体
 */
@Data
@TableName("coal_quality")
public class CoalQuality {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 检测编号
     */
    private String testNo;
    
    /**
     * 样品类型：RAW-原煤, PRODUCT-产品煤
     */
    private String sampleType;
    
    /**
     * 样品名称
     */
    private String sampleName;
    
    /**
     * 采样时间
     */
    private LocalDateTime sampleTime;
    
    /**
     * 班次：DAY-白班, NIGHT-夜班
     */
    private String shift;
    
    /**
     * 系统/工艺环节
     */
    private String systemName;
    
    /**
     * 灰分(%)
     */
    private Double ashContent;
    
    /**
     * 水分(%)
     */
    private Double moisture;
    
    /**
     * 硫分(%)
     */
    private Double sulfur;
    
    /**
     * 发热量(MJ/kg)
     */
    private Double calorificValue;
    
    /**
     * 挥发分(%)
     */
    private Double volatileMatter;
    
    /**
     * 固定碳(%)
     */
    private Double fixedCarbon;
    
    /**
     * 密度(g/cm³)
     */
    private Double density;
    
    /**
     * 粒度(mm)
     */
    private String particleSize;
    
    /**
     * 检测人
     */
    private String tester;
    
    /**
     * 检测时间
     */
    private LocalDateTime testTime;
    
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
