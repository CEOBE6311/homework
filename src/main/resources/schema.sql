-- =============================================================
-- 作业数据库 DateTest（脚本中使用小写 datetest，MySQL Windows 下库名不区分大小写）
-- 包含两张表：student（学生表）、college（学院表）
-- =============================================================

CREATE DATABASE IF NOT EXISTS datetest DEFAULT CHARACTER SET utf8mb4;
USE datetest;

-- 学生表：Id, name, major, age, 入学时间, 是否毕业, 学费
CREATE TABLE IF NOT EXISTS student (
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '学号',
    name        VARCHAR(50)   NOT NULL COMMENT '姓名',
    major       VARCHAR(50)   COMMENT '专业',
    age         INT           COMMENT '年龄',
    enroll_date DATE          COMMENT '入学时间',
    graduated   TINYINT(1)    DEFAULT 0 COMMENT '是否毕业(0-否 1-是)',
    tuition     DECIMAL(10,2) COMMENT '学费'
) COMMENT '学生表';

-- 学院表：id, name, code
CREATE TABLE IF NOT EXISTS college (
    id   INT PRIMARY KEY AUTO_INCREMENT COMMENT '学院ID',
    name VARCHAR(100) NOT NULL COMMENT '学院名称',
    code VARCHAR(20)  COMMENT '学院代码'
) COMMENT '学院表';
