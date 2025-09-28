package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.common.result.Result;
import org.example.common.vo.PageVO;
import org.example.dto.detection.DetectionRecordAddDto;
import org.example.entity.DetectionRecord;
import org.example.service.DetectionRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 检测记录控制器
 */
@RestController
@RequestMapping("/detection")
@AllArgsConstructor
public class DetectionRecordController {

    private final DetectionRecordService detectionRecordService;

    /**
     * 添加检测记录
     */
    @PostMapping("/record")
    public Result<Boolean> addDetectionRecord(@RequestBody DetectionRecordAddDto dto) {
        try {
            boolean ok = detectionRecordService.addDetectionRecord(dto);
            return Result.success(ok);
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
        PageVO<DetectionRecord> pageVO = detectionRecordService.pageDetectionRecords(current, size, cameraId, startTime, endTime, processed);
        return Result.success(pageVO);
    }

    /**
     * 获取检测记录详情
     */
    @GetMapping("/record/{id}")
    public Result<DetectionRecord> getDetectionRecordById(@PathVariable Long id) {
        DetectionRecord record = detectionRecordService.getById(id);
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
        try {
            boolean ok = detectionRecordService.updateProcessStatus(id, processed);
            return Result.success(ok);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 处理检测记录（带内容和照片）
     */
    @PutMapping("/record/process")
    public Result<Boolean> processDetectionRecord(@RequestBody Map<String, Object> params) {
        try {
            boolean ok = detectionRecordService.processDetectionRecord(params);
            return Result.success(ok);
        } catch (Exception e) {
            return Result.error("处理检测记录失败：" + e.getMessage());
        }
    }

    /**
     * 删除检测记录
     */
    @DeleteMapping("/record/{id}")
    public Result<Boolean> deleteDetectionRecord(@PathVariable Long id) {
        boolean ok = detectionRecordService.deleteById(id);
        return Result.success(ok);
    }

    /**
     * 批量删除检测记录
     */
    @DeleteMapping("/record/batch")
    public Result<Boolean> batchDeleteDetectionRecords(@RequestBody List<Long> ids) {
        try {
            boolean ok = detectionRecordService.deleteBatch(ids);
            return Result.success(ok);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 清空所有检测记录
     */
    @DeleteMapping("/record/clear-all")
    public Result<Boolean> clearAllDetectionRecords() {
        boolean ok = detectionRecordService.clearAll();
        return Result.success(ok);
    }
}
