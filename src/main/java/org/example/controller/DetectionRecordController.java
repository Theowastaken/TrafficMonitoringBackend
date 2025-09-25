package org.example.controller;

import cn.hutool.core.codec.Base64;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import org.example.common.result.Result;
import org.example.common.vo.PageVO;
import org.example.dto.detection.DetectionRecordAddDto;
import org.example.entity.DetectionRecord;
import org.example.mapper.DetectionRecordMapper;
import org.example.service.LocalStorageService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 检测记录控制器
 */
@RestController
@RequestMapping("/detection")
@AllArgsConstructor
public class DetectionRecordController {

    private final DetectionRecordMapper detectionRecordMapper;
    private final LocalStorageService localStorageService;
    private static final String DETECTION_BUCKET = "detections";


    /**
     * 添加检测记录
     */
    @PostMapping("/record")
    public Result<Boolean> addDetectionRecord(@RequestBody DetectionRecordAddDto dto) {
        try {
            DetectionRecord record = new DetectionRecord();
            record.setCameraId(dto.getCameraId());
            record.setDetectionResult(dto.getDetectionResult());
            record.setDetectionTime(dto.getDetectionTime() != null ? dto.getDetectionTime() : LocalDateTime.now());
            record.setProcessed(0);

            // 保存图片并存储 bucket, objectKey, 和完整的 URL
            if (StringUtils.hasText(dto.getImageBase64())) {
                byte[] imageBytes = decodeBase64(dto.getImageBase64());
                String objectKey = localStorageService.saveFile(imageBytes, DETECTION_BUCKET, UUID.randomUUID() + ".jpg");
                String imageUrl = localStorageService.getFileUrl(DETECTION_BUCKET, objectKey);
                record.setImageUrl(imageUrl);
                record.setImageBucket(DETECTION_BUCKET);
                record.setImageObjectKey(objectKey);
            }

            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());

            int result = detectionRecordMapper.insert(record);
            return Result.success(result > 0);
        } catch (Exception e) {
            return Result.error("添加检测记录失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询检测记录
     */
    @GetMapping("/record/page")
    public Result<PageVO<DetectionRecord>> pageDetectionRecords(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long cameraId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer processed) {

        Page<DetectionRecord> page = new Page<>(current, size);
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
        Page<DetectionRecord> recordPage = detectionRecordMapper.selectPage(page, wrapper);

        PageVO<DetectionRecord> pageVO = new PageVO<>(
                recordPage.getCurrent(),
                recordPage.getSize(),
                recordPage.getTotal(),
                recordPage.getRecords()
        );

        return Result.success(pageVO);
    }

    /**
     * 获取检测记录详情
     */
    @GetMapping("/record/{id}")
    public Result<DetectionRecord> getDetectionRecordById(@PathVariable Long id) {
        DetectionRecord record = detectionRecordMapper.selectById(id);
        if (record == null) {
            return Result.error("检测记录不存在");
        }
        return Result.success(record);
    }

    /**
     * 更新处理状态
     */
    @PutMapping("/record/{id}/process")
    public Result<Boolean> updateProcessStatus(@PathVariable Long id, @RequestParam Integer processed) {
        DetectionRecord record = detectionRecordMapper.selectById(id);
        if (record == null) {
            return Result.error("检测记录不存在");
        }

        record.setProcessed(processed);
        if (processed == 1) {
            record.setProcessTime(LocalDateTime.now());
        }
        record.setUpdateTime(LocalDateTime.now());

        int result = detectionRecordMapper.updateById(record);
        return Result.success(result > 0);
    }

    /**
     * 处理检测记录（带内容和照片）
     */
    @PutMapping("/record/process")
    public Result<Boolean> processDetectionRecord(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.valueOf(params.get("id").toString());
            Integer processed = (Integer) params.get("processed");
            String processContent = (String) params.get("processContent");
            String processImageBase64 = (String) params.get("processImageBase64");

            DetectionRecord record = detectionRecordMapper.selectById(id);
            if (record == null) {
                return Result.error("检测记录不存在");
            }

            record.setProcessed(processed);
            record.setProcessContent(processContent);
            record.setProcessTime(LocalDateTime.now());

            // 保存处理图片并存储 bucket, objectKey, 和完整的 URL
            if (StringUtils.hasText(processImageBase64)) {
                byte[] imageBytes = decodeBase64(processImageBase64);
                String objectKey = localStorageService.saveFile(imageBytes, DETECTION_BUCKET, UUID.randomUUID() + ".jpg");
                String processImageUrl = localStorageService.getFileUrl(DETECTION_BUCKET, objectKey);
                record.setProcessImageUrl(processImageUrl);
                record.setProcessImageBucket(DETECTION_BUCKET);
                record.setProcessImageObjectKey(objectKey);
            }

            record.setUpdateTime(LocalDateTime.now());

            int result = detectionRecordMapper.updateById(record);
            return Result.success(result > 0);
        } catch (Exception e) {
            return Result.error("处理检测记录失败：" + e.getMessage());
        }
    }

    /**
     * 删除检测记录
     */
    @DeleteMapping("/record/{id}")
    public Result<Boolean> deleteDetectionRecord(@PathVariable Long id) {
        DetectionRecord record = detectionRecordMapper.selectById(id);
        if (record != null) {
            deleteRecordImages(record);
        }

        int result = detectionRecordMapper.deleteById(id);
        return Result.success(result > 0);
    }

    /**
     * 批量删除检测记录
     */
    @DeleteMapping("/record/batch")
    public Result<Boolean> batchDeleteDetectionRecords(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("删除ID列表不能为空");
        }
        ids.forEach(id -> {
            DetectionRecord record = detectionRecordMapper.selectById(id);
            if (record != null) {
                deleteRecordImages(record);
            }
        });

        int result = detectionRecordMapper.deleteBatchIds(ids);
        return Result.success(result > 0);
    }

    /**
     * 清空所有检测记录
     */
    @DeleteMapping("/record/clear-all")
    public Result<Boolean> clearAllDetectionRecords() {
        List<DetectionRecord> records = detectionRecordMapper.selectList(null);
        records.forEach(this::deleteRecordImages);

        int result = detectionRecordMapper.delete(new QueryWrapper<>());
        return Result.success(result >= 0);
    }

    /**
     * 辅助方法：解码Base64字符串
     */
    private byte[] decodeBase64(String base64Data) {
        if (base64Data.contains(",")) {
            base64Data = base64Data.split(",")[1];
        }
        return Base64.decode(base64Data);
    }

    /**
     * 辅助方法：删除记录关联的图片文件
     */
    private void deleteRecordImages(DetectionRecord record) {
        try {
            if (StringUtils.hasText(record.getImageBucket()) && StringUtils.hasText(record.getImageObjectKey())) {
                localStorageService.deleteFile(record.getImageBucket(), record.getImageObjectKey());
            }
            if (StringUtils.hasText(record.getProcessImageBucket()) && StringUtils.hasText(record.getProcessImageObjectKey())) {
                localStorageService.deleteFile(record.getProcessImageBucket(), record.getProcessImageObjectKey());
            }
        } catch (IOException e) {
            // 实际项目中应记录日志
            System.err.println("删除文件失败: " + e.getMessage());
        }
    }
}
