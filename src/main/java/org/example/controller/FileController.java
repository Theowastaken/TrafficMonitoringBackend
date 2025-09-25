package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.common.result.Result;
import org.example.dto.file.FileUploadResponse;
import org.example.service.LocalStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FileController {

    private final LocalStorageService localStorageService;

    /**
     * 上传文件
     *
     * @param bucket  存储桶名称
     * @param file    文件
     * @param isCache 是否为缓存文件
     * @return 上传结果
     */
    @PostMapping("/upload/{bucket}")
    public Result<FileUploadResponse> upload(
            @PathVariable String bucket,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "is_cache", defaultValue = "false") boolean isCache
    ) {
        try {
            String objectKey = localStorageService.saveFile(file, bucket, isCache);

            FileUploadResponse response = new FileUploadResponse();
            response.setBucket(bucket);
            response.setObjectKey(objectKey);
            // 构建可访问的 URL
            response.setUrl(String.format("/file/%s/%s", bucket, objectKey));

            return Result.success(response);
        } catch (IOException e) {
            // 在实际应用中，这里应该记录详细的错误日志
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取文件
     *
     * @param bucket    存储桶名称
     * @param objectKey 文件名
     * @return 文件资源
     */
    @GetMapping("/{bucket}/{objectKey}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String bucket,
            @PathVariable String objectKey) {
        try {
            Resource resource = localStorageService.loadFileAsResource(bucket, objectKey);
            String contentType = "application/octet-stream"; // 默认内容类型

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除文件
     *
     * @param bucket    存储桶名称
     * @param objectKey 文件名
     * @return 操作结果
     */
    @DeleteMapping("/{bucket}/{objectKey}")
    public Result<Void> deleteFile(
            @PathVariable String bucket,
            @PathVariable String objectKey) {
        try {
            localStorageService.deleteFile(bucket, objectKey);
            return Result.success();
        } catch (IOException e) {
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }
}