CREATE TABLE IF NOT EXISTS `Counters` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `count` int(11) NOT NULL DEFAULT '1',
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `theme_park_live` (
                                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                 `entity_id` varchar(64) NOT NULL,
                                                 `entity_name` varchar(255) NOT NULL,
                                                 `entity_type` varchar(32) NOT NULL,
                                                 `timezone` varchar(64) DEFAULT NULL,
                                                 `status` varchar(32) DEFAULT NULL,
                                                 `park_id` varchar(64) DEFAULT NULL,
                                                 `external_id` varchar(255) DEFAULT NULL,
                                                 `standby_wait_time` int(11) DEFAULT NULL,
                                                 `single_rider_wait_time` int(11) DEFAULT NULL,
                                                 `raw_queue` text,
                                                 `live_last_updated` datetime DEFAULT NULL,
                                                 `fetched_at` datetime NOT NULL,
                                                 `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                 `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                 PRIMARY KEY (`id`),
                                                 KEY `idx_entity_id` (`entity_id`),
                                                 KEY `idx_status` (`status`),
                                                 KEY `idx_fetched_at` (`fetched_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `theme_park_entity` (
                                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                   `entity_id` varchar(64) NOT NULL,
                                                   `name` varchar(255) NOT NULL,
                                                   `slug` varchar(255) DEFAULT NULL,
                                                   `entity_type` varchar(32) NOT NULL,
                                                   `parent_id` varchar(64) DEFAULT NULL,
                                                   `destination_id` varchar(64) DEFAULT NULL,
                                                   `timezone` varchar(64) DEFAULT NULL,
                                                   `external_id` varchar(255) DEFAULT NULL,
                                                   `latitude` decimal(10,6) DEFAULT NULL,
                                                   `longitude` decimal(10,6) DEFAULT NULL,
                                                   `raw_location` text,
                                                   `raw_tags` text,
                                                   `fetched_at` datetime NOT NULL,
                                                   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                   `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                   PRIMARY KEY (`id`),
                                                   UNIQUE KEY `uk_theme_park_entity_entity_id` (`entity_id`),
                                                   KEY `idx_entity_fetched_at` (`fetched_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
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
  '{"latitude":31.14304,"longitude":121.658369}',
  NULL,
  NOW()
);
