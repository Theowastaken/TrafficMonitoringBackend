package org.example.vo.camera;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 摄像头信息VO
 */
@Data
public class CameraVO {
    /** 摄像头ID */
    private Long id;
    /** 摄像头名称 */
    private String name;
    /** 摄像头位置 */
    private String location;
    /** RTSP流地址 */
    private String rtspUrl;
    /** 摄像头状态：0-离线，1-在线 */
    private Integer status;
    /** 是否正在推流 */
    private boolean streaming;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime updateTime;
}

