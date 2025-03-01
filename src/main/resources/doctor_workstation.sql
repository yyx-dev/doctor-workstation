-- 创建数据库（删除已存在数据库并创建新库）
DROP DATABASE IF EXISTS doctor_workstation;
CREATE DATABASE IF NOT EXISTS doctor_workstation CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE doctor_workstation;

-- 用户表（存储系统用户信息）
CREATE TABLE IF NOT EXISTS `user` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(50) NOT NULL COMMENT '密码',
    `role` VARCHAR(20) NOT NULL COMMENT '角色类型(doctor/patient)', -- 'doctor' or 'patient'
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='系统用户表';

-- 医生表（存储医生详细信息）
CREATE TABLE IF NOT EXISTS `doctor` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '医生ID',
    `user_id` INT NOT NULL COMMENT '关联用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '医生姓名',
    `gender` VARCHAR(10) COMMENT '性别',
    `department` VARCHAR(50) COMMENT '所属科室',
    `title` VARCHAR(50) COMMENT '职称', -- 职称
    `phone` VARCHAR(20) COMMENT '联系电话',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) COMMENT='医生信息表';

-- 患者表（存储患者详细信息）
CREATE TABLE IF NOT EXISTS `patient` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '患者ID',
    `user_id` INT NOT NULL COMMENT '关联用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '患者姓名',
    `gender` VARCHAR(10) COMMENT '性别',
    `age` INT COMMENT '年龄',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `address` VARCHAR(255) COMMENT '联系地址',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) COMMENT='患者信息表';

-- 挂号表（记录预约挂号信息）
CREATE TABLE IF NOT EXISTS `appointment` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '挂号ID',
    `patient_id` INT NOT NULL COMMENT '患者ID',
    `doctor_id` INT COMMENT '接诊医生ID',
    `department` VARCHAR(50) NOT NULL COMMENT '挂号科室',
    `cancel_reason` VARCHAR(200) COMMENT '取消原因',
    `status` ENUM('PENDING','COMPLETED','CANCELED') COMMENT '状态(待处理/完成/取消)',
    `chief_complaint` TEXT COMMENT '主诉内容', -- 主诉
    `appointment_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`) ON DELETE SET NULL
) COMMENT='挂号记录表';

-- 入院单表（记录患者入院信息）
CREATE TABLE IF NOT EXISTS `admission_form` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '入院单ID',
    `appointment_id` INT NOT NULL COMMENT '关联挂号ID',
    `doctor_id` INT NOT NULL COMMENT '主治医生ID',
    `patient_id` INT NOT NULL COMMENT '患者ID',
    `department` VARCHAR(50) NOT NULL COMMENT '入院科室',
    `diagnosis` TEXT NOT NULL COMMENT '诊断结果',
    `admission_reason` TEXT NOT NULL COMMENT '入院原因',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`appointment_id`) REFERENCES `appointment`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`)
) COMMENT='患者入院单';

-- 病假单表（记录病假信息）
CREATE TABLE IF NOT EXISTS `sick_leave_form` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '病假单ID',
    `appointment_id` INT NOT NULL COMMENT '关联挂号ID',
    `doctor_id` INT NOT NULL COMMENT '开具医生ID',
    `patient_id` INT NOT NULL COMMENT '患者ID',
    `days` INT NOT NULL CHECK (days BETWEEN 1 AND 30) COMMENT '病假天数',
    `medical_advice` TEXT NOT NULL COMMENT '医嘱内容',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`appointment_id`) REFERENCES `appointment`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`)
) COMMENT='病假单记录表';

