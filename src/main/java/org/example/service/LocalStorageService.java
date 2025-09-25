package org.example.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 本地存储服务接口
 */
public interface LocalStorageService {
    /**
     * 保存文件
     *
     * @param file   上传的文件
     * @param bucket 存储桶
     * @return 文件名 (ObjectKey)
     */
    String saveFile(MultipartFile file, String bucket) throws IOException;

    /**
     * 将文件作为资源加载
     *
     * @param bucket    存储桶
     * @param objectKey 文件名
     * @return Spring 资源对象
     */
    Resource loadFileAsResource(String bucket, String objectKey);

    /**
     * 删除文件
     *
     * @param bucket    存储桶
     * @param objectKey 文件名
     */
    void deleteFile(String bucket, String objectKey) throws IOException;
}