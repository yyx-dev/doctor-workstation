package com.dw.model;

import java.util.Date;

/**
 * 挂号实体类
 */
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

    // 默认构造函数
    public Appointment() {
    }

    // 带参数构造函数
    public Appointment(int patientId, Integer doctorId, String department, String status, String chiefComplaint) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.department = department;
        this.status = status;
        this.chiefComplaint = chiefComplaint;
    }

    // getter和setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public Date getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Date appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", department='" + department + '\'' +
                ", status='" + status + '\'' +
                ", chiefComplaint='" + chiefComplaint + '\'' +
                ", appointmentTime=" + appointmentTime +
                '}';
    }
}