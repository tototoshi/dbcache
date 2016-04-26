CREATE TABLE `cache_entries` (
  `key` varchar(191) PRIMARY KEY,
  `value` mediumblob NOT NULL,
  `expired_at` datetime,
  `created_at` datetime NOT NULL,
  INDEX (`expired_at`),
  INDEX (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4