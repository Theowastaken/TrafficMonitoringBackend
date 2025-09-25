package org.example.dto.file;

import lombok.Data;

@Data
public class FileUploadResponse {
    private String url;
    private String bucket;
    private String objectKey;
}
