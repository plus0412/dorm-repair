-- 高校宿舍维修报修管理系统 — 初始数据
-- 密码均为明文存储：123456

USE dorm_repair;

-- 插入初始用户
INSERT INTO sys_user (username, password, real_name, phone, role, gender, status, create_time, update_time)
VALUES
    ('admin',   '123456', '管理员',  '13800000000', 'admin',   '男', 1, NOW(), NOW()),
    ('student', '123456', '张三',    '13800000001', 'student', '男', 1, NOW(), NOW()),
    ('repair',  '123456', '李四',    '13800000002', 'repair',  '男', 1, NOW(), NOW());
