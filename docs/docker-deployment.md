# Docker 部署与交付说明

## 1. 目标

当前这套部署方案的目标不是只满足本机开发，而是为了后续可以整套打包，到别的现场或服务器直接安装运行。

推荐边界：
- `web`：Docker
- `server`：Docker
- `postgres`：Docker
- `KEPServerEX`：独立部署在 Windows 主机或工控机，不放进当前 Compose

## 2. 已提供的交付文件

根目录：
- [docker-compose.yml](/d:/金海泽地/代码/管控平台/docker-compose.yml)
- [.env.docker.example](/d:/金海泽地/代码/管控平台/.env.docker.example)

前后端镜像构建文件：
- [server/Dockerfile](/d:/金海泽地/代码/管控平台/server/Dockerfile)
- [web/Dockerfile](/d:/金海泽地/代码/管控平台/web/Dockerfile)
- [web/nginx.conf](/d:/金海泽地/代码/管控平台/web/nginx.conf)

离线交付脚本：
- [export-images.ps1](/d:/金海泽地/代码/管控平台/scripts/export-images.ps1)
- [import-images.ps1](/d:/金海泽地/代码/管控平台/scripts/import-images.ps1)
- [package-delivery.ps1](/d:/金海泽地/代码/管控平台/scripts/package-delivery.ps1)

交付目录占位：
- [deployment/.gitkeep](/d:/金海泽地/代码/管控平台/deployment/.gitkeep)

## 3. 两种部署模式

### 3.1 联网构建部署

适合开发环境或现场能联网拉镜像的情况。

1. 复制环境变量模板

```powershell
Copy-Item .env.docker.example .env
```

2. 修改 `.env`

至少改这些：
- `POSTGRES_PASSWORD`
- `SPRING_SECURITY_USER_PASSWORD`

如果要接 KEPServer，再改这些：
- `PLATFORM_IOT_PROVIDER=kepserver`
- `PLATFORM_IOT_KEPSERVER_ENABLED=true`
- `PLATFORM_IOT_KEPSERVER_ENDPOINT`

3. 启动

```powershell
docker compose up -d --build
```

### 3.2 离线镜像交付部署

适合客户现场、内网、不能联网或不希望现场重新构建的情况。  
这是更推荐的交付方式。

#### 在交付前机器上

1. 先构建镜像

```powershell
docker compose build
```

2. 导出镜像

```powershell
.\scripts\export-images.ps1
```

3. 准备交付目录

```powershell
.\scripts\package-delivery.ps1
```

4. 把这些内容一起打包带走：
- `deployment\images\*.tar`
- `deployment\package\*`

#### 在目标机器上

1. 导入镜像

```powershell
.\scripts\import-images.ps1
```

2. 复制环境变量模板

```powershell
Copy-Item .env.docker.example .env
```

3. 修改 `.env`

4. 启动

```powershell
docker compose up -d
```

注意：离线导入镜像后，不需要再 `--build`。

## 4. 默认端口

- 前端：`80`
- 后端：`8080`
- PostgreSQL：`5432`

默认访问：
- 前端：`http://localhost`
- 后端：`http://localhost:8080/api`
- IOT provider 状态：`http://localhost:8080/api/iot/provider/status`

## 5. KEPServer 连接说明

默认模板里：

```env
PLATFORM_IOT_KEPSERVER_ENDPOINT=opc.tcp://host.docker.internal:49320
```

这个地址适合：
- `KEPServerEX` 安装在宿主机 Windows
- Docker 容器通过宿主机地址访问

如果 Docker 在独立 Linux 服务器上，而 KEPServer 在另一台 Windows 机器，就把地址改成对方内网 IP，例如：

```env
PLATFORM_IOT_KEPSERVER_ENDPOINT=opc.tcp://192.168.1.20:49320
```

## 6. 当前默认行为

当前 Compose 默认：
- 数据库使用 PostgreSQL 16
- 后端通过环境变量连接 PostgreSQL
- IOT provider 默认使用 `mock`
- 即使配置成 `kepserver`，如果不可用，也会按配置回退到 `mock`

## 7. 交付时必须确认的事项

1. 修改默认密码，不要继续使用示例值。
2. 确认目标机器已安装 Docker 和 Docker Compose。
3. 确认目标机器与 KEPServer 的网络是通的。
4. 确认 `.env` 中端口没有和现场已有服务冲突。
5. 确认数据库卷或外挂目录有备份策略。

## 8. 当前已验证内容

已经验证：
- [docker-compose.yml](/d:/金海泽地/代码/管控平台/docker-compose.yml) 的 `docker compose config` 通过
- `web` 前端构建通过

还没有在当前环境验证：
- `server` Maven 真实编译
- 整套容器真正启动联调

原因是当前环境没有完整 Java/Maven 容器外编译链，也没有实际跑现场数据库和 KEPServer。
