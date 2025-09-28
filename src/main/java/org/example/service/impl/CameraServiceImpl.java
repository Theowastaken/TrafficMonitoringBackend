package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.entity.Camera;
import org.example.mapper.CameraMapper;
import org.example.service.CameraService;
import org.example.vo.camera.CameraVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 摄像头服务实现类
 */
@Service
public class CameraServiceImpl implements CameraService {

    @Autowired
    private CameraMapper cameraMapper;

    @Override
    public Page<CameraVO> pageCamera(Integer current, Integer size, String name, String location, Integer status) {
        Page<Camera> page = new Page<>(current, size);
        QueryWrapper<Camera> wrapper = new QueryWrapper<>();

        if (StringUtils.hasText(name)) {
            wrapper.like("name", name);
        }
        if (StringUtils.hasText(location)) {
            wrapper.like("location", location);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }

        wrapper.orderByDesc("create_time");
        Page<Camera> cameraPage = cameraMapper.selectPage(page, wrapper);
        cameraPage.setTotal(cameraMapper.selectCount(wrapper));

        List<CameraVO> voList = cameraPage.getRecords().stream()
                .map(camera -> BeanUtil.copyProperties(camera, CameraVO.class))
                .collect(Collectors.toList());

        Page<CameraVO> voPage = new Page<>(cameraPage.getCurrent(), cameraPage.getSize(), cameraPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public CameraVO getCameraById(Long id) {
        Camera camera = cameraMapper.selectById(id);
        if (camera == null) {
            throw new RuntimeException("摄像头不存在");
        }
        return BeanUtil.copyProperties(camera, CameraVO.class);
    }

    @Override
    public Long createCamera(Camera camera) {
        camera.setCreateTime(LocalDateTime.now());
        camera.setUpdateTime(LocalDateTime.now());
        cameraMapper.insert(camera);
        return camera.getId();
    }

    @Override
    public void updateCamera(Camera camera) {
        Camera existCamera = cameraMapper.selectById(camera.getId());
        if (existCamera == null) {
            throw new RuntimeException("摄像头不存在");
        }
        camera.setUpdateTime(LocalDateTime.now());
        cameraMapper.updateById(camera);
    }

    @Override
    public void deleteCamera(Long id) {
        Camera camera = cameraMapper.selectById(id);
        if (camera == null) {
            throw new RuntimeException("摄像头不存在");
        }
        cameraMapper.deleteById(id);
    }

    @Override
    public void updateCameraStatus(Long id, Integer status) {
        Camera camera = cameraMapper.selectById(id);
        if (camera == null) {
            throw new RuntimeException("摄像头不存在");
        }
        camera.setStatus(status);
        camera.setUpdateTime(LocalDateTime.now());
        cameraMapper.updateById(camera);
    }
}

