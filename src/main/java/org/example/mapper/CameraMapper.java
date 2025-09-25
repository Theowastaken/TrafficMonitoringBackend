package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.Camera;
import org.apache.ibatis.annotations.Mapper;

/**
 * 摄像头Mapper接口
 */
@Mapper
public interface CameraMapper extends BaseMapper<Camera> {
}