# KEPServer 真实数据对接清单

本文档用于现场把 `KEPServerEX` 和当前管控平台对接到真实数据时，提前准备和逐项确认所需内容。

当前项目已经预留了 KEPServer 配置与 IoT 接口，核心入口包括：

- 服务端配置：`PLATFORM_IOT_PROVIDER`、`PLATFORM_IOT_KEPSERVER_ENABLED`、`PLATFORM_IOT_KEPSERVER_ENDPOINT`
- 统一接口前缀：`/api/iot`
- 核心能力：点位列表、实时值、历史值、报警、点位映射

## 1. 对接目标先确认

上线前先明确本次接入范围，不然点位会一直加，边界收不住。

需要确认：

- 本次接入哪些系统：PLC、仪表、传感器、变频器、电表等
- 本次接入哪些车间/区域：如给料、破碎、储装运
- 本次接入哪些页面/业务：大屏、设备监控、报警中心、趋势分析、报表
- 本次只接实时数据，还是同时接历史和报警
- 历史数据由 KEPServer/外部系统提供，还是由平台落库归档

## 2. 现场网络与连通性清单

这部分不确认，平台再完整也连不上真实数据。

- KEPServer 部署机器 IP
- KEPServer OPC UA 端口
- 平台服务所在机器到 KEPServer 机器是否网络可达
- 是否有防火墙、白名单、网闸、VPN 限制
- Docker 部署时容器是否能访问现场主机 IP
- 如果使用 `host.docker.internal` 不通，是否改为实际局域网 IP
- 现场时间是否统一，时区是否一致

建议现场提供：

| 项目 | 示例 | 是否已确认 |
| --- | --- | --- |
| KEPServer 主机 IP | `192.168.1.20` |  |
| OPC UA 端口 | `49320` |  |
| 平台部署主机 IP | `192.168.1.50` |  |
| 网络连通性 | `平台可访问 KEPServer` |  |
| 防火墙策略 | `已放通 49320` |  |

## 3. KEPServer 连接参数清单

当前项目已支持以下配置，现场至少要把这些参数补齐：

| 配置项 | 说明 | 示例 |
| --- | --- | --- |
| `PLATFORM_IOT_PROVIDER` | IoT 数据源类型 | `kepserver` |
| `PLATFORM_IOT_KEPSERVER_ENABLED` | 是否启用 KEPServer | `true` |
| `PLATFORM_IOT_KEPSERVER_ENDPOINT` | OPC UA 地址 | `opc.tcp://192.168.1.20:49320` |
| `PLATFORM_IOT_KEPSERVER_USERNAME` | 用户名，没有可留空 | `admin` |
| `PLATFORM_IOT_KEPSERVER_PASSWORD` | 密码，没有可留空 | `******` |
| `PLATFORM_IOT_KEPSERVER_SOURCE_TYPE` | 数据源标识 | `KEPSERVER` |
| `PLATFORM_IOT_KEPSERVER_CHANNEL_PREFIX` | 通道前缀 | `Channel1` |
| `PLATFORM_IOT_KEPSERVER_TIMEOUT_MS` | 连接超时毫秒数 | `3000` |

推荐最终环境变量：

```env
PLATFORM_IOT_PROVIDER=kepserver
PLATFORM_IOT_FALLBACK_TO_MOCK=false
PLATFORM_IOT_KEPSERVER_ENABLED=true
PLATFORM_IOT_KEPSERVER_ENDPOINT=opc.tcp://192.168.1.20:49320
PLATFORM_IOT_KEPSERVER_USERNAME=
PLATFORM_IOT_KEPSERVER_PASSWORD=
PLATFORM_IOT_KEPSERVER_SOURCE_TYPE=KEPSERVER
PLATFORM_IOT_KEPSERVER_CHANNEL_PREFIX=Channel1
PLATFORM_IOT_KEPSERVER_TIMEOUT_MS=3000
```

## 4. 点位清单

这是最核心的交付物。没有点位清单，平台无法完成标准化映射。

每个真实点位至少要提供以下字段：

| 字段 | 是否必填 | 说明 |
| --- | --- | --- |
| `tagCode` | 是 | 平台统一点位编码，建议英文、唯一 |
| `tagName` | 是 | 点位中文名称 |
| `sourcePath` | 是 | KEPServer 原始路径，如 `Channel1.Device1.Tag001` |
| `deviceCode` | 是 | 平台设备编码 |
| `deviceName` | 是 | 设备名称 |
| `areaCode` | 是 | 区域编码 |
| `dataType` | 是 | `DOUBLE`、`INT`、`BOOLEAN`、`STRING` 等 |
| `unit` | 建议 | 单位，如 `t/h`、`A`、`%` |
| `scanRate` | 建议 | 采样周期，毫秒 |
| `deadband` | 建议 | 死区过滤值 |
| `qualityRule` | 建议 | 质量规则，如 `GOOD_ONLY` |
| `enabled` | 是 | 是否启用 |
| `remark` | 否 | 备注 |

建议现场按下面格式给表：

