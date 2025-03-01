DROP DATABASE IF EXISTS doctor_workstation;
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
    `cancel_reason` VARCHAR(200),
    `status` ENUM('PENDING','COMPLETED','CANCELED'),
    `chief_complaint` TEXT, -- 主诉
    `appointment_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`) ON DELETE SET NULL
);

-- 入院单表
CREATE TABLE IF NOT EXISTS `admission_form` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `appointment_id` INT NOT NULL,
    `doctor_id` INT NOT NULL,
    `patient_id` INT NOT NULL,
    `department` VARCHAR(50) NOT NULL,
    `diagnosis` TEXT NOT NULL,
    `admission_reason` TEXT NOT NULL,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`appointment_id`) REFERENCES `appointment`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`)
);

-- 病假单表
CREATE TABLE IF NOT EXISTS `sick_leave_form` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `appointment_id` INT NOT NULL,
    `doctor_id` INT NOT NULL,
    `patient_id` INT NOT NULL,
    `days` INT NOT NULL CHECK (days BETWEEN 1 AND 30),
    `medical_advice` TEXT NOT NULL,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`appointment_id`) REFERENCES `appointment`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`)
);

-- 疾病证明表
CREATE TABLE IF NOT EXISTS `medical_certificate` (
     `id` INT PRIMARY KEY AUTO_INCREMENT,
     `appointment_id` INT NOT NULL,
     `doctor_id` INT NOT NULL,
     `patient_id` INT NOT NULL,
     `diagnosis` TEXT NOT NULL,
     `treatment_advice` TEXT NOT NULL,
     `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (`appointment_id`) REFERENCES `appointment`(`id`),
     FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
     FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`)
);

-- 排班计划表
CREATE TABLE schedule_plan (
    id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    department VARCHAR(50) NOT NULL,
    schedule_date DATE NOT NULL,
    shift_type ENUM('早班', '中班', '晚班') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_patients INT DEFAULT 30,
    FOREIGN KEY (doctor_id) REFERENCES doctor(id)
);

-- 号源表
CREATE TABLE registration_number (
    id INT PRIMARY KEY AUTO_INCREMENT,
    schedule_id INT NOT NULL,
    number_time DATETIME NOT NULL,
    status ENUM('未使用', '已预约', '已使用') DEFAULT '未使用',
    patient_id INT,
    FOREIGN KEY (schedule_id) REFERENCES schedule_plan(id),
    FOREIGN KEY (patient_id) REFERENCES patient(id)
);

-- 排班资源表
CREATE TABLE schedule_resource (
    id INT PRIMARY KEY AUTO_INCREMENT,
    resource_name VARCHAR(100) NOT NULL,
    resource_type ENUM('诊室', '设备') NOT NULL,
    schedule_id INT,
    status ENUM('可用', '维护中', '已占用') DEFAULT '可用',
    FOREIGN KEY (schedule_id) REFERENCES schedule_plan(id)
);

-- 病案主表
CREATE TABLE medical_record (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `appointment_id` INT NOT NULL,
    `patient_id` INT NOT NULL,
    `doctor_id` INT NOT NULL,
    `admission_date` DATE NOT NULL,
    `discharge_date` DATE,
    `current_status` VARCHAR(50) COMMENT '当前病情状态',
    `diagnosis` TEXT NOT NULL,
    `prescription` TEXT NOT NULL,
    `status` ENUM('新建', '审核中', '已归档') DEFAULT '新建',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`patient_id`) REFERENCES patient(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES doctor(`id`)
);

-- 创建病历段落表
CREATE TABLE emr_sections (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `record_id` INT NOT NULL,
    `section_type` ENUM('CHIEF_COMPLAINT', 'HISTORY_PRESENT', 'PHYSICAL_EXAM', 'DIAGNOSIS') NOT NULL,
    `content` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`record_id`) REFERENCES medical_record(`id`)
);

-- 病案明细表
CREATE TABLE medical_record_detail (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `record_id` INT NOT NULL,
    `section_type` ENUM('主诉', '现病史', '既往史', '查体', '辅助检查', '诊断', '治疗'),
    `content` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`record_id`) REFERENCES medical_record(`id`)
);

-- 接诊队列表（依赖已有的appointment表）
CREATE TABLE consultation_queue (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `appointment_id` INT NOT NULL UNIQUE,
    `queue_number` INT NOT NULL,
    `status` ENUM('等待中', '接诊中', '已完成') DEFAULT '等待中',
    `start_time` DATETIME,
    `end_time` DATETIME,
    FOREIGN KEY (`appointment_id`) REFERENCES appointment(`id`)
);

