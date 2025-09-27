# 智能交通监控后端系统

这是一个基于Spring Boot + MyBatis-Plus + MySQL的智能交通监控后端系统，对应前端的Vue.js应用。

## 项目说明

本项目是一个交通监控系统的后端 API 服务，提供用户管理、摄像头管理、检测记录管理和文件上传等功能。

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
- 密码：`123456`

## 快速使用

### 方式一：下载Release版本（推荐）

1. **下载JAR包**
   - 前往 [Releases](../../releases) 页面
   - 下载最新版本的 `traffic-monitoring-springboot-x.x.x.jar`

2. **准备数据库**
   - 创建 MySQL 数据库 `traffic_monitoring`
   - 下载并执行 [init.sql](src/main/resources/sql/init.sql) 初始化数据库

3. **运行应用**
   ```bash
   java -jar traffic-monitoring-springboot-1.0.4.jar
   ```
   应用将在 `http://localhost:9090` 启动，API基础路径为 `/api`

4. **默认账户**
   - 用户名：`admin`
   - 密码：`123456`

### 方式二：源码运行（开发）

1. **克隆项目**
   ```bash
   git clone https://github.com/Theowastaken/traffic-monitoring-springboot.git
   cd traffic-monitoring-springboot
   ```

2. **配置数据库**
   编辑 `src/main/resources/application.yml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/traffic_monitoring?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
       username: your_username
       password: your_password
   ```

3. **运行项目**
   ```cmd
   mvn clean compile
   mvn spring-boot:run
   ```

## 配置参数

### 通过命令行参数配置

可以通过 `--key=value` 的方式覆盖默认配置：

```bash
java -jar traffic-monitoring-springboot-1.0.4.jar \
  --server.port=8081 \
  --upload.base-url=http://localhost:8081/api \
  --spring.web.resources.static-locations=file:/custom/uploads/ \
  --spring.datasource.password=yourPassword \
  --spring.datasource.url="jdbc:mysql://localhost:3306/traffic_monitoring?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8" \
  --spring.datasource.username=root \
  --upload.base-dir=/custom/uploads \
  --jwt.secret=yourCustomJWTSecret \
  --jwt.expiration=86400
```

### 配置参数说明

| 参数名 | 说明 | 默认值 | 示例 |
|--------|------|--------|------|
| `server.port` | 服务端口 | 9090 | `--server.port=8080` |
| `server.servlet.context-path` | 上下文路径 | /api | `--server.servlet.context-path=/api/v1` |
| `upload.base-dir` | 文件上传基础目录 | data/uploads | `--upload.base-dir=/var/uploads` |
| `upload.base-url` | 文件访问基础URL | http://localhost:9090/api | `--upload.base-url=http://your-domain.com/api` |
| `spring.web.resources.static-locations` | 静态资源路径 | file:/data/uploads/ | `--spring.web.resources.static-locations=file:/var/uploads/` |
| `spring.datasource.url` | 数据库连接URL | jdbc:mysql://localhost:3306/traffic_monitoring... | `--spring.datasource.url="jdbc:mysql://db-server:3306/traffic"` |
| `spring.datasource.username` | 数据库用户名 | root | `--spring.datasource.username=dbuser` |
| `spring.datasource.password` | 数据库密码 | 123456789 | `--spring.datasource.password=securePassword` |
| `jwt.secret` | JWT 签名密钥 | trafficMonitoringSecretKey2023... | `--jwt.secret=yourSecretKey` |
| `jwt.expiration` | JWT 过期时间(秒) | 604800 | `--jwt.expiration=86400` |

### 通过环境变量配置

也可以通过环境变量的方式配置：

**Windows (cmd):**
```cmd
set SERVER_PORT=8081
set UPLOAD_BASE_URL=http://localhost:8081/api
set SPRING_WEB_RESOURCES_STATIC_LOCATIONS=file:/custom/uploads/
set SPRING_DATASOURCE_PASSWORD=yourPassword
java -jar traffic-monitoring-springboot-1.0.4.jar
```

**Linux/MacOS:**
```bash
export SERVER_PORT=8081
export UPLOAD_BASE_URL=http://localhost:8081/api
export SPRING_WEB_RESOURCES_STATIC_LOCATIONS=file:/custom/uploads/
export SPRING_DATASOURCE_PASSWORD=yourPassword
java -jar traffic-monitoring-springboot-1.0.4.jar
```

### 使用外部配置文件

创建一个外部的 application-prod.yml 文件：

```yaml
server:
  port: 8080
spring:
  datasource:
    password: productionPassword
upload:
  base-dir: /var/uploads
  base-url: http://your-domain.com/api
```

然后使用以下命令运行：
```bash
java -jar traffic-monitoring-springboot-1.0.4.jar --spring.profiles.active=prod --spring.config.location=classpath:/,./application-prod.yml
```

### 配置优先级

Spring Boot 配置的优先级（从高到低）：
1. 命令行参数（--key=value）
2. 操作系统环境变量
3. 外部配置文件
4. jar 包内的 application.yml

### 生产环境建议

1. **安全性配置**：
   - 修改默认的 JWT secret
   - 使用强密码
   - 配置 HTTPS

2. **性能配置**：
   ```bash
   java -Xms512m -Xmx2g -jar traffic-monitoring-springboot-1.0.4.jar
   ```

3. **日志配置**：
   ```bash
   --logging.level.org.example=info \
   --logging.level.com.baomidou.mybatisplus=warn \
   --logging.file.name=logs/application.log
   ```

## 开发者指南

### 项目打包发布

如果你是开发者需要打包发布新版本：

1. **更新版本号**
   编辑 `pom.xml` 中的 `<version>` 标签

2. **打包项目**
   ```bash
   mvn clean package -DskipTests
   ```

3. **创建Release**
   - 在 GitHub 上创建新的 Release
   - 上传生成的 `target/traffic-monitoring-springboot-x.x.x.jar`
   - 添加版本更新说明

4. **测试发布版本**
   ```bash
   java -jar traffic-monitoring-springboot-x.x.x.jar
   ```

## API 文档

启动服务后，可以通过以下地址访问：
- 服务地址: http://localhost:9090/api
- 健康检查: http://localhost:9090/api/actuator/health（如果启用了 actuator）

## 目录结构

```
data/
├── uploads/
│   ├── avatars/     # 用户头像
│   ├── detections/  # 检测图片
│   └── images/      # 其他图片
```

## 常见问题

### 1. 端口被占用
```bash
# 查看端口占用
netstat -ano | findstr :9090
# 使用不同端口启动
java -jar TrafficMonitoringBackend.jar --server.port=8080
```

### 2. 文件上传路径问题
确保上传目录存在且有写权限：
```bash
mkdir -p /var/uploads
chmod 755 /var/uploads
```

### 3. 数据库连接问题
检查数据库服务是否启动，用户名密码是否正确：
```bash
mysql -u root -p -h localhost -P 3306
```