-- 疾病证明表（记录疾病诊断证明）
CREATE TABLE IF NOT EXISTS `medical_certificate` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '证明ID',
    `appointment_id` INT NOT NULL COMMENT '关联挂号ID',
    `doctor_id` INT NOT NULL COMMENT '开具医生ID',
    `patient_id` INT NOT NULL COMMENT '患者ID',
    `diagnosis` TEXT NOT NULL COMMENT '诊断结果',
    `treatment_advice` TEXT NOT NULL COMMENT '治疗建议',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`appointment_id`) REFERENCES `appointment`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`)
) COMMENT='疾病证明表';

-- 排班计划表（记录医生排班信息）
CREATE TABLE IF NOT EXISTS `schedule_plan` (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '排班ID',
    doctor_id INT NOT NULL COMMENT '医生ID',
    department VARCHAR(50) NOT NULL COMMENT '科室名称',
    schedule_date DATE NOT NULL COMMENT '排班日期',
    shift_type ENUM('早班', '中班', '晚班') NOT NULL COMMENT '班次类型',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    max_patients INT DEFAULT 30 COMMENT '最大接诊人数',
    FOREIGN KEY (doctor_id) REFERENCES doctor(id)
) COMMENT='医生排班计划表';

-- 号源表（记录挂号号源状态）
CREATE TABLE IF NOT EXISTS `registration_number` (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '号源ID',
    schedule_id INT NOT NULL COMMENT '关联排班ID',
    number_time DATETIME NOT NULL COMMENT '号源时间',
    status ENUM('未使用', '已预约', '已使用') DEFAULT '未使用' COMMENT '号源状态',
    patient_id INT COMMENT '预约患者ID',
    FOREIGN KEY (schedule_id) REFERENCES schedule_plan(id),
    FOREIGN KEY (patient_id) REFERENCES patient(id)
) COMMENT='挂号号源表';

-- 排班资源表（记录诊室和设备资源）
CREATE TABLE IF NOT EXISTS `schedule_resource` (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '资源ID',
    resource_name VARCHAR(100) NOT NULL COMMENT '资源名称',
    resource_type ENUM('诊室', '设备') NOT NULL COMMENT '资源类型',
    schedule_id INT COMMENT '关联排班ID',
    status ENUM('可用', '维护中', '已占用') DEFAULT '可用' COMMENT '资源状态',
    FOREIGN KEY (schedule_id) REFERENCES schedule_plan(id)
) COMMENT='排班资源表';

-- 病案主表（存储病案主要信息）
CREATE TABLE IF NOT EXISTS `medical_record` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '病案ID',
    `appointment_id` INT NOT NULL COMMENT '关联挂号ID',
    `patient_id` INT NOT NULL COMMENT '患者ID',
    `doctor_id` INT NOT NULL COMMENT '主治医生ID',
    `admission_date` DATE NOT NULL COMMENT '入院日期',
    `discharge_date` DATE COMMENT '出院日期',
    `current_status` VARCHAR(50) COMMENT '当前病情状态',
    `diagnosis` TEXT NOT NULL COMMENT '诊断结果',
    `prescription` TEXT NOT NULL COMMENT '处方内容',
    `status` ENUM('新建', '审核中', '已归档') DEFAULT '新建' COMMENT '病案状态',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`patient_id`) REFERENCES patient(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES doctor(`id`)
) COMMENT='电子病案主表';

-- 病历段落表（存储病案结构化内容）
CREATE TABLE IF NOT EXISTS `emr_sections` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '段落ID',
    `record_id` INT NOT NULL COMMENT '关联病案ID',
    `section_type` ENUM('CHIEF_COMPLAINT', 'HISTORY_PRESENT', 'PHYSICAL_EXAM', 'DIAGNOSIS') NOT NULL COMMENT '段落类型',
    `content` TEXT NOT NULL COMMENT '段落内容',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`record_id`) REFERENCES medical_record(`id`)
) COMMENT='病案段落明细表';

-- 病案明细表（存储病案详细信息）
CREATE TABLE IF NOT EXISTS `medical_record_detail` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    `record_id` INT NOT NULL COMMENT '关联病案ID',
    `section_type` ENUM('主诉', '现病史', '既往史', '查体', '辅助检查', '诊断', '治疗') COMMENT '章节类型',
    `content` TEXT NOT NULL COMMENT '详细内容',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`record_id`) REFERENCES medical_record(`id`)
) COMMENT='病案详细记录表';

-- 接诊队列表（管理患者接诊队列）
CREATE TABLE IF NOT EXISTS `consultation_queue` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '队列ID',
    `appointment_id` INT NOT NULL UNIQUE COMMENT '关联挂号ID',
    `queue_number` INT NOT NULL COMMENT '排队序号',
    `status` ENUM('等待中', '接诊中', '已完成') DEFAULT '等待中' COMMENT '接诊状态',
    `start_time` DATETIME COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    FOREIGN KEY (`appointment_id`) REFERENCES appointment(`id`)
) COMMENT='接诊队列管理表';

-- 诊断书表（存储诊断证明信息）
CREATE TABLE IF NOT EXISTS `diagnosis_certificate` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '诊断书ID',
    `record_id` INT NOT NULL COMMENT '关联病案ID',
    `doctor_id` INT NOT NULL COMMENT '开具医生ID',
    `diagnosis_details` TEXT NOT NULL COMMENT '诊断详情',
    `treatment_plan` TEXT NOT NULL COMMENT '治疗方案',
    `medication` TEXT COMMENT '用药说明',
    `follow_up` TEXT COMMENT '随访建议',
    `issue_date` DATE NOT NULL COMMENT '开具日期',
    `signature` VARCHAR(100) COMMENT '医生签名',
    FOREIGN KEY (`record_id`) REFERENCES medical_record(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES doctor(`id`)
) COMMENT='诊断证明书表';

-- 诊断模板表（存储诊断模板信息）
CREATE TABLE IF NOT EXISTS `diagnosis_template` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    `template_name` VARCHAR(50) NOT NULL COMMENT '模板名称',
    `department` VARCHAR(50) NOT NULL COMMENT '适用科室',
    `content` TEXT NOT NULL COMMENT '模板内容'
) COMMENT='诊断模板表';

-- 号源生成表（管理医生号源信息）
CREATE TABLE IF NOT EXISTS `number_source` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '号源ID',
    `doctor_id` INT NOT NULL COMMENT '医生ID',
    `date` DATE NOT NULL COMMENT '号源日期',
    `time_slot` VARCHAR(5) NOT NULL COMMENT '时间段(如09:00)', -- 时间段，格式如 09:00
    `status` ENUM('available', 'reserved', 'completed') DEFAULT 'available' COMMENT '号源状态',
    `patient_id` INT COMMENT '预约患者ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`doctor_id`) REFERENCES doctor(`id`)
) COMMENT='医生号源生成表';

-- 诊断文档表（存储诊断文档信息）
CREATE TABLE IF NOT EXISTS `diagnosis_document` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '文档ID',
    `record_id` INT NOT NULL COMMENT '关联病案ID', -- 关联病历ID
    `patient_id` INT NOT NULL COMMENT '患者ID',
    `doctor_id` INT NOT NULL COMMENT '医生ID',
    `diagnosis` TEXT NOT NULL COMMENT '诊断结论', -- 诊断结论
    `treatment` TEXT COMMENT '治疗方案', -- 治疗方案
    `medication` TEXT COMMENT '处方药物', -- 处方药物
    `issue_date` DATETIME NOT NULL COMMENT '开具时间',
    FOREIGN KEY (`record_id`) REFERENCES medical_record(`id`),
    FOREIGN KEY (`patient_id`) REFERENCES patient(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES doctor(`id`)
) COMMENT='诊断文档表';

-- 发票表（存储发票信息）
CREATE TABLE IF NOT EXISTS `invoice` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '发票ID',
    `invoice_no` VARCHAR(20) UNIQUE COMMENT '发票号码',
    `patient_id` INT COMMENT '患者ID',
    `doctor_id` INT COMMENT '医生ID',
    `amount` DECIMAL(10,2) COMMENT '金额',
    `status` ENUM('NORMAL','REISSUED','VOID') COMMENT '发票状态',
    `pdf_path` VARCHAR(255) COMMENT 'PDF存储路径',
    `remarks` TEXT COMMENT '备注',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='发票信息表';

-- 发票号段表（管理发票号码）
CREATE TABLE IF NOT EXISTS `invoice_number` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '号段ID',
    `full_number` VARCHAR(20) UNIQUE COMMENT '完整发票号',
    `is_used` BOOLEAN DEFAULT 0 COMMENT '是否使用',
    `used_time` TIMESTAMP COMMENT '使用时间'
) COMMENT='发票号段管理表';

-- 退款请求表（记录退款申请信息）
CREATE TABLE IF NOT EXISTS `refund_request` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '退款ID',
    `refund_no` VARCHAR(20) UNIQUE COMMENT '退款单号',
    `original_invoice_no` VARCHAR(20) COMMENT '原发票号',
    `patient_id` INT COMMENT '患者ID',
    `amount` DECIMAL(10,2) COMMENT '退款金额',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '处理状态',
    `doctor_id` INT COMMENT '处理医生ID',
    `remark` TEXT COMMENT '备注',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='退款申请表';

-- 日结单表（记录每日结算信息）
CREATE TABLE IF NOT EXISTS `daily_settlement` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '日结单ID',
    `settlement_no` VARCHAR(20) UNIQUE COMMENT '结算单号',
    `settle_date` TIMESTAMP COMMENT '结算日期',
    `total_amount` DECIMAL(10,2) COMMENT '总金额',
    `cash_amount` DECIMAL(10,2) COMMENT '现金金额',
    `refund_amount` DECIMAL(10,2) COMMENT '退款金额',
    `invoice_count` INT COMMENT '发票数量',
    `operator_id` INT COMMENT '操作员ID',
    `remark` TEXT COMMENT '备注'
) COMMENT='日结单报表';

-- 收费记录表（记录收费明细）
CREATE TABLE IF NOT EXISTS `charge_record` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `charge_no` VARCHAR(20) UNIQUE COMMENT '收费单号',
    `patient_id` INT COMMENT '患者ID',
    `doctor_id` INT COMMENT '医生ID',
    `amount` DECIMAL(10,2) COMMENT '金额',
    `charge_type` VARCHAR(20) COMMENT '收费类型',
    `payment_method` VARCHAR(20) COMMENT '支付方式',
    `remark` TEXT COMMENT '备注',
    `charge_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收费时间'
) COMMENT='收费记录表';

