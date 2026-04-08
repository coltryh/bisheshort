-- =============================================
-- 短链接管理系统 - 权限扩展SQL（完整版）
-- 数据库：link
-- =============================================

USE link;

-- =============================================
-- 1. 给所有用户分片表添加 role 字段
-- =============================================
ALTER TABLE t_user_0 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_1 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_2 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_3 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_4 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_5 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_6 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_7 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_8 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_9 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_10 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_11 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_12 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_13 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_14 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';
ALTER TABLE t_user_15 ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户';

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
-- 完成
-- =============================================
SELECT '权限扩展SQL执行完成！' AS status;
