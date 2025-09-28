package org.example.dto.camera;

import lombok.Data;

/**
 * 摄像头查询参数DTO
 */
@Data
public class CameraQueryDto {
    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 摄像头名称
     */
    private String name;

    /**
     * 摄像头位置
     */
    private String location;

    /**
     * 摄像头状态：0-离线，1-在线
     */
    private Integer status;
}

