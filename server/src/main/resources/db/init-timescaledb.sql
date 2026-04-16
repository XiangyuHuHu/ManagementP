-- ============================================================
-- 煤炭管控平台 — TimescaleDB 初始化脚本
-- 前提：已安装 PostgreSQL 15 + TimescaleDB 扩展
-- 使用方法：psql -U postgres -d coal_platform -f init-timescaledb.sql
-- ============================================================

-- 启用 TimescaleDB 扩展
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- ============================================================
-- 1. 实时最新值表（替代 Redis，每个 tag 只保留最新一行）
-- ============================================================
CREATE TABLE IF NOT EXISTS iot_realtime (
    tag_code    VARCHAR(128) PRIMARY KEY,
    value       DOUBLE PRECISION,
    quality     SMALLINT DEFAULT 192,
    unit        VARCHAR(32),
    device_code VARCHAR(128),
    area_code   VARCHAR(128),
    source_type VARCHAR(64) DEFAULT 'KEPSERVER',
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_iot_realtime_device ON iot_realtime (device_code);
CREATE INDEX IF NOT EXISTS idx_iot_realtime_area ON iot_realtime (area_code);

-- ============================================================
-- 2. 历史时序数据表（TimescaleDB 超表）
-- ============================================================
CREATE TABLE IF NOT EXISTS iot_history (
    id          BIGSERIAL,
    time        TIMESTAMPTZ NOT NULL,
    tag_code    VARCHAR(128) NOT NULL,
    value       DOUBLE PRECISION,
    quality     SMALLINT DEFAULT 192,
    source_type VARCHAR(64) DEFAULT 'KEPSERVER'
);

-- 转为超表（按 7 天一个分区块）
SELECT create_hypertable('iot_history', 'time',
    chunk_time_interval => INTERVAL '7 days',
    if_not_exists => TRUE
);

CREATE INDEX IF NOT EXISTS idx_iot_history_tag_time ON iot_history (tag_code, time DESC);

-- ============================================================
-- 3. 自动数据保留策略：90 天后自动清理
-- ============================================================
SELECT add_retention_policy('iot_history', INTERVAL '90 days', if_not_exists => TRUE);

-- ============================================================
-- 4. 连续聚合：每分钟均值/最大/最小（报表和趋势图用）
-- ============================================================
CREATE MATERIALIZED VIEW IF NOT EXISTS iot_history_1min
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 minute', time) AS bucket,
    tag_code,
    AVG(value) AS avg_val,
    MIN(value) AS min_val,
    MAX(value) AS max_val,
    COUNT(*) AS sample_count
FROM iot_history
GROUP BY bucket, tag_code
WITH NO DATA;

-- 自动刷新策略：每 1 分钟刷新，覆盖最近 5 分钟数据
SELECT add_continuous_aggregate_policy('iot_history_1min',
    start_offset    => INTERVAL '5 minutes',
    end_offset      => INTERVAL '1 minute',
    schedule_interval => INTERVAL '1 minute',
    if_not_exists   => TRUE
);

-- ============================================================
-- 5. 连续聚合：每小时均值（日报表用）
-- ============================================================
CREATE MATERIALIZED VIEW IF NOT EXISTS iot_history_1h
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', time) AS bucket,
    tag_code,
    AVG(value) AS avg_val,
    MIN(value) AS min_val,
    MAX(value) AS max_val,
    COUNT(*) AS sample_count
FROM iot_history
GROUP BY bucket, tag_code
WITH NO DATA;

SELECT add_continuous_aggregate_policy('iot_history_1h',
    start_offset    => INTERVAL '2 hours',
    end_offset      => INTERVAL '1 hour',
    schedule_interval => INTERVAL '1 hour',
    if_not_exists   => TRUE
);
