package com.dw.model.other;

import java.util.Date;

import com.dw.model.user.Doctor;
import com.dw.model.user.Patient;
import lombok.Data;

/**
 * 挂号实体类
 */

@Data
public class Appointment {
    private int id;
    private int patientId;
    private Integer doctorId; // 可以为空，因为可能只挂科室号
    private String department;
    private String status; // 'waiting', 'processing', 'completed'
    private String chiefComplaint; // 主诉
    private Date appointmentTime;

    // 关联对象(非数据库映射)
    private Patient patient;
    private Doctor doctor;

    public String getFormattedTime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(appointmentTime);
    }
}