| tagCode | tagName | sourcePath | deviceCode | deviceName | areaCode | dataType | unit | scanRate | deadband | qualityRule | enabled | remark |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `coal.feed.rate` | 原煤给料量 | `Channel1.Feeder01.Tag001` | `FEEDER-01` | 1号给料机 | `feed-workshop` | `DOUBLE` | `t/h` | `1000` | `0.1` | `GOOD_ONLY` | `true` |  |

## 5. 设备与业务映射清单

现场给了原始点位还不够，还要明确它在平台里归属到哪个设备、哪个页面、哪个业务指标。

需要补齐：

- 设备编码和 KEPServer 设备名的对应关系
- 区域编码和现场区域名称的对应关系
- 页面卡片/工艺图/趋势图实际用哪些 `tagCode`
- 哪些点位是核心生产指标，哪些只是辅助监测
- 哪些点位参与报警判断

建议补一张业务映射表：

| businessCode | businessName | tagCode | 页面/模块 | 用途 |
| --- | --- | --- | --- | --- |
| `dashboard.feedRate` | 大屏-给料量 | `coal.feed.rate` | 首页大屏 | 实时展示 |
| `equipment.crusherCurrent` | 设备-破碎机电流 | `crusher.current` | 设备监控 | 实时监测 |

## 6. 报警对接清单

如果本次需要接报警，必须明确报警来源到底在哪一层。

二选一先定清楚：

- 方案 A：报警由现场系统或 KEPServer 侧产出，平台只接收结果
- 方案 B：平台根据实时点位和阈值自行判断报警

报警至少要明确：

- `alarmCode`
- `alarmName`
- `tagCode`
- `alarmLevel`
- 阈值
- 报警触发条件
- 报警恢复条件
- 是否需要确认
- 是否需要关闭
- 报警责任岗位/通知对象

建议整理为：

| alarmCode | alarmName | tagCode | alarmLevel | threshold | condition | autoRecover | needAck | remark |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `CRUSHER_CURRENT_HIGH` | 破碎机电流高 | `crusher.current` | `HIGH` | `78` | `>` | `true` | `true` |  |

## 7. 历史数据清单

当前平台接口已经预留历史查询能力，但上线前要先定历史数据来源。

需要确认：

- 历史数据从哪里来
- 历史数据保存多久
- 查询粒度要求：原始值、1 分钟、5 分钟、1 小时
- 聚合方式要求：`RAW`、`AVG`、`MIN`、`MAX`、`SUM`
- 是否需要导出报表

如果历史暂时不做，至少要明确：

- 首期只接实时
- 历史接口先不作为验收范围

## 8. 平台接口验收清单

现场点位准备好后，至少要跑通下面这些接口：

- `GET /api/iot/tags`
- `GET /api/iot/tags/{tagCode}`
- `GET /api/iot/realtime`
- `GET /api/iot/realtime/snapshot`
- `GET /api/iot/history`
- `GET /api/iot/alarms`
- `GET /api/iot/tag-mappings`

验收时重点检查：

- 能否查到真实点位列表
- 实时值是否持续变化
- `quality` 是否正常
- `timestamp` 是否为现场真实时间
- 单位和数据类型是否正确
- 页面是否已改为按 `tagCode` 取数，而不是写死业务字段

## 9. 上线前配置清单

- 修改 `.env` 或部署环境变量，启用 `kepserver`
- 关闭 `mock` 回退：`PLATFORM_IOT_FALLBACK_TO_MOCK=false`
- 确认 `docker-compose.yml` 中服务端环境变量已生效
- 确认数据库已保存 `iot_tag` 和 `iot_tag_mapping`
- 确认现场最少 3 到 5 个真实点位可用于联调
- 确认至少 1 个报警点位可用于验证

## 10. 建议现场一次性交付的资料

你可以直接向现场、自控方或实施方要下面这些文件：

- KEPServer 连接信息
- 点位总表 Excel
- 设备编码对照表
- 区域编码对照表
- 报警规则表
- 首期接入范围说明
- 网络拓扑或访问说明

## 11. 最终交付标准

满足以下条件，基本可以认为“KEPServer 已接入真实数据”：

- 平台已连接真实 KEPServer，而不是 mock 数据
- 点位清单已标准化，`tagCode` 唯一且可管理
- 至少一批核心实时点位可稳定读取
- 页面已显示真实现场值
- 报警链路已验证或已明确延期范围
- 历史能力已落地或已明确首期不做

## 12. 你现在可以直接让对方填写的最小清单

如果你想先快速推进，先让对方只交这 8 项：

1. KEPServer IP 和端口
2. 用户名和密码
3. 通道前缀 `Channel`
4. 首批要接的设备清单
5. 首批要接的点位清单
6. 每个点位的单位、类型、采样周期
7. 哪些点位要报警
8. 哪些页面要显示这些点位

---

如果需要，我下一步可以继续帮你补一份可直接发给现场的 Excel 列头模板，或者把这份清单再拆成“现场方提供”和“平台方配置”两个版本。
