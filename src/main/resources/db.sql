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
  `yymmddhh` varchar(8) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_live_yymmddhh` (`yymmddhh`),
  KEY `idx_live_fetched_at_entity_id` (`fetched_at`, `entity_id`),
  KEY `idx_live_entity_id_fetched_at_id` (`entity_id`, `fetched_at`, `id`)
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

CREATE TABLE IF NOT EXISTS `theme_park_poi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entity_id` varchar(128) NOT NULL,
  `name` varchar(255) NOT NULL,
  `english_name` varchar(255) DEFAULT NULL,
  `category` varchar(128) DEFAULT NULL,
  `img_url` varchar(1024) DEFAULT NULL,
  `primary_location_key` varchar(128) DEFAULT NULL,
  `latitude` decimal(10,6) DEFAULT NULL,
  `longitude` decimal(10,6) DEFAULT NULL,
  `raw_coordinates` text,
  `location_name` varchar(255) DEFAULT NULL,
  `guest_height` varchar(255) DEFAULT NULL,
  `thrill_level` varchar(255) DEFAULT NULL,
  `age_range` varchar(512) DEFAULT NULL,
  `interest_tags` varchar(512) DEFAULT NULL,
  `today_open_time` varchar(128) DEFAULT NULL,
  `raw_detail` text,
  `raw_schedule_time_info` text,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_theme_park_poi_entity_id` (`entity_id`),
  KEY `idx_theme_park_poi_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
