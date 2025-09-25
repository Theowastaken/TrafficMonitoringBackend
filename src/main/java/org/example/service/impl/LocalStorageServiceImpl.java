package org.example.service.impl;

import org.example.service.LocalStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageServiceImpl implements LocalStorageService {

    @Value("${upload.base-dir:data/uploads}")
    private String baseDir;

    @Value("${upload.base-url:http://localhost:9090/api}")
    private String baseFileUrl;


    @Override
    public String saveFile(MultipartFile file, String bucket, boolean isCache) throws IOException {
        if (bucket == null || bucket.trim().isEmpty() || bucket.contains("..")) {
            throw new IllegalArgumentException("Invalid bucket name.");
        }

        Path bucketPath = Paths.get(baseDir, bucket);
        // 如果是缓存文件，则存储在 bucket 下的 cache 目录中
        if (isCache) {
            bucketPath = bucketPath.resolve("cache");
        }

        if (!Files.exists(bucketPath)) {
            Files.createDirectories(bucketPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String objectKey = UUID.randomUUID() + extension;

        Path destinationFile = bucketPath.resolve(objectKey);
        Files.copy(file.getInputStream(), destinationFile);

        // 如果是缓存文件，返回的 objectKey 需要包含 cache/ 前缀
        if (isCache) {
            return "cache/" + objectKey;
        }
        return objectKey;
    }

    @Override
    public String saveFile(byte[] data, String bucket, String fileName) throws IOException {
        if (bucket == null || bucket.trim().isEmpty() || bucket.contains("..")) {
            throw new IllegalArgumentException("Invalid bucket name.");
        }

        Path bucketPath = Paths.get(baseDir, bucket);
        if (!Files.exists(bucketPath)) {
            Files.createDirectories(bucketPath);
        }

        String extension = fileName != null && fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf("."))
                : "";
        String objectKey = UUID.randomUUID() + extension;

        Path destinationFile = bucketPath.resolve(objectKey);
        Files.write(destinationFile, data);

        return objectKey;
    }

    @Override
    public Resource loadFileAsResource(String bucket, String objectKey) {
        try {
            Path filePath = Paths.get(baseDir, bucket, objectKey);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or cannot be read: " + objectKey);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File path is invalid: " + objectKey, e);
        }
    }

    @Override
    public void deleteFile(String bucket, String objectKey) throws IOException {
        Path filePath = Paths.get(baseDir, bucket, objectKey);
        Files.deleteIfExists(filePath);
    }

    @Override
    public String getFileUrl(String bucket, String objectKey) {
        return String.format("%s/file/%s/%s",baseFileUrl, bucket, objectKey);
    }
}
