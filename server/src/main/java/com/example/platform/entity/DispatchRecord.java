package com.example.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 调度记录实体
 */
@Data
@TableName("dispatch_record")
public class DispatchRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 记录编号
     */
    private String recordNo;
    
    /**
     * 班次：DAY-白班, NIGHT-夜班
     */
    private String shift;
    
    /**
     * 记录类型：OPERATION-运转情况, ACCIDENT-事故问题, INSTRUCTION-上传下达, LEADER-领导指示, PENDING-遗留问题
     */
    private String recordType;
    
    /**
     * 记录内容
     */
    private String content;
    
    /**
     * 处理情况
     */
    private String handling;
    
    /**
     * 记录人
     */
    private String recorder;
    
    /**
     * 记录时间
     */
    private LocalDateTime recordTime;
    
    /**
     * 处理人
     */
    private String handler;
    
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    
    /**
     * 状态：PENDING-待处理, PROCESSING-处理中, COMPLETED-已完成
     */
    private String status;
    
    /**
     * 优先级：HIGH-高, MEDIUM-中, LOW-低
     */
    private String priority;
    
    /**
     * 附件URL
     */
    private String attachmentUrl;
    
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
