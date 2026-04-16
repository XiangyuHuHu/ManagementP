# KEPServer 对接预留接口设计

## 1. 目标

本文档用于约定选煤厂一体化管控平台在后续接入 `KEPServerEX` 时，需要提前预留的接口、数据模型和系统边界。

设计原则：

- 平台业务服务不直接依赖 KEPServer 点位命名。
- 前端不直接访问 KEPServer。
- 采集层、标准化层、业务层分离。
- 实时数据、历史数据、报警事件采用统一接口模型。
- 后续即使替换为 OPC UA 直连、PLC 驱动或其他网关，平台接口保持不变。

## 2. 推荐架构

建议采用以下分层：

1. 设备采集层  
   `PLC / 仪表 / 传感器 -> KEPServerEX`

2. 接入适配层  
   `KEPServer Adapter / iot-gateway`

3. 平台标准化层  
   统一把原始点位转换为平台标准模型：
   - 设备
   - 点位
   - 实时值
   - 历史值
   - 报警事件

4. 业务消费层  
   生产管理、设备管理、能耗管理、报警管理、大屏等模块统一消费平台标准接口。

## 3. 接入边界

### 3.1 KEPServer 负责什么

- 与 PLC、仪表、传感器通信
- 采集原始点位值
- 提供 OPC UA / API 访问能力
- 维护采样周期、质量码、连接状态

### 3.2 平台 Adapter 负责什么

- 连接 KEPServer
- 拉取或订阅原始点位数据
- 做点位映射、单位转换、类型转换
- 写入平台实时缓存
- 异步归档历史数据
- 触发报警判断或接收报警事件

### 3.3 业务系统负责什么

- 展示实时监控
- 查询历史趋势
- 展示和处理报警
- 根据标准点位做业务统计、报表、联动

## 4. 标准数据模型

### 4.1 点位定义 `TagDefinition`

```json
{
  "tagCode": "coal.feed.rate",
  "tagName": "原煤给料量",
  "sourceType": "KEPSERVER",
  "sourcePath": "Channel1.Device1.Tag001",
  "deviceCode": "FEEDER-01",
  "deviceName": "1号给料机",
  "areaCode": "feed-workshop",
  "dataType": "DOUBLE",
  "unit": "t/h",
  "scanRate": 1000,
  "deadband": 0.1,
  "qualityRule": "GOOD_ONLY",
  "enabled": true,
  "remark": "生产核心测点"
}
```

说明：

- `tagCode` 是平台统一标识，后续前后端都只认这个字段。
- `sourcePath` 保存 KEPServer 原始点位路径。
- `scanRate`、`deadband` 建议在平台侧保留，便于后续统一治理。

### 4.2 实时值 `RealtimeValue`

```json
{
  "tagCode": "coal.feed.rate",
  "value": 428.6,
  "valueText": "428.6",
  "dataType": "DOUBLE",
  "unit": "t/h",
  "quality": "GOOD",
  "timestamp": "2026-03-20T10:15:21+08:00",
  "sourceTime": "2026-03-20T10:15:21+08:00",
  "deviceCode": "FEEDER-01",
  "areaCode": "feed-workshop",
  "sourceType": "KEPSERVER"
}
```

### 4.3 历史值 `HistoryValue`

```json
{
  "tagCode": "coal.feed.rate",
  "time": "2026-03-20T10:15:00+08:00",
  "value": 427.9,
  "quality": "GOOD",
  "aggregateType": "RAW"
}
```

### 4.4 报警事件 `AlarmEvent`

```json
{
  "alarmId": "ALM-20260320-0001",
  "tagCode": "crusher.vibration",
  "alarmCode": "CRUSHER_VIB_HIGH",
  "alarmName": "破碎机振动偏高",
  "alarmLevel": "HIGH",
  "alarmStatus": "ACTIVE",
  "currentValue": 12.4,
  "thresholdValue": 10.0,
  "unit": "mm/s",
  "deviceCode": "CRUSHER-01",
  "startTime": "2026-03-20T10:12:10+08:00",
  "ackTime": null,
  "recoverTime": null,
  "ackBy": null,
  "remark": ""
}
```

## 5. 后端接口设计

统一前缀建议：

- `/api/iot`

