# SQL

## 1. 主题乐园实时数据快照表

```sql
CREATE TABLE `theme_park_live` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `entity_id` varchar(64) NOT NULL COMMENT '项目ID',
  `entity_name` varchar(255) NOT NULL COMMENT '项目名称',
  `entity_type` varchar(32) NOT NULL COMMENT '实体类型',
  `timezone` varchar(64) DEFAULT NULL COMMENT '时区',
  `status` varchar(32) DEFAULT NULL COMMENT '运营状态',
  `park_id` varchar(64) DEFAULT NULL COMMENT '园区ID',
  `external_id` varchar(255) DEFAULT NULL COMMENT '外部ID',
  `standby_wait_time` int(11) DEFAULT NULL COMMENT '普通排队等待时间（分钟）',
  `single_rider_wait_time` int(11) DEFAULT NULL COMMENT '单人通道等待时间（分钟）',
  `raw_queue` text COMMENT '原始queue JSON',
  `live_last_updated` datetime DEFAULT NULL COMMENT '第三方接口最后更新时间(UTC)',
  `fetched_at` datetime NOT NULL COMMENT '本系统抓取时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_entity_id` (`entity_id`),
  KEY `idx_status` (`status`),
  KEY `idx_fetched_at` (`fetched_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主题乐园实时数据快照表';
```

## 2. 示例说明

- 每次抓取 `/live` 接口后，会把 `liveData` 数组拆成多行写入 `theme_park_live`
- 一条游乐项目对应一条快照记录
- `raw_queue` 用于保留完整队列结构，便于后续扩展更多队列类型
- `fetched_at` 是你的系统实际抓取时间
- `live_last_updated` 是第三方返回的 `lastUpdated`

## 3. 实体信息快照表

```sql
CREATE TABLE `theme_park_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `entity_id` varchar(64) NOT NULL COMMENT '实体ID',
  `name` varchar(255) NOT NULL COMMENT '实体名称',
  `slug` varchar(255) DEFAULT NULL COMMENT '实体slug',
  `entity_type` varchar(32) NOT NULL COMMENT '实体类型',
  `parent_id` varchar(64) DEFAULT NULL COMMENT '父级实体ID',
  `destination_id` varchar(64) DEFAULT NULL COMMENT '目标目的地ID',
  `timezone` varchar(64) DEFAULT NULL COMMENT '时区',
  `external_id` varchar(255) DEFAULT NULL COMMENT '外部ID',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `raw_location` text COMMENT '原始location JSON',
  `raw_tags` text COMMENT '原始tags JSON',
  `fetched_at` datetime NOT NULL COMMENT '本系统抓取时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_theme_park_entity_entity_id` (`entity_id`),
  KEY `idx_entity_fetched_at` (`fetched_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主题乐园实体信息快照表';
```


### 数据
```sql
INSERT INTO `theme_park_entity` (
    `entity_id`,
    `name`,
    `slug`,
    `entity_type`,
    `parent_id`,
    `destination_id`,
    `timezone`,
    `external_id`,
    `latitude`,
    `longitude`,
    `raw_location`,
    `raw_tags`,
    `fetched_at`
  ) VALUES (
    '6e1464ca-1e9b-49c3-8937-c5c6f6675057',
    'Shanghai Disney Resort',
    'shanghaidisneyresort',
    'DESTINATION',
    NULL,
    NULL,
    'Asia/Shanghai',
    'shanghaidisneyresort',
    31.143040,
    121.658369,
    '{\"latitude\":31.14304,\"longitude\":121.658369}',
    NULL,
    NOW()
  );
```

### 实体信息字段来源

对应以下结构：

```json
{
  "id": "string",
  "name": "string",
  "entityType": "DESTINATION",
  "parentId": "string",
  "destinationId": "string",
  "timezone": "string",
  "location": {
    "latitude": 0,
    "longitude": 0
  },
  "tags": [
    {
      "tag": "string",
      "tagName": "string",
      "id": "string",
      "value": "string"
    }
  ]
}
```

说明：
- `location` 同时拆分存储为 `latitude`、`longitude`
- 原始 `location` 额外保存在 `raw_location`
- 原始 `tags` 保存在 `raw_tags`
- 每次抓取会插入一条新的实体快照记录
