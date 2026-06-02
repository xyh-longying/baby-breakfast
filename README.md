# baby-breakfast
AI 驱动的儿童智能早餐管家

## 项目结构

```text
apps/
  pwa/      家庭用户端 PWA
  admin/    PC 管理后台
packages/
  shared/   前端共享类型与主题令牌
server/     Spring Boot 后端服务
deploy/     本地开发与部署配置
```

## 本地开发

### 1. 启动基础依赖

```bash
docker compose -f deploy/docker-compose.yml up -d
```

### 2. 安装前端依赖

```bash
npm install
```

### 3. 启动 PWA

```bash
npm run dev:pwa
```

### 4. 启动 Admin

```bash
npm run dev:admin
```

### 5. 启动后端

```bash
cd server
mvn spring-boot:run
```

当前环境缺少 `Java` 和 `Maven` 时，可先完成前端开发与接口骨架联调，后续在具备 JDK 21 和 Maven 3.9+ 的环境启动后端。

## 当前演示会话

- 默认用户名：`lingyun`
- 默认密码：`baby-breakfast`
- 当前会话接口：`/api/auth/login`
- 角色切换接口：`/api/auth/switch-role`
