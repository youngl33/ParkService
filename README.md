# ParkService

基于 Spring Boot 2.5.5 的后端服务，当前主要提供两类能力：

1. 模板遗留的计数器接口
2. 主题乐园实时数据抓取、落库与查询接口

当前主题乐园部分默认接入 `themeparks.wiki` 的实时数据接口，已配置的默认园区为上海迪士尼度假区。

## 技术栈

- Java 8
- Spring Boot 2.5.5
- Spring Web
- Spring Data JPA
- Spring Scheduling
- MySQL 8
- Docker Compose

## 项目结构

```text
.
├── docker-compose.yml
├── pom.xml
├── poi.sql
├── spec/
├── src/
│   └── main/
│       ├── java/com/tencent/wxcloudrun/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── model/
│       │   ├── repository/
│       │   ├── service/
│       │   └── task/
│       └── resources/
│           ├── application.yml
│           └── db.sql
└── Dockerfile
```

## 核心能力

### 1. 主题乐园实时数据同步

- 定时从第三方接口拉取 `/v1/entity/{entityId}/live`
- 将园区实体信息写入 `theme_park_entity`
- 将 `liveData` 明细写入 `theme_park_live`
- 在内存中缓存最近一次拉取结果
- 提供最新批次查询、单项目查询、排序查询、POI 融合查询

### 2. POI 查询增强

- 查询 `theme_park_poi` 并拼接实时状态与等待时间
- 返回字段 `iamge_url`
- `iamge_url` 的值规则为：
  `THEMEPARK_POI_IMAGE_URL_PREFIX + entity_id + ".png"`

### 3. 模板计数器接口

- 保留 `GET /api/count`
- 保留 `POST /api/count`

### 4. 数据库健康检查

- 提供 `GET /api/health/db`

## 环境变量

应用配置位于 [application.yml](/Users/bytedance/WeChatProjects/ParkService/src/main/resources/application.yml:1)。

### 数据库

- `MYSQL_ADDRESS`
  默认值：`127.0.0.1:3306`
- `MYSQL_DATABASE`
  默认值：`park`
- `MYSQL_USERNAME`
  默认值：`young`
- `MYSQL_PASSWORD`
  默认值：`park123`

### 主题乐园同步

- `THEMEPARK_POI_IMAGE_URL_PREFIX`
  示例：`https://cdn.example.com/themepark/`
  用于拼接 POI 返回中的 `iamge_url`

`themepark.live.entity-ids` 和 `themepark.live.url-template` 当前写在配置文件中，默认值如下：

- `url-template`: `https://api.themeparks.wiki/v1/entity/%s/live`
- `entity-ids`: `6e1464ca-1e9b-49c3-8937-c5c6f6675057`

## 本地运行

### 1. 启动 MySQL

```bash
docker compose up -d mysql phpmyadmin
```

启动后：

- MySQL: `127.0.0.1:3306`
- phpMyAdmin: `http://localhost:8081`
- 数据库名: `park`
- 普通用户: `young`
- 普通用户密码: `park123`
- root 密码: `root123456`

初始化脚本：

- [db.sql](/Users/bytedance/WeChatProjects/ParkService/src/main/resources/db.sql:1)

### 2. 配置环境变量

```bash
export MYSQL_ADDRESS=127.0.0.1:3306
export MYSQL_DATABASE=park
export MYSQL_USERNAME=young
export MYSQL_PASSWORD=park123
export THEMEPARK_POI_IMAGE_URL_PREFIX=https://cdn.example.com/themepark/
```

### 3. 启动应用

如果本机安装了 Maven：

```bash
mvn spring-boot:run
```

如果你希望使用 Maven Wrapper，需要先补齐仓库中的 `.mvn/wrapper` 文件；当前仓库只有 `mvnw` 脚本，没有完整 wrapper 目录。

默认服务端口：

- `80`

如果本地调试不想占用低位端口，建议通过额外配置覆盖 `server.port`。

## 定时任务

定时任务定义在 [ThemeParkLiveScheduler.java](/Users/bytedance/WeChatProjects/ParkService/src/main/java/com/tencent/wxcloudrun/task/ThemeParkLiveScheduler.java:1)。

执行表达式：

```java
@Scheduled(cron = "0 */5 9-22 * * ?", zone = "Asia/Shanghai")
```

执行规则：

- 时区：`Asia/Shanghai`
- 每天 `09:00` 到 `22:59`
- 每 `5` 分钟执行一次

任务行为：

1. 拉取最新主题乐园实时数据
2. 写入 `theme_park_entity`
3. 写入 `theme_park_live`
4. 更新服务内存缓存
5. 打印同步日志

