-- =============================================
-- 短链接管理系统 - 权限扩展SQL
-- 数据库：link
-- 在原有表结构基础上添加权限相关表
-- =============================================

USE link;

-- =============================================
-- 1. 给用户表添加 role 字段（所有分片表都需要添加）
-- =============================================
-- 注意：由于原有用户表是分片的（t_user_0 ~ t_user_15），需要在每个分片表添加字段
-- 这里提供 ALTER TABLE 语句，请确保在数据库执行前修改正确的表名

-- 示例：在所有 t_user_X 表中添加 role 字段
-- ALTER TABLE t_user_0 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
-- ALTER TABLE t_user_1 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
-- ... 依此类推，直到 t_user_15

-- =============================================
-- 2. 权限表 (t_permission) - 单表，不分片
-- =============================================
CREATE TABLE IF NOT EXISTS t_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '权限代码',
    description VARCHAR(200) COMMENT '权限描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 插入默认权限
INSERT INTO t_permission (name, code, description) VALUES
('用户管理', 'USER_MANAGE', '管理系统用户'),
('权限管理', 'PERMISSION_MANAGE', '管理用户权限'),
('链接查看', 'LINK_READ', '查看短链接'),
('链接创建', 'LINK_CREATE', '创建短链接'),
('链接修改', 'LINK_UPDATE', '修改短链接'),
('链接删除', 'LINK_DELETE', '删除短链接'),
('图表查看', 'STATS_VIEW', '查看访问统计'),
('AI分析', 'AI_ANALYZE', '使用AI分析功能');

-- =============================================
-- 3. 用户权限关联表 (t_user_permission) - 单表
-- =============================================
CREATE TABLE IF NOT EXISTS t_user_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权限关联表';

-- =============================================
-- 4. 给现有用户设置默认权限
-- =============================================
-- 注意：执行此 SQL 前，请先确保所有 t_user_X 表都已添加 role 字段
-- 并插入管理员账户（如果还没有的话）

-- 插入管理员账户（如果用户表已存在，可跳过此步）
-- INSERT INTO t_user_0 (username, password, real_name, role, del_flag) VALUES ('admin', 'admin123', '系统管理员', 'admin', 0);

-- 给管理员分配所有权限（假设 admin 用户在 t_user_0 表）
-- 请根据实际情况修改表名和用户ID
-- INSERT INTO t_user_permission (user_id, permission_id)
-- SELECT u.id, p.id FROM t_user_0 u, t_permission p WHERE u.username = 'admin';

-- =============================================
-- 完成
-- =============================================
SELECT '权限扩展SQL执行完成！请确保已为所有 t_user_X 表添加 role 字段。' AS status;
