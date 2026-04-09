# allinone 模块修改日志

## 修改日期: 2026-04-09

### 1. 回收站接口路径修复

**问题**: 前端期望的接口路径与后端不一致

**修改文件**:
- `controller/RecycleBinController.java`

**修改内容**:
```java
// 旧接口
@PostMapping("/recover/{gid}/{fullShortUrl}")
@DeleteMapping("/remove/{gid}/{fullShortUrl}")

// 新接口（匹配前端 /recycle-bin/recover/${id}）
@PostMapping("/recover/{id}")
@DeleteMapping("/remove/{id}")
```

### 2. ShortLinkService 接口新增方法

**文件**: `service/ShortLinkService.java`

**新增方法**:
```java
void recoverShortLinkById(Long id, String username);
void removeShortLinkById(Long id, String username);
```

### 3. ShortLinkServiceImpl 实现新增方法

**文件**: `service/impl/ShortLinkServiceImpl.java`

**新增实现**:
- `recoverShortLinkById(Long id, String username)` - 根据ID恢复短链接
- `removeShortLinkById(Long id, String username)` - 根据ID永久删除短链接

**功能说明**:
- 根据短链接ID进行恢复/删除操作
- 同步更新跳转记录表
- 维护内存缓存（GOTO_CACHE, DELETED_CACHE）

### 4. 已完成的功能模块

#### 4.1 登录认证
- 用户登录: `POST /api/login`
- 用户注册: `POST /api/register`
- 用户登出: `POST /api/logout`
- 当前用户: `GET /api/currentUser`

#### 4.2 用户管理
- 用户列表: `GET /api/admin/user/list`
- 更新用户: `PUT /api/admin/user/update`
- 删除用户: `DELETE /api/admin/user/delete/{id}`

#### 4.3 权限管理
- 权限列表: `GET /api/admin/permission/list`
- 用户权限: `GET /api/admin/permission/user/{userId}`
- 分配权限: `POST /api/admin/permission/assign`
- 检查权限: `GET /api/admin/permission/check/{permissionCode}`

#### 4.4 分组管理
- 分组列表: `GET /api/group/list`
- 创建分组: `POST /api/group/save`
- 更新分组: `PUT /api/group/update`
- 删除分组: `DELETE /api/group/delete/{gid}`
- 分组排序: `PUT /api/group/sort`

#### 4.5 短链接管理
- 短链接列表: `GET /api/link/list`
- 创建短链接: `POST /api/link/save`
- 更新短链接: `PUT /api/link/update`
- 删除短链接: `DELETE /api/link/delete/{id}`
- 批量创建: `POST /api/link/batch`

#### 4.6 回收站
- 回收站列表: `GET /api/recycle-bin/list`
- 恢复短链接: `POST /api/recycle-bin/recover/{id}`
- 永久删除: `DELETE /api/recycle-bin/remove/{id}`

#### 4.7 统计功能
- 统计概览: `GET /api/stats/overview`
- 短链接统计: `GET /api/stats/link/{fullShortUrl}`
- 详细统计: `GET /api/stats/detail/{fullShortUrl}`

#### 4.8 导入导出
- 导出CSV: `GET /api/link/export/{gid}`
- 导入CSV: `POST /api/link/import/{gid}`

#### 4.9 AI功能
- AI分析: `GET /api/ai/analyze`
- AI对话: `POST /api/ai/chat`

### 5. 数据库表结构

详见 `sql/init.sql`，包含以下表：
- `tb_user` - 用户表
- `tb_group` - 分组表
- `tb_group_unique` - 分组唯一约束表
- `tb_short_link` - 短链接表
- `tb_short_link_goto` - 跳转记录表
- `tb_permission` - 权限表
- `tb_user_permission` - 用户权限关联表
- `tb_link_access_logs` - 访问日志表
- `tb_link_access_stats` - 访问统计表
- `tb_link_device_stats` - 设备统计表
- `tb_link_browser_stats` - 浏览器统计表
- `tb_link_os_stats` - 操作系统统计表
- `tb_link_locale_stats` - 地区统计表
- `tb_link_network_stats` - 网络运营商统计表
- `tb_link_stats_today` - 今日统计表

### 6. 配置文件

**application.yaml** 主要配置:
- 服务端口: 8001
- 数据库连接: localhost:3306/link
- 短链接默认域名: localhost:8001
- JWT配置: 24小时有效期
- MiniMax AI配置: 用于智能分析功能
