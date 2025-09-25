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

    @Override
    public String saveFile(MultipartFile file, String bucket) throws IOException {
        if (bucket == null || bucket.trim().isEmpty() || bucket.contains("..")) {
            throw new IllegalArgumentException("Invalid bucket name.");
        }

        Path bucketPath = Paths.get(baseDir, bucket);
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
}
