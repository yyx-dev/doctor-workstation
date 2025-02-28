CREATE DATABASE IF NOT EXISTS doctor_workstation CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE doctor_workstation;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(50) NOT NULL,
    `role` VARCHAR(20) NOT NULL, -- 'doctor' or 'patient'
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建医生表
CREATE TABLE IF NOT EXISTS `doctor` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `gender` VARCHAR(10),
    `department` VARCHAR(50),
    `title` VARCHAR(50), -- 职称
    `phone` VARCHAR(20),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- 创建患者表
CREATE TABLE IF NOT EXISTS `patient` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `gender` VARCHAR(10),
    `age` INT,
    `phone` VARCHAR(20),
    `address` VARCHAR(255),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- 创建挂号表
CREATE TABLE IF NOT EXISTS `appointment` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `patient_id` INT NOT NULL,
    `doctor_id` INT,
    `department` VARCHAR(50) NOT NULL,
    `status` VARCHAR(20) NOT NULL, -- 'waiting', 'processing', 'completed'
    `chief_complaint` TEXT, -- 主诉
    `appointment_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`) ON DELETE SET NULL
);

-- 创建病历表
CREATE TABLE IF NOT EXISTS `medical_record` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `appointment_id` INT NOT NULL,
    `doctor_id` INT NOT NULL,
    `patient_id` INT NOT NULL,
    `diagnosis` TEXT NOT NULL, -- 诊断结果
    `prescription` TEXT, -- 药方
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`appointment_id`) REFERENCES `appointment`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`) ON DELETE CASCADE
);

-- 插入示范数据 - 用户表
INSERT INTO `user` (`username`, `password`, `role`) VALUES
    ('doctor1', 'password', 'doctor'),
    ('doctor2', 'password', 'doctor'),
    ('doctor3', 'password', 'doctor'),
    ('patient1', 'password', 'patient'),
    ('patient2', 'password', 'patient'),
    ('patient3', 'password', 'patient');

-- 插入示范数据 - 医生表
INSERT INTO `doctor` (`user_id`, `name`, `gender`, `department`, `title`, `phone`) VALUES
    (1, '张医生', '男', '内科', '主任医师', '13800138001'),
    (2, '李医生', '女', '外科', '副主任医师', '13800138002'),
    (3, '王医生', '男', '儿科', '主治医师', '13800138003');

-- 插入示范数据 - 患者表
INSERT INTO `patient` (`user_id`, `name`, `gender`, `age`, `phone`, `address`) VALUES
    (4, '刘患者', '男', 45, '13900139001', '北京市海淀区'),
    (5, '陈患者', '女', 28, '13900139002', '上海市浦东新区'),
    (6, '赵患者', '男', 35, '13900139003', '广州市天河区');

-- 插入示范数据 - 挂号表
INSERT INTO `appointment` (`patient_id`, `doctor_id`, `department`, `status`, `chief_complaint`) VALUES
    (1, 1, '内科', 'waiting', '头痛、发热两天'),
    (2, 2, '外科', 'processing', '右腿受伤一周'),
    (3, 3, '儿科', 'waiting', '咳嗽、流涕三天');

-- 插入示范数据 - 病历表
INSERT INTO `medical_record` (`appointment_id`, `doctor_id`, `patient_id`, `diagnosis`, `prescription`) VALUES
    (2, 2, 2, '右腿软组织挫伤', '活血化瘀膏 - 每日外敷两次');