# P0 工程骨架说明

## 当前已完成内容

- Monorepo 工作区初始化
- PWA 用户端基础页面
- Admin 管理端基础页面
- 前端共享主题包
- Spring Boot 后端骨架
- MySQL 与 Redis 的本地 `docker compose` 配置
- 认证与家庭上下文接口骨架
- PWA 登录态与角色切换接入

## 目录说明

### `apps/pwa`

家庭用户端，当前已包含：

- 家长/儿童模式切换
- 今日早餐占位页
- 一周计划占位页
- `Vite` 代理到 `/api`

### `apps/admin`

后台管理端，当前已包含：

- 管理台基础布局
- 模块入口占位区
- `Element Plus` 接入

### `packages/shared`

前端共享包，当前包含：

- 角色类型定义
- 家长/儿童主题令牌

### `server`

后端服务骨架，当前已包含：

- Spring Boot 启动类
- `/api/health` 健康检查接口
- `/api/auth/login`、`/api/auth/session`、`/api/auth/switch-role` 接口骨架
- 基础安全配置
- MySQL 与 Redis 配置模板

## 当前限制

当前运行环境缺少 `Java` 和 `Maven`，所以后端只完成了代码骨架，未执行启动验证。

## 下一步建议

按 P0 继续推进以下模块：

1. 认证与家庭上下文
2. 家庭成员管理
3. 菜品中心基础 CRUD
4. 库存与购物清单基础能力
5. 节假日与周计划主链路
