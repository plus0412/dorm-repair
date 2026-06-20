# 高校宿舍维修报修管理系统

本项目是一个基于 Spring Boot + Vue 3 + Vite 的高校宿舍维修报修管理系统。

项目结构：

- `dorm-repair`：后端
- `dorm-repair-ui`：前端
- `sql`：数据库脚本

## 运行环境

- JDK 17
- Maven 3.9+
- Node.js 18+
- MySQL 8.0
- Redis 6+

## 本地配置说明

后端配置文件：

- `dorm-repair/src/main/resources/application.yml`

说明：

- 该文件包含本地运行配置
- 当前仓库默认不提交这个文件
- 请根据自己的环境填写数据库、Redis、JWT、OSS 等配置

## 数据库初始化

首次运行时，按顺序执行：

1. `sql/01_create_tables.sql`
2. `sql/02_init_data.sql`

说明：

- `01_create_tables.sql` 包含数据库创建语句
- `02_init_data.sql` 包含初始化数据

## 启动后端

方式一：使用 IDEA 启动

主类：

- `com.example.dormrepair.DormRepairApplication`

方式二：命令行启动

```powershell
cd G:\smxy\biyesheji\dorm-repair
mvn spring-boot:run
```

如果已经完成打包，也可以直接运行：

```powershell
cd G:\smxy\biyesheji\dorm-repair
java -jar target\dorm-repair-0.0.1-SNAPSHOT.jar
```

后端默认端口：

- `8080`

启动后可访问：

- 接口地址：`http://localhost:8080`
- Knife4j：`http://localhost:8080/doc.html`

## 启动前端

```powershell
cd G:\smxy\biyesheji\dorm-repair-ui
npm install
npm run dev
```

前端默认按 `vite.config.js` 中的配置启动。

如果默认端口被占用，可以临时指定端口：

```powershell
npm run dev -- --host 127.0.0.1 --port 5173
```

对应访问地址：

- `http://127.0.0.1:5173`

## 推荐启动顺序

1. 启动 MySQL
2. 启动 Redis
3. 首次执行 SQL 脚本
4. 启动后端
5. 启动前端

## 图片上传

当前项目图片上传已接入阿里云 OSS。

说明：

- 新上传图片不会存到本地 `uploads` 目录
- 图片相关配置请在本地 `application.yml` 中维护

## 常见问题

### 前端无法访问

检查：

- 是否执行过 `npm install`
- 前端端口是否被占用
- 后端是否已经启动

### 后端启动失败

检查：

- MySQL 是否启动
- Redis 是否启动
- `application.yml` 是否填写完整
- 数据库是否已初始化

### 接口报错

检查：

- 两个 SQL 是否都已执行
- 后端是否连接成功数据库
- 前端访问地址是否正确

## 目录说明

```text
G:\smxy\biyesheji
├─ dorm-repair
├─ dorm-repair-ui
├─ sql
├─ README.md
├─ 毕业设计文档.md
└─ 概要设计.md
```

## 关闭服务

如果是终端启动，直接关闭终端即可。

如果要按端口查进程：

```powershell
netstat -ano | findstr 8080
netstat -ano | findstr 80
netstat -ano | findstr 5173
```

查到 PID 后结束：

```powershell
taskkill /PID 进程ID /F
```
