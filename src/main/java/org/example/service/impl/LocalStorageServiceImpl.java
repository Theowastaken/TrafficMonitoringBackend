package org.example.service.impl;

import org.example.service.LocalStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageServiceImpl implements LocalStorageService {

    @Value("${upload.base-dir:/data/uploads}")
    private String baseDir;

    @Override
    public String saveFile(MultipartFile file) throws IOException {
        // 确保基础目录存在
        File uploadDir = new File(baseDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // 保存文件
        String filePath = baseDir + File.separator + uniqueFileName;
        file.transferTo(new File(filePath));

        return uniqueFileName; // 返回文件名
    }

    @Override
    public byte[] readFile(String filePath) {
        try {
            return Files.readAllBytes(Paths.get(baseDir, filePath));
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败：" + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(baseDir, filePath));
        } catch (IOException e) {
            throw new RuntimeException("删除文件失败：" + e.getMessage(), e);
        }
    }
}