-- 权限表（存储系统权限项）
CREATE TABLE IF NOT EXISTS `permission` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    `permission_code` VARCHAR(50) UNIQUE NOT NULL COMMENT '权限编码',
    `description` VARCHAR(200) COMMENT '权限描述'
) COMMENT='系统权限表';

-- 用户权限关联表（用户与权限关系）
CREATE TABLE IF NOT EXISTS `user_permission` (
    `user_id` INT COMMENT '用户ID',
    `permission_id` INT COMMENT '权限ID',
    PRIMARY KEY (`user_id`, `permission_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`permission_id`) REFERENCES `permission`(`id`)
) COMMENT='用户权限关联表';


-- 测试数据插入（按表结构顺序插入避免外键约束）
-- 用户表测试数据
INSERT INTO `user` (username, password, role) VALUES
    ('doctor1', '1234', 'doctor'),
    ('doctor2', '1234', 'doctor'),
    ('patient1', '1234', 'patient'),
    ('patient2', '1234', 'patient'),
    ('patient3', '1234', 'patient');

-- 医生表测试数据
INSERT INTO `doctor` (user_id, name, gender, department, title, phone) VALUES
    (1, '张华', '男', '心血管内科', '主任医师', '13800001111'),
    (2, '李娜', '女', '呼吸内科', '副主任医师', '13800002222');

-- 患者表测试数据
INSERT INTO `patient` (user_id, name, gender, age, phone, address) VALUES
    (3, '王强', '男', 45, '13912345678', '北京市朝阳区'),
    (4, '陈红', '女', 32, '13987654321', '上海市浦东新区'),
    (5, '李明', '男', 28, '13511223344', '广州市天河区');

-- 挂号表测试数据
INSERT INTO `appointment` (patient_id, doctor_id, department, status, chief_complaint, appointment_time) VALUES
    (1, 1, '心血管内科', 'COMPLETED', '胸闷气短一周', '2023-10-01 09:00:00'),
    (2, 2, '呼吸内科', 'PENDING', '持续咳嗽两周', '2023-10-02 10:30:00'),
    (3, 1, '心血管内科', 'CANCELED', '血压异常', '2023-10-03 14:00:00');

-- 入院单测试数据
INSERT INTO `admission_form` (appointment_id, doctor_id, patient_id, department, diagnosis, admission_reason) VALUES
    (1, 1, 1, '心血管内科', '冠心病', '需要冠状动脉造影检查'),
    (2, 2, 2, '呼吸内科', '肺炎', '需住院抗生素治疗');

-- 病假单测试数据
INSERT INTO `sick_leave_form` (appointment_id, doctor_id, patient_id, days, medical_advice) VALUES
    (1, 1, 1, 7, '避免剧烈运动，定期复查'),
    (2, 2, 2, 10, '保持呼吸道通畅，按时服药');

-- 疾病证明测试数据
INSERT INTO `medical_certificate` (appointment_id, doctor_id, patient_id, diagnosis, treatment_advice) VALUES
    (1, 1, 1, '稳定性心绞痛', '长期服用阿司匹林'),
    (2, 2, 2, '细菌性肺炎', '静脉注射头孢曲松钠');

-- 排班计划测试数据
INSERT INTO `schedule_plan` (doctor_id, department, schedule_date, shift_type, start_time, end_time) VALUES
    (1, '心血管内科', '2023-10-10', '早班', '08:00', '12:00'),
    (1, '心血管内科', '2023-10-11', '中班', '13:00', '17:00'),
    (2, '呼吸内科', '2023-10-10', '晚班', '18:00', '22:00');

-- 号源表测试数据
INSERT INTO `registration_number` (schedule_id, number_time, status, patient_id) VALUES
    (1, '2023-10-10 08:30:00', '已使用', 1),
    (1, '2023-10-10 09:00:00', '已预约', 2),
    (3, '2023-10-10 18:30:00', '未使用', NULL);

-- 排班资源测试数据
INSERT INTO `schedule_resource` (resource_name, resource_type, schedule_id, status) VALUES
    ('内科诊室1', '诊室', 1, '已占用'),
    ('心电图机', '设备', 1, '可用'),
    ('呼吸科诊室2', '诊室', 3, '可用');

-- 病案主表测试数据
INSERT INTO `medical_record` (appointment_id, patient_id, doctor_id, admission_date, diagnosis, prescription, status) VALUES
    (1, 1, 1, '2023-10-01', '冠心病', '阿司匹林 100mg qd', '已归档'),
    (2, 2, 2, '2023-10-02', '肺炎', '头孢曲松钠 2g iv q12h', '审核中');

-- 病历段落测试数据
INSERT INTO `emr_sections` (record_id, section_type, content) VALUES
    (1, 'CHIEF_COMPLAINT', '患者主诉胸闷气短一周'),
    (1, 'DIAGNOSIS', '冠状动脉粥样硬化性心脏病');

-- 病案明细测试数据
INSERT INTO `medical_record_detail` (record_id, section_type, content) VALUES
    (1, '主诉', '活动后胸痛持续3-5分钟'),
    (1, '诊断', '稳定性心绞痛');

-- 接诊队列测试数据
INSERT INTO `consultation_queue` (appointment_id, queue_number, status) VALUES
    (2, 1, '等待中'),
    (3, 2, '已完成');

-- 诊断书测试数据
INSERT INTO `diagnosis_certificate` (record_id, doctor_id, diagnosis_details, treatment_plan, issue_date) VALUES
    (1, 1, '冠状动脉供血不足', '长期抗血小板治疗', '2023-10-05');

-- 诊断模板测试数据
INSERT INTO `diagnosis_template` (template_name, department, content) VALUES
    ('心绞痛模板', '心血管内科', '典型胸痛症状，ECG显示ST段压低'),
    ('肺炎模板', '呼吸内科', '肺部湿啰音，胸片显示浸润影');

-- 号源生成测试数据
INSERT INTO `number_source` (doctor_id, date, time_slot, status) VALUES
    (1, '2023-10-15', '09:00', 'available'),
    (1, '2023-10-15', '09:30', 'reserved'),
    (2, '2023-10-15', '10:00', 'completed');

-- 诊断文档测试数据
INSERT INTO `diagnosis_document` (record_id, patient_id, doctor_id, diagnosis, issue_date) VALUES
    (1, 1, 1, '冠状动脉粥样硬化性心脏病', '2023-10-05 10:00:00');

-- 发票测试数据
INSERT INTO `invoice` (invoice_no, patient_id, doctor_id, amount, status) VALUES
    ('INV202310001', 1, 1, 500.00, 'NORMAL'),
    ('INV202310002', 2, 2, 300.00, 'REISSUED');

-- 发票号段测试数据
INSERT INTO `invoice_number` (full_number, is_used) VALUES
    ('INV2023100001', 1),
    ('INV2023100002', 0);

-- 退款请求测试数据
INSERT INTO `refund_request` (refund_no, original_invoice_no, amount, status) VALUES
    ('REF20231001', 'INV202310001', 500.00, 'APPROVED');

-- 日结单测试数据
INSERT INTO `daily_settlement` (settlement_no, total_amount, cash_amount) VALUES
    ('SET20231001', 8000.00, 7500.00);

-- 收费记录测试数据
INSERT INTO `charge_record` (charge_no, patient_id, amount, charge_type) VALUES
    ('CHG202310001', 1, 200.00, '诊查费'),
    ('CHG202310002', 2, 150.00, '药费');

-- 权限测试数据
INSERT INTO `permission` (permission_code, description) VALUES
    ('PATIENT_READ', '查看患者信息权限'),
    ('RECORD_WRITE', '病历编辑权限');

-- 用户权限测试数据
INSERT INTO `user_permission` (user_id, permission_id) VALUES
    (1, 1),
    (1, 2);