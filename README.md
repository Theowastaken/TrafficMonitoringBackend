# 智能交通监控后端系统

这是一个基于Spring Boot + MyBatis-Plus + MySQL的智能交通监控后端系统，对应前端的Vue.js应用。

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── org/example/
│   │       ├── TrafficMonitoringApplication.java  # 主启动类
│   │       ├── entity/                             # 实体类
│   │       │   ├── User.java                       # 用户实体
│   │       │   ├── Camera.java                     # 摄像头实体
│   │       │   └── DetectionRecord.java            # 检测记录实体
│   │       ├── dto/                                # 数据传输对象
│   │       │   ├── user/                           # 用户相关DTO
│   │       │   └── detection/                      # 检测记录相关DTO
│   │       ├── vo/                                 # 视图对象
│   │       │   └── user/                           # 用户相关VO
│   │       ├── common/                             # 通用类
│   │       │   ├── result/Result.java              # 统一响应结果
│   │       │   └── vo/PageVO.java                  # 分页结果
│   │       ├── mapper/                             # 数据访问层
│   │       │   ├── UserMapper.java
│   │       │   ├── CameraMapper.java
│   │       │   └── DetectionRecordMapper.java
│   │       ├── service/                            # 业务服务层
│   │       │   ├── UserService.java
│   │       │   ├── LocalStorageService.java        # 文件存储服务
│   │       │   └── impl/                           # 业务实现类
│   │       ├── controller/                         # 控制器层
│   │       │   ├── UserController.java
│   │       │   ├── CameraController.java
│   │       │   ├── DetectionRecordController.java
│   │       │   └── FileController.java             # 文件上传接口
│   │       └── config/                             # 配置类
│   │           ├── CorsConfig.java                 # 跨域配置
│   │           ├── GlobalExceptionHandler.java     # 全局异常处理
│   │           └── SecurityConfig.java             # 安全配置
│   └── resources/
│       ├── application.yml                         # 应用配置
│       ├── schema.sql                              # 数据库结构
│       └── sql/init.sql                            # 数据库初始化脚本
```
## 技术栈

- **Spring Boot 2.7.18** - 主框架
- **MySQL 8.0** - 数据库
- **MyBatis-Plus 3.5.3** - ORM框架
- **Spring Security** - 安全框架
- **JWT** - 令牌认证
- **Hutool** - 工具类库
- **Lombok** - 代码简化

## 主要功能

### 用户管理
- 用户登录/注册
- 用户信息管理
- 密码修改/重置
- 用户状态管理

### 摄像头管理
- 摄像头信息增删改查
- 摄像头状态管理
- 分页查询支持

### 检测记录管理
- 检测记录添加
- 检测记录查询（支持多条件筛选）
- 检测记录处理状态管理
- 图片上传和存储
- 批量删除功能

### 文件上传
- 图片/头像上传（Base64或文件流）
- 按日期分目录存储

## API接口

### 用户接口
- `POST /api/user/login` - 用户登录
- `POST /api/user/register` - 用户注册
- `POST /api/user/logout` - 退出登录
- `POST /api/user/password` - 修改密码
- `GET /api/user/page` - 分页查询用户
- `GET /api/user/{id}` - 获取用户详情
- `POST /api/user` - 添加用户
- `PUT /api/user` - 更新用户
- `DELETE /api/user/{id}` - 删除用户
- `PUT /api/user/{id}/reset-password` - 重置密码
- `PUT /api/user/{id}/status` - 更新用户状态

### 摄像头接口
- `GET /api/camera/page` - 分页查询摄像头
- `GET /api/camera/{id}` - 获取摄像头详情
- `POST /api/camera` - 创建摄像头
- `PUT /api/camera` - 更新摄像头
- `DELETE /api/camera/{id}` - 删除摄像头
- `PUT /api/camera/{id}/status` - 更新摄像头状态

### 检测记录接口
- `POST /api/detection/record` - 添加检测记录
- `GET /api/detection/record/page` - 分页查询检测记录
- `GET /api/detection/record/{id}` - 获取检测记录详情
- `PUT /api/detection/record/{id}/process` - 更新处理状态
- `PUT /api/detection/record/process` - 处理检测记录
- `DELETE /api/detection/record/{id}` - 删除检测记录
- `DELETE /api/detection/record/batch` - 批量删除检测记录
- `DELETE /api/detection/record/clear-all` - 清空所有检测记录

### 文件接口
- `POST /api/file/upload` - 图片/文件上传
- `GET /api/file/download/{filename}` - 文件下载

## 快速开始

### 1. 环境要求
- JDK 11+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置
1. 创建MySQL数据库
2. 执行 `src/main/resources/sql/init.sql` 初始化数据库表和数据

### 3. 修改配置
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/traffic_monitoring?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: your_username
    password: your_password
```

### 4. 运行应用
```cmd
mvn clean compile
mvn spring-boot:run
```
应用将在 `http://localhost:9090` 启动，API基础路径为 `/api`

### 5. 默认账户
- 用户名：`admin`
- 密码：`admin123`

## 开发说明

### 响应格式
所有API响应都遵循统一格式：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

### 分页格式
分页查询响应格式：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "current": 1,
    "size": 10,
    "total": 100,
    "pages": 10,
    "records": []
  }
}
```

### JWT认证
- 除登录/注册接口外，其他接口都需要在请求头中携带JWT token
- Header格式：`Authorization: Bearer {token}`

### 图片上传
- 支持Base64格式图片上传和文件流上传
- 图片保存在 `data/uploads/images/` 目录下，按日期分目录存储
- 头像保存在 `data/uploads/avatars/` 目录
- 检测图片保存在 `data/uploads/detections/` 目录

## 注意事项

1. 项目使用了Lombok，请确保IDE安装了Lombok插件
2. 数据库连接信息需要根据实际环境修改
3. JWT密钥建议在生产环境中更改
4. 图片上传目录需要有写入权限
5. 跨域已配置，支持前端开发调试

## 扩展功能
- 可添加Redis缓存
- 可集成文件存储服务（如阿里云OSS）
- 可添加消息队列处理
- 可集成监控和日志系统
