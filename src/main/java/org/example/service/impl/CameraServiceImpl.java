package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.dto.camera.CameraQueryDto;
import org.example.common.context.CurrentUserInfo;
import org.example.entity.Camera;
import org.example.mapper.CameraMapper;
import org.example.service.CameraService;
import org.example.vo.camera.CameraVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 摄像头服务实现类
 */
@Service
public class CameraServiceImpl extends BaseServiceImpl implements CameraService {

    private final CameraMapper cameraMapper;

    public CameraServiceImpl(CameraMapper cameraMapper) {
        this.cameraMapper = cameraMapper;
    }

    /**
     * 转换分页对象
     * @param sourcePage 源分页对象
     * @param targetClass 目标类型
     * @param <S> 源类型
     * @param <T> 目标类型
     * @return 转换后的分页对象
     */
    private <S, T> Page<T> convertPage(Page<S> sourcePage, Class<T> targetClass) {
        //TODO: 可以考虑使用更高效的映射工具如MapStruct
        //      可以将此函数提取到一个公共的工具类中
        List<T> voList = sourcePage.getRecords().stream()
                .map(source -> BeanUtil.copyProperties(source, targetClass))
                .collect(Collectors.toList());

        Page<T> targetPage = new Page<>(sourcePage.getCurrent(), sourcePage.getSize(), sourcePage.getTotal());
        targetPage.setRecords(voList);
        return targetPage;
    }

    @Override
    public Page<CameraVO> pageCamera(CameraQueryDto queryDto) {
        Integer current = queryDto.getCurrent();
        Integer size = queryDto.getSize();
        String name = queryDto.getName();
        String location = queryDto.getLocation();
        Integer status = queryDto.getStatus();

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

        return convertPage(cameraPage, CameraVO.class);
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
        // 获取当前用户信息 - 现在可以直接使用父类的方法
        CurrentUserInfo currentUser = getCurrentUser();
        camera.setUserId(currentUser.getUserId());
        camera.setUserName(currentUser.getUsername());

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
