package org.example.service.impl;

import cn.hutool.core.codec.Base64;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.example.common.vo.PageVO;
import org.example.dto.detection.DetectionRecordAddDto;
import org.example.entity.Camera;
import org.example.entity.DetectionRecord;
import org.example.mapper.CameraMapper;
import org.example.mapper.DetectionRecordMapper;
import org.example.service.DetectionRecordService;
import org.example.service.LocalStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class DetectionRecordServiceImpl implements DetectionRecordService {

    private final DetectionRecordMapper detectionRecordMapper;
    private final LocalStorageService localStorageService;
    private final ObjectMapper objectMapper;
    private final CameraMapper cameraMapper;

    private static final String DETECTION_BUCKET = "detections";

    @Override
    @Transactional
    public boolean addDetectionRecord(DetectionRecordAddDto dto) {
        DetectionRecord record = new DetectionRecord();
        record.setCameraId(dto.getCameraId());
        String processedDetectionResult = processDetectionResult(dto.getDetectionResult());
        record.setDetectionResult(processedDetectionResult);
        record.setDetectionTime(dto.getDetectionTime() != null ? dto.getDetectionTime() : LocalDateTime.now());
        record.setProcessed(0);
        // set camera name via CameraMapper
        Camera camera = cameraMapper.selectById(dto.getCameraId());
        if (camera != null) {
            record.setCameraName(camera.getName());
        }

        if (StringUtils.hasText(dto.getImageBase64())) {
            byte[] imageBytes = decodeBase64(dto.getImageBase64());
            try {
                String objectKey = localStorageService.saveFile(imageBytes, DETECTION_BUCKET, UUID.randomUUID() + ".jpg");
                String imageUrl = localStorageService.getFileUrl(DETECTION_BUCKET, objectKey);
                record.setImageUrl(imageUrl);
                record.setImageBucket(DETECTION_BUCKET);
                record.setImageObjectKey(objectKey);
            } catch (IOException e) {
                throw new RuntimeException("保存检测图片失败: " + e.getMessage(), e);
            }
        }

        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        return detectionRecordMapper.insert(record) > 0;
    }

    @Override
    public PageVO<DetectionRecord> pageDetectionRecords(Integer current, Integer size, Long cameraId, String startTime, String endTime, Integer processed) {
        QueryWrapper<DetectionRecord> wrapper = new QueryWrapper<>();

        if (cameraId != null) {
            wrapper.eq("camera_id", cameraId);
        }
        if (StringUtils.hasText(startTime)) {
            wrapper.ge("detection_time", startTime);
        }
        if (StringUtils.hasText(endTime)) {
            wrapper.le("detection_time", endTime);
        }
        if (processed != null) {
            wrapper.eq("processed", processed);
        }
        wrapper.orderByDesc("detection_time");

        Page<DetectionRecord> page = new Page<>(current, size, detectionRecordMapper.selectCount(wrapper));
        Page<DetectionRecord> recordPage = detectionRecordMapper.selectPage(page, wrapper);
        return new PageVO<>(
                recordPage.getCurrent(),
                recordPage.getSize(),
                recordPage.getTotal(),
                recordPage.getRecords()
        );
    }

    @Override
    public DetectionRecord getById(Long id) {
        return detectionRecordMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean updateProcessStatus(Long id, Integer processed) {
        DetectionRecord record = detectionRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("检测记录不存在");
        }
        record.setProcessed(processed);
        if (processed == 1) {
            record.setProcessTime(LocalDateTime.now());
        }
        record.setUpdateTime(LocalDateTime.now());
        return detectionRecordMapper.updateById(record) > 0;
    }

    @Override
    @Transactional
    public boolean processDetectionRecord(Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer processed = (Integer) params.get("processed");
        String processContent = (String) params.get("processContent");
        String processImageBase64 = (String) params.get("processImageBase64");

        DetectionRecord record = detectionRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("检测记录不存在");
        }

        record.setProcessed(processed);
        record.setProcessContent(processContent);
        record.setProcessTime(LocalDateTime.now());

        if (StringUtils.hasText(processImageBase64)) {
            byte[] imageBytes = decodeBase64(processImageBase64);
            try {
                String objectKey = localStorageService.saveFile(imageBytes, DETECTION_BUCKET, UUID.randomUUID() + ".jpg");
                String processImageUrl = localStorageService.getFileUrl(DETECTION_BUCKET, objectKey);
                record.setProcessImageUrl(processImageUrl);
                record.setProcessImageBucket(DETECTION_BUCKET);
                record.setProcessImageObjectKey(objectKey);
            } catch (IOException e) {
                throw new RuntimeException("保存处理图片失败: " + e.getMessage(), e);
            }
        }

        record.setUpdateTime(LocalDateTime.now());
        return detectionRecordMapper.updateById(record) > 0;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        DetectionRecord record = detectionRecordMapper.selectById(id);
        if (record != null) {
            deleteRecordImages(record);
        }
        return detectionRecordMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("删除ID列表不能为空");
        }
        ids.forEach(id -> {
            DetectionRecord record = detectionRecordMapper.selectById(id);
            if (record != null) {
                deleteRecordImages(record);
            }
        });
        return detectionRecordMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    @Transactional
    public boolean clearAll() {
        List<DetectionRecord> records = detectionRecordMapper.selectList(null);
        records.forEach(this::deleteRecordImages);
        return detectionRecordMapper.delete(new QueryWrapper<>()) >= 0;
    }

    private byte[] decodeBase64(String base64Data) {
        if (base64Data.contains(",")) {
            base64Data = base64Data.split(",")[1];
        }
        return Base64.decode(base64Data);
    }

    private String processDetectionResult(String detectionResult) {
        if (!StringUtils.hasText(detectionResult)) {
            return detectionResult;
        }
        try {
            JsonNode root = objectMapper.readTree(detectionResult);
            boolean changed = processGroupImagesRecursive(root);
            if (changed) {
                return objectMapper.writeValueAsString(root);
            }
        } catch (Exception ex) {
            // ignore and return original string
        }
        return detectionResult;
    }

    private boolean processGroupImagesRecursive(JsonNode node) {
        boolean changed = false;
        if (node == null) return false;

        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            JsonNode groupImages = obj.get("groupImages");
            if (groupImages != null && groupImages.isArray()) {
                ArrayNode arrayNode = (ArrayNode) groupImages;
                for (JsonNode item : arrayNode) {
                    if (item != null && item.isObject()) {
                        ObjectNode itemObj = (ObjectNode) item;
                        if (itemObj.hasNonNull("imageBase64")) {
                            String base64 = itemObj.get("imageBase64").asText("");
                            if (StringUtils.hasText(base64)) {
                                try {
                                    byte[] bytes = decodeBase64(base64);
                                    String objectKey = localStorageService.saveFile(bytes, DETECTION_BUCKET, UUID.randomUUID() + ".jpg");
                                    String url = localStorageService.getFileUrl(DETECTION_BUCKET, objectKey);
                                    itemObj.put("imageUrl", url);
                                    itemObj.remove("imageBase64");
                                    changed = true;
                                } catch (IOException ioe) {
                                    // skip this item on failure
                                }
                            }
                        }
                        changed |= processGroupImagesRecursive(itemObj);
                    }
                }
            }
            var fields = obj.fields();
            while (fields.hasNext()) {
                var entry = fields.next();
                if (entry.getValue() != null) {
                    changed |= processGroupImagesRecursive(entry.getValue());
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                changed |= processGroupImagesRecursive(child);
            }
        }
        return changed;
    }

    private void deleteRecordImages(DetectionRecord record) {
        try {
            if (StringUtils.hasText(record.getImageBucket()) && StringUtils.hasText(record.getImageObjectKey())) {
                localStorageService.deleteFile(record.getImageBucket(), record.getImageObjectKey());
            }
            if (StringUtils.hasText(record.getProcessImageBucket()) && StringUtils.hasText(record.getProcessImageObjectKey())) {
                localStorageService.deleteFile(record.getProcessImageBucket(), record.getProcessImageObjectKey());
            }
        } catch (IOException e) {
            // log in real project
            System.err.println("删除文件失败: " + e.getMessage());
        }
    }
}
