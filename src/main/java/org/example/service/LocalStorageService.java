package org.example.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 本地存储服务接口
 * port: 5001
 */
public interface LocalStorageService {
    /**
     * 保存文件
     */
    String saveFile(MultipartFile file) throws IOException;

    /**
     * 读取文件
     * @param filePath 文件路径
     * @return 文件内容
     */
    byte[] readFile(String filePath);

    /**
     * 删除文件
     * @param filePath 文件路径
     */
    void deleteFile(String filePath);
}
