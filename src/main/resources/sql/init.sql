-- 创建数据库
CREATE DATABASE IF NOT EXISTS traffic_monitoring DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE traffic_monitoring;

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '用户状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标志：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 摄像头表
DROP TABLE IF EXISTS `camera`;
CREATE TABLE `camera` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '摄像头ID',
  `name` varchar(100) NOT NULL COMMENT '摄像头名称',
  `location` varchar(200) NOT NULL COMMENT '摄像头位置',
  `ip_address` varchar(50) DEFAULT NULL COMMENT '摄像头IP地址',
  `port` int(11) DEFAULT NULL COMMENT '摄像头端口',
  `rtsp_url` varchar(500) DEFAULT NULL COMMENT 'RTSP流地址',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '摄像头状态：0-离线，1-在线',
  `description` text COMMENT '摄像头描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标志：0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='摄像头表';

-- 检测记录表
DROP TABLE IF EXISTS `detection_record`;
CREATE TABLE `detection_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `camera_id` bigint(20) NOT NULL COMMENT '摄像头ID',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `detection_time` datetime NOT NULL COMMENT '检测时间',
  `detection_result` text NOT NULL COMMENT '检测结果（JSON格式）',
  `processed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '处理状态：0-未处理，1-已处理',
  `process_content` text COMMENT '处理内容',
  `process_image_url` varchar(500) COMMENT '处理图片URL',
  `process_time` datetime COMMENT '处理时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标志：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_camera_id` (`camera_id`),
  KEY `idx_detection_time` (`detection_time`),
  KEY `idx_processed` (`processed`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='检测记录表';

-- 插入初始数据

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `email`, `status`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXISwKhwjPiMZcKnDrOhE0FLQ6G', '系统管理员', '13800138000', 'admin@example.com', 1);

-- 插入测试摄像头数据
INSERT INTO `camera` (`name`, `location`, `ip_address`, `port`, `status`, `description`) VALUES
('摄像头01', '主入口', '192.168.1.100', 554, 1, '监控主入口车辆通行情况'),
('摄像头02', '停车场A区', '192.168.1.101', 554, 1, '监控停车场A区车辆停放'),
('摄像头03', '停车场B区', '192.168.1.102', 554, 0, '监控停车场B区车辆停放'),
('摄像头04', '侧门入口', '192.168.1.103', 554, 1, '监控侧门车辆进出');

-- 插入测试检测记录数据
INSERT INTO `detection_record` (`camera_id`, `image_url`, `detection_time`, `detection_result`, `processed`) VALUES
(1, '/uploads/images/2023/12/21/image001.jpg', '2023-12-21 09:30:00', '{"vehicles": [{"type": "car", "license": "京A12345", "confidence": 0.95}]}', 0),
(1, '/uploads/images/2023/12/21/image002.jpg', '2023-12-21 10:15:00', '{"vehicles": [{"type": "truck", "license": "京B67890", "confidence": 0.88}]}', 1),
(2, '/uploads/images/2023/12/21/image003.jpg', '2023-12-21 11:00:00', '{"vehicles": [{"type": "car", "license": "沪C11111", "confidence": 0.92}]}', 0),
(3, '/uploads/images/2023/12/21/image004.jpg', '2023-12-21 14:30:00', '{"vehicles": [{"type": "motorcycle", "license": "粤D22222", "confidence": 0.85}]}', 0);