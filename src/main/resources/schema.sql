-- 创建用户表
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER, ADMIN',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `last_login` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`),
    INDEX `idx_role` (`role`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建摄像头表
CREATE TABLE IF NOT EXISTS `cameras` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL COMMENT '摄像头名称',
    `location` VARCHAR(255) NOT NULL COMMENT '摄像头位置',
    `ip_address` VARCHAR(45) NOT NULL COMMENT 'IP地址',
    `port` INT NOT NULL DEFAULT 8080 COMMENT '端口号',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '登录用户名',
    `password` VARCHAR(255) DEFAULT NULL COMMENT '登录密码',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-离线，1-在线',
    `stream_url` VARCHAR(500) DEFAULT NULL COMMENT '视频流地址',
    `description` TEXT DEFAULT NULL COMMENT '描述',
    `created_by` BIGINT NOT NULL COMMENT '创建者ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_name` (`name`),
    INDEX `idx_location` (`location`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_by` (`created_by`),
    FOREIGN KEY (`created_by`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='摄像头表';

-- 创建检测记录表
CREATE TABLE IF NOT EXISTS `detection_records` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `camera_id` BIGINT NOT NULL COMMENT '摄像头ID',
    `detection_time` DATETIME NOT NULL COMMENT '检测时间',
    `detection_type` VARCHAR(50) NOT NULL COMMENT '检测类型：PERSON, VEHICLE, OBJECT等',
    `detection_result` JSON NOT NULL COMMENT '检测结果JSON',
    `confidence` DECIMAL(5,4) NOT NULL COMMENT '置信度',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '检测图片URL',
    `video_url` VARCHAR(500) DEFAULT NULL COMMENT '检测视频URL',
    `processed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已处理：0-未处理，1-已处理',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    INDEX `idx_camera_id` (`camera_id`),
    INDEX `idx_detection_time` (`detection_time`),
    INDEX `idx_detection_type` (`detection_type`),
    INDEX `idx_confidence` (`confidence`),
    INDEX `idx_processed` (`processed`),
    FOREIGN KEY (`camera_id`) REFERENCES `cameras`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='检测记录表';

-- 插入默认管理员用户（密码：123456）
INSERT INTO `users` (`username`, `password`, `email`, `role`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7.QdEKLiq', 'admin@example.com', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE `username`=`username`;

-- 插入测试摄像头数据
INSERT INTO `cameras` (`name`, `location`, `ip_address`, `port`, `username`, `password`, `status`, `stream_url`, `description`, `created_by`)
VALUES 
    ('摄像头-001', '主入口', '192.168.1.100', 8080, 'admin', 'admin123', 1, 'rtsp://192.168.1.100:554/stream1', '主入口监控摄像头', 1),
    ('摄像头-002', '停车场', '192.168.1.101', 8080, 'admin', 'admin123', 1, 'rtsp://192.168.1.101:554/stream1', '停车场监控摄像头', 1)
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);