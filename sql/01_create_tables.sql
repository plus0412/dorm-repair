-- 高校宿舍维修报修管理系统 — 建表 SQL
-- 执行顺序：直接运行本文件即可创建全部 10 张表
-- 字符集：utf8mb4，存储引擎：InnoDB

CREATE DATABASE IF NOT EXISTS dorm_repair
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_general_ci;

USE dorm_repair;

-- 1. 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username      VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录用户名',
    password      VARCHAR(100) NOT NULL COMMENT 'BCrypt 密文',
    real_name     VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    phone         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    role          VARCHAR(20)  NOT NULL COMMENT '角色：admin/student/repair',
    gender        VARCHAR(10)  DEFAULT NULL COMMENT '性别：男/女',
    dorm_building_id BIGINT    DEFAULT NULL COMMENT '所属宿舍楼ID',
    dorm_room_id  BIGINT       DEFAULT NULL COMMENT '所属宿舍房间ID',
    status        INT           DEFAULT 1 COMMENT '状态：1=正常 0=禁用',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 宿舍楼表
DROP TABLE IF EXISTS dorm_building;
CREATE TABLE dorm_building (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name          VARCHAR(50) NOT NULL COMMENT '楼栋名称，如：1号宿舍楼',
    description   VARCHAR(200) DEFAULT NULL COMMENT '备注说明',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍楼表';

-- 3. 宿舍房间表
DROP TABLE IF EXISTS dorm_room;
CREATE TABLE dorm_room (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    building_id   BIGINT      NOT NULL COMMENT '所属宿舍楼ID',
    room_no       VARCHAR(20) NOT NULL COMMENT '房间号，如：101',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_building_room (building_id, room_no),
    CONSTRAINT fk_room_building FOREIGN KEY (building_id) REFERENCES dorm_building (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍房间表';

-- 4. 报修类型表
DROP TABLE IF EXISTS repair_type;
CREATE TABLE repair_type (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    type_name     VARCHAR(50) NOT NULL COMMENT '类型名称，如：水电维修',
    description   VARCHAR(200) DEFAULT NULL COMMENT '类型说明',
    status        INT          DEFAULT 1 COMMENT '状态：1=启用 0=禁用',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修类型表';

-- 5. 报修工单表
DROP TABLE IF EXISTS repair_order;
CREATE TABLE repair_order (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_no         VARCHAR(50)  NOT NULL UNIQUE COMMENT '工单编号：BX+时间+3位随机数',
    student_id       BIGINT       NOT NULL COMMENT '报修学生ID',
    building_id     BIGINT       DEFAULT NULL COMMENT '宿舍楼ID',
    room_id         BIGINT       DEFAULT NULL COMMENT '宿舍房间ID',
    repair_type_id   BIGINT       DEFAULT NULL COMMENT '报修类型ID',
    title           VARCHAR(100) NOT NULL COMMENT '报修标题',
    description     VARCHAR(500) DEFAULT NULL COMMENT '报修详细描述',
    status          INT           DEFAULT 0 COMMENT '状态：0待审核 1驳回 2待派单 3待维修 4维修中 5已完成 6已评价 7已取消',
    image_urls      VARCHAR(1000) DEFAULT NULL COMMENT '报修图片URL，多个逗号分隔',
    reject_reason   VARCHAR(200)  DEFAULT NULL COMMENT '驳回原因（status=1时）',
    repair_user_id  BIGINT        DEFAULT NULL COMMENT '分配的维修人员ID',
    repair_result   VARCHAR(500)  DEFAULT NULL COMMENT '维修结果描述',
    repair_images   VARCHAR(1000) DEFAULT NULL COMMENT '维修后图片URL',
    start_time      DATETIME       DEFAULT NULL COMMENT '开始维修时间',
    finish_time     DATETIME       DEFAULT NULL COMMENT '完成维修时间',
    evaluate_id     BIGINT         DEFAULT NULL COMMENT '评价记录ID',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_order_student FOREIGN KEY (student_id) REFERENCES sys_user (id),
    CONSTRAINT fk_order_building FOREIGN KEY (building_id) REFERENCES dorm_building (id),
    CONSTRAINT fk_order_room FOREIGN KEY (room_id) REFERENCES dorm_room (id),
    CONSTRAINT fk_order_repair_type FOREIGN KEY (repair_type_id) REFERENCES repair_type (id),
    CONSTRAINT fk_order_repair_user FOREIGN KEY (repair_user_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修工单表';

-- 6. 报修图片表
DROP TABLE IF EXISTS repair_order_image;
CREATE TABLE repair_order_image (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_id      BIGINT      NOT NULL COMMENT '工单ID',
    image_url     VARCHAR(200) NOT NULL COMMENT '图片URL',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_image_order FOREIGN KEY (order_id) REFERENCES repair_order (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修图片表';

-- 7. 维修记录表
DROP TABLE IF EXISTS repair_record;
CREATE TABLE repair_record (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_id      BIGINT      NOT NULL COMMENT '工单ID',
    operation     VARCHAR(50) NOT NULL COMMENT '操作描述，如：提交报修、审核通过、开始维修',
    operator_id   BIGINT      NOT NULL COMMENT '操作人ID',
    operator_role VARCHAR(20)  DEFAULT NULL COMMENT '操作人角色',
    remark        VARCHAR(200) DEFAULT NULL COMMENT '备注',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_record_order FOREIGN KEY (order_id) REFERENCES repair_order (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修记录表';

-- 8. 维修评价表
DROP TABLE IF EXISTS repair_evaluation;
CREATE TABLE repair_evaluation (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_id        BIGINT      NOT NULL UNIQUE COMMENT '工单ID',
    student_id      BIGINT      NOT NULL COMMENT '评价学生ID',
    repair_user_id  BIGINT      NOT NULL COMMENT '维修人员ID',
    rating          INT         NOT NULL COMMENT '评分：1-5',
    comment         VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_eval_order FOREIGN KEY (order_id) REFERENCES repair_order (id),
    CONSTRAINT fk_eval_student FOREIGN KEY (student_id) REFERENCES sys_user (id),
    CONSTRAINT fk_eval_repair FOREIGN KEY (repair_user_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修评价表';

-- 9. 公告表
DROP TABLE IF EXISTS notice;
CREATE TABLE notice (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title         VARCHAR(100) NOT NULL COMMENT '公告标题',
    content       VARCHAR(2000) DEFAULT NULL COMMENT '公告内容',
    status        INT           DEFAULT 1 COMMENT '状态：1=显示 0=隐藏',
    publisher_id  BIGINT       DEFAULT NULL COMMENT '发布人ID',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_notice_publisher FOREIGN KEY (publisher_id) REFERENCES sys_user (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 10. 操作日志表
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id       BIGINT       DEFAULT NULL COMMENT '操作用户ID',
    username      VARCHAR(50)  DEFAULT NULL COMMENT '用户名',
    operation     VARCHAR(100) DEFAULT NULL COMMENT '操作描述',
    method        VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    params        VARCHAR(2000) DEFAULT NULL COMMENT '请求参数',
    ip            VARCHAR(50)  DEFAULT NULL COMMENT 'IP地址',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
