-- 预置 IoT 点位与映射（与前端默认演示一致；现场请按 KEPServer 修改 source_path）
-- 执行示例：psql -U coal_user -d coal_platform -f deploy/sql/seed-iot-tags.sql

INSERT INTO iot_tag (tag_code, tag_name, source_type, source_path, device_code, device_name, area_code, data_type, unit, scan_rate, deadband, quality_rule, enabled, remark, created_at, updated_at)
SELECT 'coal.feed.rate', '原煤给料量', 'MOCK', 'mock.feed.rate', 'FEEDER-01', '1号给料机', 'plant', 'DOUBLE', 't/h', 1000, 0, '', true, '演示/可改为 OPC 标识符', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM iot_tag WHERE tag_code = 'coal.feed.rate');

INSERT INTO iot_tag (tag_code, tag_name, source_type, source_path, device_code, device_name, area_code, data_type, unit, scan_rate, deadband, quality_rule, enabled, remark, created_at, updated_at)
SELECT 'crusher.current', '破碎机电流', 'MOCK', 'mock.crusher.current', 'CRUSHER-01', '主破碎机', 'plant', 'DOUBLE', 'A', 1000, 0, '', true, '', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM iot_tag WHERE tag_code = 'crusher.current');

INSERT INTO iot_tag (tag_code, tag_name, source_type, source_path, device_code, device_name, area_code, data_type, unit, scan_rate, deadband, quality_rule, enabled, remark, created_at, updated_at)
SELECT 'screen.vibration', '振动筛振幅', 'MOCK', 'mock.screen.vibration', 'SCREEN-01', '1号振动筛', 'plant', 'DOUBLE', 'mm', 500, 0, '', true, '', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM iot_tag WHERE tag_code = 'screen.vibration');

INSERT INTO iot_tag (tag_code, tag_name, source_type, source_path, device_code, device_name, area_code, data_type, unit, scan_rate, deadband, quality_rule, enabled, remark, created_at, updated_at)
SELECT 'belt.speed', '皮带速度', 'MOCK', 'mock.belt.speed', 'BELT-01', '主运输皮带', 'plant', 'DOUBLE', 'm/s', 1000, 0, '', false, '', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM iot_tag WHERE tag_code = 'belt.speed');

INSERT INTO iot_tag (tag_code, tag_name, source_type, source_path, device_code, device_name, area_code, data_type, unit, scan_rate, deadband, quality_rule, enabled, remark, created_at, updated_at)
SELECT 'energy.power.total', '总用电累计', 'MOCK', 'mock.energy.power', 'ENERGY', '能耗汇总', 'energy', 'DOUBLE', 'kWh', 1000, 0, '', true, 'EnergyV2 历史曲线用', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM iot_tag WHERE tag_code = 'energy.power.total');

INSERT INTO iot_tag_mapping (mapping_id, tag_code, business_code, business_name, source_path, transform_rule, enabled, remark, created_at, updated_at)
SELECT 'MAP-001', 'coal.feed.rate', 'dashboard.feedRate', '大屏给料量', 'mock.feed.rate', 'identity', true, '', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM iot_tag_mapping WHERE mapping_id = 'MAP-001');

INSERT INTO iot_tag_mapping (mapping_id, tag_code, business_code, business_name, source_path, transform_rule, enabled, remark, created_at, updated_at)
SELECT 'MAP-002', 'crusher.current', 'equipment.crusherCurrent', '破碎机电流', 'mock.crusher.current', 'identity', true, '', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM iot_tag_mapping WHERE mapping_id = 'MAP-002');

INSERT INTO iot_tag_mapping (mapping_id, tag_code, business_code, business_name, source_path, transform_rule, enabled, remark, created_at, updated_at)
SELECT 'MAP-003', 'screen.vibration', 'equipment.screenVibration', '振动筛振幅', 'mock.screen.vibration', 'identity', true, '', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM iot_tag_mapping WHERE mapping_id = 'MAP-003');
