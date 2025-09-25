package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 检测记录实体类
 */
@Data
@TableName("detection_record")
public class DetectionRecord {
    
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 摄像头ID
     */
    private Long cameraId;

    /**
     * 摄像头名称
     */
    private String cameraName;

    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 检测时间
     */
    private LocalDateTime detectionTime;
    
    /**
     * 检测结果（JSON格式存储）
     */
    private String detectionResult;
    
    /**
     * 处理状态：0-未处理，1-已处理
     */
    private Integer processed;
    
    /**
     * 处理内容
     */
    private String processContent;
    
    /**
     * 处理图片URL
     */
    private String processImageUrl;
    
    /**
     * 处理时间
     */
    private LocalDateTime processTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}