-- 诊断书表
CREATE TABLE diagnosis_certificate (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `record_id` INT NOT NULL,
    `doctor_id` INT NOT NULL,
    `diagnosis_details` TEXT NOT NULL,
    `treatment_plan` TEXT NOT NULL,
    `medication` TEXT,
    `follow_up` TEXT,
    `issue_date` DATE NOT NULL,
    `signature` VARCHAR(100),
    FOREIGN KEY (`record_id`) REFERENCES medical_record(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES doctor(`id`)
);

-- 诊断书模板表
CREATE TABLE diagnosis_template (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `template_name` VARCHAR(50) NOT NULL,
    `department` VARCHAR(50) NOT NULL,
    `content` TEXT NOT NULL
);

-- 号源生成表
CREATE TABLE number_source (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `doctor_id` INT NOT NULL,
    `date` DATE NOT NULL,
    `time_slot` VARCHAR(5) NOT NULL COMMENT '时间段，格式如 09:00',
    `status` ENUM('available', 'reserved', 'completed') DEFAULT 'available',
    `patient_id` INT COMMENT '预约患者ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`doctor_id`) REFERENCES doctor(`id`)
);

CREATE TABLE diagnosis_document (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `record_id` INT NOT NULL COMMENT '关联病历ID',
    `patient_id` INT NOT NULL,
    `doctor_id` INT NOT NULL,
    `diagnosis` TEXT NOT NULL COMMENT '诊断结论',
    `treatment` TEXT COMMENT '治疗方案',
    `medication` TEXT COMMENT '处方药物',
    `issue_date` DATETIME NOT NULL,
    FOREIGN KEY (`record_id`) REFERENCES medical_record(`id`),
    FOREIGN KEY (`patient_id`) REFERENCES patient(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES doctor(`id`)
);

CREATE TABLE invoice (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `invoice_no` VARCHAR(20) UNIQUE,
    `patient_id` INT,
    `doctor_id` INT,
    `amount` DECIMAL(10,2),
    `status` ENUM('NORMAL','REISSUED','VOID'),
    `pdf_path` VARCHAR(255),
    `remarks` TEXT,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invoice_number (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `full_number` VARCHAR(20) UNIQUE,
    `is_used` BOOLEAN DEFAULT 0,
    `used_time` TIMESTAMP
);

CREATE TABLE refund_request (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `refund_no` VARCHAR(20) UNIQUE,
    `original_invoice_no` VARCHAR(20),
    `patient_id` INT,
    `amount` DECIMAL(10,2),
    `status` VARCHAR(20) DEFAULT 'PENDING',
    `doctor_id` INT,
    `remark` TEXT,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE daily_settlement (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `settlement_no` VARCHAR(20) UNIQUE,
    `settle_date` TIMESTAMP,
    `total_amount` DECIMAL(10,2),
    `cash_amount` DECIMAL(10,2),
    `refund_amount` DECIMAL(10,2),
    `invoice_count` INT,
    `operator_id` INT,
    `remark` TEXT
);

CREATE TABLE charge_record (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `charge_no` VARCHAR(20) UNIQUE,
    `patient_id` INT,
    `doctor_id` INT,
    `amount` DECIMAL(10,2),
    `charge_type` VARCHAR(20),
    `payment_method` VARCHAR(20),
    `remark` TEXT,
    `charge_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 权限表
CREATE TABLE permission (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `permission_code` VARCHAR(50) UNIQUE NOT NULL,
    `description` VARCHAR(200)
);

-- 用户权限关联表
CREATE TABLE user_permission (
    `user_id` INT REFERENCES user(`id`),
    `permission_id` INT REFERENCES permission(`id`),
    PRIMARY KEY (`user_id`, `permission_id`)
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
    (1, 1, '内科', 'PENDING', '头痛、发热两天'),
    (2, 2, '外科', 'CANCELED', '右腿受伤一周'),
    (3, 3, '儿科', 'COMPLETED', '咳嗽、流涕三天');

-- 入院单测试数据
INSERT INTO admission_form (appointment_id, doctor_id, patient_id, department, diagnosis, admission_reason)
    VALUES (1, 1, 1, '内科', '急性肺炎', '需住院观察治疗');

-- 病假单测试数据
INSERT INTO sick_leave_form (appointment_id, doctor_id, patient_id, days, medical_advice)
    VALUES (2, 2, 2, 5, '建议卧床休息，避免剧烈运动');

-- 疾病证明测试数据
INSERT INTO medical_certificate (appointment_id, doctor_id, patient_id, diagnosis, treatment_advice)
    VALUES (3, 3, 3, '慢性支气管炎', '定期复查，避免接触过敏源');