### 5.1 点位管理接口

#### 5.1.1 查询点位列表

`GET /api/iot/tags`

查询参数：

- `keyword`
- `deviceCode`
- `areaCode`
- `enabled`
- `sourceType`
- `pageNum`
- `pageSize`

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 2,
    "records": [
      {
        "tagCode": "coal.feed.rate",
        "tagName": "原煤给料量",
        "deviceCode": "FEEDER-01",
        "unit": "t/h",
        "dataType": "DOUBLE",
        "enabled": true
      }
    ]
  }
}
```

#### 5.1.2 查询单个点位详情

`GET /api/iot/tags/{tagCode}`

#### 5.1.3 查询设备下点位

`GET /api/iot/devices/{deviceCode}/tags`

### 5.2 实时值接口

#### 5.2.1 批量查询实时值

`GET /api/iot/realtime`

查询参数：

- `tagCodes`，多个点位逗号分隔
- `deviceCode`
- `areaCode`

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "tagCode": "coal.feed.rate",
      "value": 428.6,
      "unit": "t/h",
      "quality": "GOOD",
      "timestamp": "2026-03-20T10:15:21+08:00"
    },
    {
      "tagCode": "crusher.current",
      "value": 83.2,
      "unit": "A",
      "quality": "GOOD",
      "timestamp": "2026-03-20T10:15:21+08:00"
    }
  ]
}
```

#### 5.2.2 实时快照接口

`GET /api/iot/realtime/snapshot`

用途：

- 大屏首页
- 工艺流程图
- 设备总览

建议支持按页面配置一次性返回需要的点位集合。

### 5.3 历史数据接口

#### 5.3.1 查询历史曲线

`GET /api/iot/history`

查询参数：

- `tagCode`
- `startTime`
- `endTime`
- `interval`
- `aggregate`

`aggregate` 建议支持：

