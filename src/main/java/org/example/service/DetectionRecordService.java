package org.example.service;

import org.example.common.vo.PageVO;
import org.example.dto.detection.DetectionRecordAddDto;
import org.example.entity.DetectionRecord;

import java.util.List;
import java.util.Map;

public interface DetectionRecordService {

    boolean addDetectionRecord(DetectionRecordAddDto dto);

    PageVO<DetectionRecord> pageDetectionRecords(Integer current, Integer size, Long cameraId, String startTime, String endTime, Integer processed);

    DetectionRecord getById(Long id);

    boolean updateProcessStatus(Long id, Integer processed);

    boolean processDetectionRecord(Map<String, Object> params);

    boolean deleteById(Long id);

    boolean deleteBatch(List<Long> ids);

    boolean clearAll();
}

