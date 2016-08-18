CREATE TABLE `cache_entries` (
  `cache_key` varchar(191) PRIMARY KEY,
  `cache_value` mediumblob NOT NULL,
  `expired_at` datetime,
  `created_at` datetime NOT NULL,
  INDEX (`expired_at`),
  INDEX (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4