- `RAW`
- `AVG`
- `MIN`
- `MAX`
- `SUM`

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "time": "2026-03-20T10:00:00+08:00",
      "value": 420.5,
      "quality": "GOOD",
      "aggregateType": "AVG"
    },
    {
      "time": "2026-03-20T10:05:00+08:00",
      "value": 425.1,
      "quality": "GOOD",
      "aggregateType": "AVG"
    }
  ]
}
```

#### 5.3.2 批量查询多点位趋势

`POST /api/iot/history/batch`

请求示例：

```json
{
  "tagCodes": ["coal.feed.rate", "crusher.current"],
  "startTime": "2026-03-20T00:00:00+08:00",
  "endTime": "2026-03-20T23:59:59+08:00",
  "interval": "5m",
  "aggregate": "AVG"
}
```

### 5.4 报警接口

#### 5.4.1 报警列表

`GET /api/iot/alarms`

查询参数：

- `alarmStatus`
- `alarmLevel`
- `deviceCode`
- `tagCode`
- `startTime`
- `endTime`
- `pageNum`
- `pageSize`

#### 5.4.2 报警确认

`POST /api/iot/alarms/{alarmId}/ack`

请求示例：

```json
{
  "remark": "已通知现场检查"
}
```

#### 5.4.3 报警关闭

`POST /api/iot/alarms/{alarmId}/close`

请求示例：

```json
{
  "remark": "故障已排除"
}
```

### 5.5 点位映射管理接口

#### 5.5.1 查询映射列表

`GET /api/iot/tag-mappings`

#### 5.5.2 新增映射

`POST /api/iot/tag-mappings`

请求示例：

```json
{
  "tagCode": "coal.feed.rate",
  "sourcePath": "Channel1.Device1.Tag001",
  "deviceCode": "FEEDER-01",
  "dataType": "DOUBLE",
  "unit": "t/h",
  "enabled": true
}
```

#### 5.5.3 修改映射

`PUT /api/iot/tag-mappings/{id}`

#### 5.5.4 启用/停用映射

`POST /api/iot/tag-mappings/{id}/enable`

`POST /api/iot/tag-mappings/{id}/disable`

### 5.6 WebSocket 推送接口

建议预留：

- `/ws/iot/realtime`
- `/ws/iot/alarms`

推送内容：

- 实时点位变化
- 报警新增
- 报警恢复
- 设备在线状态变化

前端大屏和监控页优先用 WebSocket，不建议高频轮询。

## 6. 数据库表建议

### 6.1 `iot_source_config`

用于保存采集源配置。

建议字段：

- `id`
- `source_code`
- `source_name`
- `source_type`
- `endpoint`
- `username`
- `password`
- `enabled`
- `remark`
- `created_at`
- `updated_at`

### 6.2 `iot_tag`

用于保存平台标准点位。

建议字段：

- `id`
- `tag_code`
- `tag_name`
- `device_code`
- `device_name`
- `area_code`
- `data_type`
- `unit`
- `scan_rate`
- `deadband`
- `quality_rule`
- `enabled`
- `remark`
- `created_at`
- `updated_at`

### 6.3 `iot_tag_mapping`

用于保存平台点位与 KEPServer 原始点位的映射关系。

建议字段：

- `id`
- `tag_code`
- `source_code`
- `source_path`
- `source_tag_name`
- `source_channel`
- `source_device`
- `transform_rule`
- `enabled`
- `created_at`
- `updated_at`

### 6.4 `iot_realtime_snapshot`

用于保存实时快照。

建议字段：

- `tag_code`
- `value_num`
- `value_text`
- `quality`
- `source_time`
- `receive_time`
- `unit`

说明：

- 实时快照建议放 Redis，关系库只做补充缓存或落表备份。

### 6.5 `iot_history_value`

用于保存历史归档数据。

建议字段：

- `id`
- `tag_code`
- `time_bucket`
- `value_num`
- `quality`
- `aggregate_type`

说明：

- 如果历史量大，建议进入时序库，不建议长期放关系库。

### 6.6 `iot_alarm_event`

用于保存报警事件。

建议字段：

- `id`
- `alarm_id`
- `tag_code`
- `alarm_code`
- `alarm_name`
- `alarm_level`
- `alarm_status`
- `current_value`
- `threshold_value`
- `start_time`
- `ack_time`
- `recover_time`
- `ack_by`
- `close_by`
- `remark`

## 7. 前端调用约定

前端页面不要直接耦合业务接口与采集点位。

建议统一按三类接口使用：

1. 实时页面  
   调 `/api/iot/realtime` 或 `/ws/iot/realtime`

2. 趋势页面  
   调 `/api/iot/history`

3. 报警页面  
   调 `/api/iot/alarms`

示例：

- Dashboard 卡片读实时值
- 设备详情页读实时值 + 历史趋势
- 能耗趋势页读历史聚合
- 报警中心读报警事件

## 8. 需要提前预留的业务字段

以下字段建议现在就进入设备、页面配置或业务配置模型：

- `tagCode`
- `deviceCode`
- `areaCode`
- `sourceType`
- `unit`
- `quality`
- `timestamp`
- `alarmLevel`
- `alarmStatus`

如果现在页面写死为：

- `todayOutput`
- `equipmentStatus`
- `coalLevel`

后面接 KEPServer 时还要再拆一层。更好的做法是页面配置直接绑定 `tagCode`。

## 9. 不建议的做法

- 前端直接访问 KEPServer
- 后端业务服务直接写死 KEP 点名
- 每个业务页面单独定义一套实时接口
- 报警、实时、历史混在一个大接口里
- 中文点位名直接作为系统唯一标识

## 10. 分期实施建议

### 第一期

- 建立 `iot_tag`、`iot_tag_mapping`、`iot_alarm_event`
- 建立 `/api/iot/realtime`、`/api/iot/history`、`/api/iot/alarms`
- 建立 WebSocket 推送通道

### 第二期

- 接入 KEPServer Adapter
- 打通实时缓存与历史归档
- 将大屏、设备监控、报警中心切换到统一接口

### 第三期

- 增加质量码治理
- 增加点位权限和分级授权
- 增加点位变更审计
- 增加报警压缩、联动和消息通知

## 11. 当前项目建议优先落地项

如果按当前代码状态推进，建议优先做下面 4 件事：

1. 增加 `iot` 统一接口前缀和模块目录
2. 先建点位定义表与映射表
3. 前端页面统一改成消费 `realtime/history/alarms`
4. 再接 KEPServer，不要反过来先把 KEP 点位灌到页面里

