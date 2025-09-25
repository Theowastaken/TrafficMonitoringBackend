package org.example.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.result.Result;
import org.example.common.vo.PageVO;
import org.example.dto.detection.DetectionRecordAddDto;
import org.example.entity.DetectionRecord;
import org.example.mapper.DetectionRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 检测记录控制器
 */
@RestController
@RequestMapping("/detection")
public class DetectionRecordController {
    
    @Autowired
    private DetectionRecordMapper detectionRecordMapper;
    
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
            
            // 处理图片base64并保存
            if (dto.getImageBase64() != null && !dto.getImageBase64().trim().isEmpty()) {
                String imageUrl = saveBase64Image(dto.getImageBase64());
                record.setImageUrl(imageUrl);
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
        if (startTime != null && !startTime.trim().isEmpty()) {
            wrapper.ge("detection_time", startTime);
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            wrapper.le("detection_time", endTime);
        }
        if (processed != null) {
            wrapper.eq("processed", processed);
        }
        
        wrapper.orderByDesc("detection_time");
        Page<DetectionRecord> recordPage = detectionRecordMapper.selectPage(page, wrapper);
        
        // 转换为PageVO
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
            
            // 处理图片base64并保存
            if (processImageBase64 != null && !processImageBase64.trim().isEmpty()) {
                String processImageUrl = saveBase64Image(processImageBase64);
                record.setProcessImageUrl(processImageUrl);
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
        if (record == null) {
            return Result.error("检测记录不存在");
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
        
        int result = detectionRecordMapper.deleteBatchIds(ids);
        return Result.success(result > 0);
    }
    
    /**
     * 清空所有检测记录
     */
    @DeleteMapping("/record/clear-all")
    public Result<Boolean> clearAllDetectionRecords() {
        QueryWrapper<DetectionRecord> wrapper = new QueryWrapper<>();
        wrapper.isNotNull("id"); // 查询所有记录
        
        int result = detectionRecordMapper.delete(wrapper);
        return Result.success(result >= 0);
    }
    
    /**
     * 保存Base64图片
     */
    private String saveBase64Image(String base64Data) {
        try {
            // 去掉data:image/xxx;base64,前缀
            if (base64Data.contains(",")) {
                base64Data = base64Data.split(",")[1];
            }
            
            // 解码base64
            byte[] imageBytes = Base64.decode(base64Data);
            
            // 生成文件名
            String fileName = IdUtil.simpleUUID() + ".jpg";
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = "/data/uploads/images/" + datePath + "/" + fileName;
            
            // 创建目录
            String uploadDir = System.getProperty("user.dir") + "/data/uploads/images/" + datePath;
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            String fullPath = uploadDir + "/" + fileName;
            FileUtil.writeBytes(imageBytes, new File(fullPath));
            
            return relativePath;
        } catch (Exception e) {
            throw new RuntimeException("保存图片失败：" + e.getMessage());
        }
    }
}