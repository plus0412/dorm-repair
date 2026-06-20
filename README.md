# 高校宿舍维修报修管理系统

本项目是一个基于 Spring Boot + Vue 3 + Vite 的高校宿舍维修报修管理系统，包含：

- 后端：`dorm-repair`
- 前端：`dorm-repair-ui`
- 数据库脚本：`sql/01_create_tables.sql`、`sql/02_init_data.sql`

## 1. 运行环境

建议环境：

- JDK 17
- Maven 3.9+
- Node.js 18+ 或 20+
- MySQL 8.0
- Redis 6+

## 2. 当前项目配置

后端配置文件：

- `dorm-repair/src/main/resources/application.yml`

当前使用的数据库和 Redis 配置如下：

- MySQL：`192.168.174.150:3306`
- 数据库名：`dorm_repair`
- 用户名：`root`
- 密码：`123456`
- Redis：`192.168.174.150:6379`
- Redis 密码：无

后端默认端口：

- `8080`

前端默认端口：

- `80`

前端代理配置：

- `/api -> http://localhost:8080`

## 3. 初始化数据库

先确保 MySQL 和 Redis 已启动。

然后按顺序执行下面两个 SQL 文件：

1. `sql/01_create_tables.sql`
2. `sql/02_init_data.sql`

说明：

- `01_create_tables.sql` 中已经包含 `CREATE DATABASE IF NOT EXISTS dorm_repair`
- `02_init_data.sql` 会写入初始账号数据

## 4. 默认测试账号

初始化数据中的默认账号如下：

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | `admin` | `123456` |
| 学生 | `student` | `123456` |
| 维修人员 | `repair` | `123456` |

## 5. 启动后端

### 方式一：用 IDEA 启动

启动主类：

- `com.example.dormrepair.DormRepairApplication`

### 方式二：命令行启动

进入后端目录：

```powershell
cd G:\smxy\biyesheji\dorm-repair
```

启动项目：

```powershell
mvn spring-boot:run
```

如果你已经打过包，也可以直接运行：

```powershell
cd G:\smxy\biyesheji\dorm-repair
java -jar target\dorm-repair-0.0.1-SNAPSHOT.jar
```

后端启动成功后访问：

- 项目接口：`http://localhost:8080`
- Knife4j 文档：`http://localhost:8080/doc.html`

## 6. 启动前端

进入前端目录：

```powershell
cd G:\smxy\biyesheji\dorm-repair-ui
```

首次启动前如果没有安装依赖，先执行：

```powershell
npm install
```

正常启动：

```powershell
npm run dev
```

如果前端成功启动，控制台一般会显示访问地址。

### 前端默认访问地址

由于当前 `vite.config.js` 配置的是 `80` 端口，所以默认访问：

- `http://localhost`

### 如果 80 端口被占用

可以临时改用 5173 端口启动：

```powershell
npm run dev -- --host 127.0.0.1 --port 5173
```

然后访问：

- `http://127.0.0.1:5173`

说明：

- 即使改成 `5173`，前端依然会把 `/api` 请求代理到 `http://localhost:8080`
- 所以前端改端口不会影响后端接口调用

## 7. 推荐启动顺序

建议按下面顺序启动：

1. 启动 MySQL
2. 启动 Redis
3. 执行 SQL 脚本（首次初始化时）
4. 启动后端 `dorm-repair`
5. 启动前端 `dorm-repair-ui`

## 8. 常见问题

### 1）前端打不开

先确认前端控制台有没有正常输出地址。

再检查：

- 是否执行了 `npm install`
- `80` 端口是否被占用
- 后端是否已经启动

如果 `80` 端口被占用，直接用：

```powershell
npm run dev -- --host 127.0.0.1 --port 5173
```

### 2）后端启动失败

优先检查：

- MySQL 是否启动
- Redis 是否启动
- `application.yml` 里的 IP、端口、用户名、密码是否正确
- `dorm_repair` 数据库是否已创建

### 3）登录后接口报错

一般先检查：

- 两个 SQL 是否都执行了
- 后端是否连上数据库
- 浏览器是否访问的是正确的前端地址

## 9. 项目目录说明

```text
G:\smxy\biyesheji
├─ dorm-repair         后端 Spring Boot 项目
├─ dorm-repair-ui      前端 Vue 项目
├─ sql                 数据库脚本
├─ 毕业设计文档.md
└─ 概要设计.md
```

## 10. 手动关闭服务

如果是命令行启动，直接关闭对应终端窗口即可。

如果端口被占用，可以先查端口再结束进程，例如：

```powershell
netstat -ano | findstr 8080
netstat -ano | findstr 80
netstat -ano | findstr 5173
```

查到 PID 后结束进程：

```powershell
taskkill /PID 进程ID /F
```
