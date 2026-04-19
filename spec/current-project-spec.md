# ParkService 当前项目规格说明（Spec）

## 1. 项目概述

ParkService 是一个基于 Spring Boot 2.5.5 的后端服务。

当前项目包含两类能力：
1. 模板原有的计数器接口
2. 上海迪士尼实时数据抓取、缓存、入库能力

项目支持通过 Docker Compose 启动本地 MySQL 和 phpMyAdmin，并通过定时任务在指定时间段内自动抓取主题乐园实时数据。

## 2. 技术栈

- Java 8
- Spring Boot 2.5.5
- Spring Web
- Spring Scheduling
- Spring Data JPA
- MySQL 8
- Docker Compose
- Maven Wrapper

## 3. 本地运行方式

### 3.1 启动 MySQL 与可视化工具

```bash
docker compose up -d mysql phpmyadmin
```

默认配置：
- MySQL 容器：`parkservice-mysql`
- phpMyAdmin：`http://localhost:8081`
- 数据库：`park`
- 用户名：`young`
- 密码：`park123`
- root 密码：`root123456`
- 端口：`3306`

首次启动时会自动执行：
- `src/main/resources/db.sql`

### 3.2 Spring Boot 环境变量

```bash
export MYSQL_ADDRESS=127.0.0.1:3306
export MYSQL_DATABASE=park
export MYSQL_USERNAME=young
export MYSQL_PASSWORD=park123
```

### 3.3 启动应用

```bash
./mvnw spring-boot:run
```

## 4. 配置项

配置文件：`src/main/resources/application.yml`

### 4.1 服务端口
- `server.port=80`

### 4.2 数据库配置
- `spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver`
- `spring.datasource.url=jdbc:mysql://${MYSQL_ADDRESS}/${MYSQL_DATABASE:park}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai`
- `spring.datasource.username=${MYSQL_USERNAME}`
- `spring.datasource.password=${MYSQL_PASSWORD}`

### 4.3 主题乐园数据源配置
- `themepark.live.url`
- 默认值：`https://api.themeparks.wiki/v1/entity/6e1464ca-1e9b-49c3-8937-c5c6f6675057/live`

## 5. 模块说明

### 5.1 计数器模块

#### 接口
- `GET /api/count`：查询当前计数值
- `POST /api/count`：更新计数，自增或清零

#### 数据访问方式
- 使用 Spring Data JPA Repository：`CounterRepository`

### 5.2 主题乐园实时数据模块

#### 数据来源
- 第三方接口：`/v1/entity/{id}/live`
- 当前 entity id：`6e1464ca-1e9b-49c3-8937-c5c6f6675057`
- 当前目标数据：Shanghai Disney Resort

#### 核心能力
1. 拉取第三方 `/live` 接口数据
2. 将顶层 Entity 信息写入 `theme_park_entity`
3. 将 `liveData` 数组拆分为多条记录写入 `theme_park_live`
4. 在内存中缓存最近一次抓取结果
5. 提供 HTTP 接口返回最近一次实时数据
6. 通过定时任务自动抓取并打印日志

## 6. 数据模型

### 6.1 第三方返回模型
类：`ThemeParkLiveResponse`

顶层字段：
- `id`
- `name`
- `slug`
- `entityType`
- `parentId`
- `destinationId`
- `timezone`
- `externalId`
- `location`
- `tags`
- `liveData`

`liveData` 子项字段：
- `id`
- `name`
- `entityType`
- `parkId`
- `externalId`
- `queue`
- `status`
- `lastUpdated`

### 6.2 数据库存储模型

#### 表：`theme_park_entity`
用于存储顶层 Entity 信息。

主要字段：
- `entity_id`
- `name`
- `slug`
- `entity_type`
- `parent_id`
- `destination_id`
- `timezone`
- `external_id`
- `latitude`
- `longitude`
- `raw_location`
- `raw_tags`
- `fetched_at`

#### 表：`theme_park_live`
用于存储每次抓取时的实时排队和状态数据。

主要字段：
- `entity_id`
- `entity_name`
- `entity_type`
- `timezone`
- `status`
- `park_id`
- `external_id`
- `standby_wait_time`
- `single_rider_wait_time`
- `raw_queue`
- `live_last_updated`
- `fetched_at`

## 7. 对外接口规格

### 7.1 获取主题乐园实时数据

路径：
```http
GET /api/themepark/live
```

返回结构：
```json
{
  "code": 0,
  "errorMsg": "",
  "data": {
    "id": "6e1464ca-1e9b-49c3-8937-c5c6f6675057",
    "name": "Shanghai Disney Resort",
    "entityType": "DESTINATION",
    "timezone": "Asia/Shanghai",
    "liveData": []
  }
}
```

行为说明：
- 优先返回内存缓存
- 若缓存为空，则实时拉取第三方接口
- 拉取成功后同时完成数据库写入

## 8. 定时任务规格

类：`ThemeParkLiveScheduler`

定时表达式：
```java
@Scheduled(cron = "0 */5 9-22 * * ?", zone = "Asia/Shanghai")
```

执行规则：
- 每天 09:00 到 22:59
- 每 5 分钟执行一次
- 使用上海时区 `Asia/Shanghai`

任务行为：
1. 调用 `ThemeParkLiveService.fetchLiveData()`
2. 获取第三方最新实时数据
3. 更新内存缓存
4. 将 Entity 与 live 数据落库
5. 打印任务开始、成功、失败日志

## 9. 数据库初始化

初始化脚本：
- `src/main/resources/db.sql`
- `sql.sql`

包含数据表：
- `Counters`
- `theme_park_entity`
- `theme_park_live`

## 10. 日志要求

定时任务每次执行时应打印：
- 任务开始时间
- 是否执行成功
- 顶层 entity 信息
- `liveData` 数量
- 失败堆栈（如有）


## 11. 数据库健康检查接口

路径：
```http
GET /api/health/db
```

返回说明：
- 成功时返回数据库连接信息与 `SELECT 1` 结果
- 失败时返回当前数据库配置摘要以及异常类型、异常消息
