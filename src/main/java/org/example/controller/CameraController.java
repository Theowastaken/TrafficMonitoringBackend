package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.result.Result;
import org.example.entity.Camera;
import org.example.mapper.CameraMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 摄像头控制器
 */
@RestController
@RequestMapping("/camera")
public class CameraController {
    
    @Autowired
    private CameraMapper cameraMapper;
    
    /**
     * 分页查询摄像头
     */
    @GetMapping("/page")
    public Result<Page<Camera>> pageCamera(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer status) {
        
        Page<Camera> page = new Page<>(current, size);
        QueryWrapper<Camera> wrapper = new QueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like("name", name);
        }
        if (location != null && !location.trim().isEmpty()) {
            wrapper.like("location", location);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        
        wrapper.orderByDesc("create_time");
        Page<Camera> cameraPage = cameraMapper.selectPage(page, wrapper);
        
        return Result.success(cameraPage);
    }
    
    /**
     * 获取摄像头详情
     */
    @GetMapping("/{id}")
    public Result<Camera> getCameraById(@PathVariable Long id) {
        Camera camera = cameraMapper.selectById(id);
        if (camera == null) {
            return Result.error("摄像头不存在");
        }
        return Result.success(camera);
    }
    
    /**
     * 创建摄像头
     */
    @PostMapping
    public Result<Long> createCamera(@RequestBody Camera camera) {
        camera.setCreateTime(LocalDateTime.now());
        camera.setUpdateTime(LocalDateTime.now());
        cameraMapper.insert(camera);
        return Result.success(camera.getId());
    }
    
    /**
     * 更新摄像头
     */
    @PutMapping
    public Result<Void> updateCamera(@RequestBody Camera camera) {
        Camera existCamera = cameraMapper.selectById(camera.getId());
        if (existCamera == null) {
            return Result.error("摄像头不存在");
        }
        camera.setUpdateTime(LocalDateTime.now());
        cameraMapper.updateById(camera);
        return Result.success();
    }
    
    /**
     * 删除摄像头
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCamera(@PathVariable Long id) {
        Camera camera = cameraMapper.selectById(id);
        if (camera == null) {
            return Result.error("摄像头不存在");
        }
        cameraMapper.deleteById(id);
        return Result.success();
    }
    
    /**
     * 更新摄像头状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateCameraStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Camera camera = cameraMapper.selectById(id);
        if (camera == null) {
            return Result.error("摄像头不存在");
        }
        
        Integer status = params.get("status");
        camera.setStatus(status);
        camera.setUpdateTime(LocalDateTime.now());
        cameraMapper.updateById(camera);
        
        return Result.success();
    }
}