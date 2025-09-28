package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.entity.Camera;
import org.example.vo.camera.CameraVO;

/**
 * 摄像头服务接口
 */
public interface CameraService {
    /**
     * 分页查询摄像头
     */
    Page<CameraVO> pageCamera(Integer current, Integer size, String name, String location, Integer status);

    /**
     * 获取摄像头详情
     */
    CameraVO getCameraById(Long id);

    /**
     * 创建摄像头
     */
    Long createCamera(Camera camera);

    /**
     * 更新摄像头
     */
    void updateCamera(Camera camera);

    /**
     * 删除摄像头
     */
    void deleteCamera(Long id);

    /**
     * 更新摄像头状态
     */
    void updateCameraStatus(Long id, Integer status);
}

