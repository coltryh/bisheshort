-- =============================================
-- 短链接管理系统 - 单机版初始化SQL
-- 数据库：link
-- =============================================

USE link;

-- =============================================
-- 1. 用户表 (t_user) - 单表，不分片
-- =============================================
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    mail VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：admin=管理员，user=普通用户',
    deletion_time BIGINT DEFAULT 0 COMMENT '注销时间戳',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标识：0=未删除，1=已删除',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 2. 分组表 (t_group) - 单表，不分片
-- =============================================
CREATE TABLE IF NOT EXISTS t_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分组ID',
    gid VARCHAR(32) NOT NULL UNIQUE COMMENT '分组标识',
    name VARCHAR(100) NOT NULL COMMENT '分组名称',
    username VARCHAR(50) NOT NULL COMMENT '创建分组用户名',
    sort_order INT DEFAULT 0 COMMENT '分组排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标识：0=未删除，1=已删除',
    INDEX idx_username (username),
    INDEX idx_gid (gid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接分组表';

-- =============================================
-- 3. 分组唯一标识表 (t_group_unique) - 用于生成不重复的gid
-- =============================================
CREATE TABLE IF NOT EXISTS t_group_unique (
    gid VARCHAR(32) PRIMARY KEY COMMENT '分组标识'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分组唯一标识表';

-- =============================================
-- 4. 短链接表 (t_link) - 单表，不分片
-- =============================================
CREATE TABLE IF NOT EXISTS t_link (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '短链接ID',
    domain VARCHAR(100) NOT NULL COMMENT '域名',
    short_uri VARCHAR(32) NOT NULL COMMENT '短链接后缀',
    full_short_url VARCHAR(200) NOT NULL COMMENT '完整短链接',
    origin_url TEXT NOT NULL COMMENT '原始链接',
    gid VARCHAR(32) NOT NULL COMMENT '分组标识',
    click_num INT DEFAULT 0 COMMENT '点击量',
    created_type TINYINT DEFAULT 0 COMMENT '创建类型：0=接口创建，1=控制台创建',
    valid_date_type TINYINT DEFAULT 0 COMMENT '有效期类型：0=永久有效，1=自定义',
    valid_date DATETIME COMMENT '有效期',
    `describe` VARCHAR(500) COMMENT '描述',
    favicon VARCHAR(500) COMMENT '网站图标',
    total_pv INT DEFAULT 0 COMMENT '历史PV',
    total_uv INT DEFAULT 0 COMMENT '历史UV',
    total_uip INT DEFAULT 0 COMMENT '历史UIP',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标识：0=未删除，1=已删除',
    del_time BIGINT DEFAULT 0 COMMENT '删除时间戳',
    enable_status TINYINT DEFAULT 0 COMMENT '启用状态：0=启用，1=未启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    INDEX idx_full_short_url (full_short_url),
    INDEX idx_gid (gid),
    INDEX idx_origin_url (origin_url(100)),
    INDEX idx_del_flag_gid (del_flag, gid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接表';

-- =============================================
-- 5. 短链接跳转表 (t_link_goto) - 用于快速查找跳转
-- =============================================
CREATE TABLE IF NOT EXISTS t_link_goto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '跳转ID',
    full_short_url VARCHAR(200) NOT NULL COMMENT '完整短链接',
    gid VARCHAR(32) NOT NULL COMMENT '分组标识',
    INDEX idx_full_short_url (full_short_url),
    INDEX idx_gid (gid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接跳转表';

-- =============================================
-- 6. 权限表 (t_permission) - 单表
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
-- 7. 用户权限关联表 (t_user_permission)
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
-- 8. 短链接访问统计表 (t_link_access_stats)
-- =============================================
CREATE TABLE IF NOT EXISTS t_link_access_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    full_short_url VARCHAR(200) NOT NULL COMMENT '完整短链接',
    date DATE NOT NULL COMMENT '统计日期',
    pv INT DEFAULT 0 COMMENT '页面访问量',
    uv INT DEFAULT 0 COMMENT '独立访客数',
    uip INT DEFAULT 0 COMMENT '独立IP数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_short_url_date (full_short_url, date),
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接访问统计表';

-- =============================================
-- 9. 短链接访问日志表 (t_link_access_logs)
-- =============================================
CREATE TABLE IF NOT EXISTS t_link_access_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    full_short_url VARCHAR(200) NOT NULL COMMENT '完整短链接',
    ip VARCHAR(50) COMMENT 'IP地址',
    country VARCHAR(50) COMMENT '国家',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    device VARCHAR(50) COMMENT '设备',
    os VARCHAR(50) COMMENT '操作系统',
    browser VARCHAR(50) COMMENT '浏览器',
    network VARCHAR(50) COMMENT '网络',
    uv VARCHAR(50) COMMENT '访客标识',
    access_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    INDEX idx_full_short_url (full_short_url),
    INDEX idx_access_time (access_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接访问日志表';

-- =============================================
-- 10. 插入默认管理员账户
-- =============================================
INSERT INTO t_user (username, password, real_name, role) VALUES
('admin', 'admin123', '系统管理员', 'admin');

-- 给管理员分配所有权限
INSERT INTO t_user_permission (user_id, permission_id)
SELECT u.id, p.id FROM t_user u, t_permission p WHERE u.username = 'admin';

-- 给管理员创建一个默认分组
INSERT INTO t_group_unique (gid) VALUES ('default');
INSERT INTO t_group (gid, name, username, sort_order) VALUES ('default', '默认分组', 'admin', 0);

-- =============================================
-- 10. 短链接设备统计表 (t_link_device_stats)
-- =============================================
CREATE TABLE IF NOT EXISTS t_link_device_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    full_short_url VARCHAR(200) NOT NULL COMMENT '完整短链接',
    date DATE NOT NULL COMMENT '统计日期',
    device VARCHAR(50) COMMENT '设备类型',
    cnt INT DEFAULT 0 COMMENT '访问量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_short_url_date_device (full_short_url, date, device),
    INDEX idx_full_short_url (full_short_url),
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接设备统计表';

-- =============================================
-- 11. 短链接浏览器统计表 (t_link_browser_stats)
-- =============================================
CREATE TABLE IF NOT EXISTS t_link_browser_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    full_short_url VARCHAR(200) NOT NULL COMMENT '完整短链接',
    date DATE NOT NULL COMMENT '统计日期',
    browser VARCHAR(50) COMMENT '浏览器',
    cnt INT DEFAULT 0 COMMENT '访问量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_short_url_date_browser (full_short_url, date, browser),
    INDEX idx_full_short_url (full_short_url),
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接浏览器统计表';

-- =============================================
-- 12. 短链接操作系统统计表 (t_link_os_stats)
-- =============================================
CREATE TABLE IF NOT EXISTS t_link_os_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    full_short_url VARCHAR(200) NOT NULL COMMENT '完整短链接',
    date DATE NOT NULL COMMENT '统计日期',
    os VARCHAR(50) COMMENT '操作系统',
    cnt INT DEFAULT 0 COMMENT '访问量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_short_url_date_os (full_short_url, date, os),
    INDEX idx_full_short_url (full_short_url),
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接操作系统统计表';

-- =============================================
-- 13. 短链接地区统计表 (t_link_locale_stats)
-- =============================================
CREATE TABLE IF NOT EXISTS t_link_locale_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    full_short_url VARCHAR(200) NOT NULL COMMENT '完整短链接',
    date DATE NOT NULL COMMENT '统计日期',
    country VARCHAR(50) COMMENT '国家',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    cnt INT DEFAULT 0 COMMENT '访问量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_short_url_date_locale (full_short_url, date, province, city),
    INDEX idx_full_short_url (full_short_url),
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接地区统计表';

-- =============================================
-- 14. 短链接网络类型统计表 (t_link_network_stats)
-- =============================================
CREATE TABLE IF NOT EXISTS t_link_network_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    full_short_url VARCHAR(200) NOT NULL COMMENT '完整短链接',
    date DATE NOT NULL COMMENT '统计日期',
    network VARCHAR(50) COMMENT '网络类型',
    cnt INT DEFAULT 0 COMMENT '访问量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_short_url_date_network (full_short_url, date, network),
    INDEX idx_full_short_url (full_short_url),
    INDEX idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接网络类型统计表';

-- =============================================
-- 15. 插入默认管理员账户
-- =============================================
INSERT INTO t_user (username, password, real_name, role) VALUES
('admin', 'admin123', '系统管理员', 'admin');

-- 给管理员分配所有权限
INSERT INTO t_user_permission (user_id, permission_id)
SELECT u.id, p.id FROM t_user u, t_permission p WHERE u.username = 'admin';

-- 给管理员创建一个默认分组
INSERT INTO t_group_unique (gid) VALUES ('default');
INSERT INTO t_group (gid, name, username, sort_order) VALUES ('default', '默认分组', 'admin', 0);

-- =============================================
-- 完成
-- =============================================
SELECT '单机版初始化SQL执行完成！' AS status;
