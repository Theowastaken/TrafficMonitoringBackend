package org.example.dto.detection;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 添加检测记录DTO
 */
@Data
public class DetectionRecordAddDto {
    
    @NotNull(message = "摄像头ID不能为空")
    private Long cameraId;
    
    @NotNull(message = "图片不能为空")
    private String imageBase64;
    
    private LocalDateTime detectionTime;
    
    @NotNull(message = "检测结果不能为空")
    private String detectionResult;
}