// DiagnosisDocument.java 诊断书模型类
package com.dw.model;

import java.util.Date;
import lombok.Data;

@Data
public class DiagnosisDocument {
    private int id;
    private int recordId;       // 关联的病历ID
    private int patientId;
    private int doctorId;
    private String diagnosis;  // 诊断结论
    private String treatment;  // 治疗方案
    private String medication; // 处方药物
    private Date issueDate;

    public String getFormattedDate() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(issueDate);
    }
}