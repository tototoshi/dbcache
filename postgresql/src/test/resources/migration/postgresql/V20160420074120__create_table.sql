CREATE TABLE cache_entries (
  cache_key varchar(191) PRIMARY KEY,
  cache_value bytea NOT NULL,
  expired_at timestamp,
  created_at timestamp NOT NULL,
  updated_at timestamp NOT NULL
);

create index on cache_entries(expired_at);
create index on cache_entries(created_at);
