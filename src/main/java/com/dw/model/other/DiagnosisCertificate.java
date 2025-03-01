package com.dw.model.other;

import java.sql.Date;
import lombok.Data;

@Data
public class DiagnosisCertificate {
    private Integer id;
    private Integer recordId;
    private Integer doctorId;
    private String diagnosisDetails;
    private String treatmentPlan;
    private String medication;
    private String followUp;
    private Date issueDate;
    private String signature;
}