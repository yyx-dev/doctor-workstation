package com.dw.model;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MedicalRecordDetail {
    private Integer id;
    private Integer recordId;
    private String sectionType;
    private String content;
    private Timestamp createdAt;

    // getters/setters...
}