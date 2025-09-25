package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 摄像头实体类
 */
@Data
@TableName("camera")
public class Camera {
    
    /**
     * 摄像头ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 摄像头名称
     */
    private String name;
    
    /**
     * 摄像头位置
     */
    private String location;

    /**
     * RTSP流地址
     */
    private String rtspUrl;
    
    /**
     * 摄像头状态：0-离线，1-在线
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 是否正在推流
     */
    private boolean streaming;
}