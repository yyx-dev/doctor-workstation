package com.dw.model;

import java.sql.Date;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class MedicalRecord {
    private Integer id;
    private Integer appointmentId;
    private Integer patientId;
    private Integer doctorId;
    private Date admissionDate;
    private Date dischargeDate;
    private String diagnosis;
    private String status;
    private String prescription;
    private Timestamp createdAt;
}