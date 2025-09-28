package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.result.Result;
import org.example.dto.camera.CameraQueryDto;
import org.example.dto.camera.CameraStatusUpdateDto;
import org.example.entity.Camera;
import org.example.service.CameraService;
import org.example.vo.camera.CameraVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 摄像头控制器
 */
@RestController
@RequestMapping("/camera")
public class CameraController {

    @Autowired
    private CameraService cameraService;

    /**
     * 分页查询摄像头
     */
    @GetMapping("/page")
    public Result<Page<CameraVO>> pageCamera(CameraQueryDto queryDto) {
        Page<CameraVO> cameraPage = cameraService.pageCamera(queryDto);
        return Result.success(cameraPage);
    }

    /**
     * 获取摄像头详情
     */
    @GetMapping("/{id}")
    public Result<CameraVO> getCameraById(@PathVariable Long id) {
        CameraVO camera = cameraService.getCameraById(id);
        return Result.success(camera);
    }

    /**
     * 创建摄像头
     */
    @PostMapping
    public Result<Long> createCamera(@RequestBody Camera camera) {
        Long id = cameraService.createCamera(camera);
        return Result.success(id);
    }

    /**
     * 更新摄像头
     */
    @PutMapping
    public Result<Void> updateCamera(@RequestBody Camera camera) {
        cameraService.updateCamera(camera);
        return Result.success();
    }

    /**
     * 删除摄像头
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCamera(@PathVariable Long id) {
        cameraService.deleteCamera(id);
        return Result.success();
    }

    /**
     * 更新摄像头状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateCameraStatus(@PathVariable Long id, @RequestBody CameraStatusUpdateDto params) {
        Integer status = params.getStatus();
        cameraService.updateCameraStatus(id, status);
        return Result.success();
    }
}