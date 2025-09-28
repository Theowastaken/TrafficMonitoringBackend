package org.example.dto.camera;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 摄像头状态更新DTO
 */
@Data
public class CameraStatusUpdateDto {
    /**
     * 摄像头状态：0-离线，1-在线
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}