## API

统一响应结构：

```json
{
  "code": 0,
  "errorMsg": "",
  "data": {}
}
```

失败时 `code = -1`。

### 1. 获取最新主题乐园原始实时数据

`GET /api/themepark/live`

说明：

- 优先返回内存中的最近结果
- 如果缓存为空，会主动调用第三方接口拉取

### 2. 查询最新批次中某个实体的等待信息

`GET /api/park/live/{entityId}`

示例：

```bash
curl http://localhost/api/park/live/attTronLightcyclePowerRun
```

### 3. 查询最新批次全部实时数据

`GET /api/park/live/latest`

返回内容包含：

- `fetchedAt`
- `count`
- `items`
- `groupedByDestinationOrPark`

### 4. 查询最新批次等待时间排行

`GET /api/park/live/latest/rank`

说明：

- 按 `standbyWaitTime` 倒序排序

### 5. 查询 POI + 最新实时状态

`GET /api/park/live/latest/poi`

返回内容除了 `theme_park_poi` 基础字段外，还会带上实时字段：

- `status`
- `standbyWaitTime`
- `singleRiderWaitTime`
- `queue`
- `liveLastUpdated`
- `liveFetchedAt`
- `iamge_url`

`iamge_url` 示例：

```json
{
  "entityId": "attTronLightcyclePowerRun",
  "iamge_url": "https://cdn.example.com/themepark/attTronLightcyclePowerRun.png"
}
```

### 6. 数据库健康检查

`GET /api/health/db`

成功时会返回：

- `mysqlAddress`
- `mysqlDatabase`
- `mysqlUsername`
- `connected`
- `ping`

失败时还会返回：

- `errorType`
- `errorMessage`

### 7. 计数器接口

获取计数：

`GET /api/count`

更新计数：

`POST /api/count`

请求体：

```json
{
  "action": "inc"
}
```

支持的 `action`：

- `inc`
- `clear`

## 数据库表

数据库初始化脚本见 [db.sql](/Users/bytedance/WeChatProjects/ParkService/src/main/resources/db.sql:1)。

当前核心表：

- `Counters`
- `theme_park_entity`
- `theme_park_live`
- `theme_park_poi`

### theme_park_entity

存储乐园实体信息，如园区、景点等顶层实体快照。

关键字段：

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

### theme_park_live

存储每次同步产生的实时状态与等待时间。

关键字段：

- `entity_id`
- `entity_name`
- `entity_type`
- `status`
- `park_id`
- `standby_wait_time`
- `single_rider_wait_time`
- `raw_queue`
- `live_last_updated`
- `fetched_at`

### theme_park_poi

存储 POI 基础信息，用于和最新一批实时数据做融合查询。

关键字段：

- `entity_id`
- `name`
- `english_name`
- `category`
- `img_url`
- `primary_location_key`
- `latitude`
- `longitude`
- `location_name`
- `guest_height`
- `thrill_level`
- `age_range`
- `interest_tags`
- `today_open_time`

## 相关代码入口

- 应用入口：[WxCloudRunApplication.java](/Users/bytedance/WeChatProjects/ParkService/src/main/java/com/tencent/wxcloudrun/WxCloudRunApplication.java:1)
- 主题乐园接口：[ThemeParkLiveController.java](/Users/bytedance/WeChatProjects/ParkService/src/main/java/com/tencent/wxcloudrun/controller/ThemeParkLiveController.java:1)
- 主题乐园服务：[ThemeParkLiveServiceImpl.java](/Users/bytedance/WeChatProjects/ParkService/src/main/java/com/tencent/wxcloudrun/service/impl/ThemeParkLiveServiceImpl.java:1)
- 定时任务：[ThemeParkLiveScheduler.java](/Users/bytedance/WeChatProjects/ParkService/src/main/java/com/tencent/wxcloudrun/task/ThemeParkLiveScheduler.java:1)
- 健康检查：[HealthController.java](/Users/bytedance/WeChatProjects/ParkService/src/main/java/com/tencent/wxcloudrun/controller/HealthController.java:1)
- 计数器接口：[CounterController.java](/Users/bytedance/WeChatProjects/ParkService/src/main/java/com/tencent/wxcloudrun/controller/CounterController.java:1)

## 说明

- 项目里仍保留了模板生成的一些基础文件和计数器功能。
- 当前 README 以实际代码行为为准，不再按原始模板文案描述。
- `iamge_url` 字段名保持与当前接口返回一致；如果后续要修正拼写，建议前后端一起改约定。
