package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.DetectionRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 检测记录Mapper接口
 */
@Mapper
public interface DetectionRecordMapper extends BaseMapper<DetectionRecord> {
}