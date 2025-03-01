package com.dw.model;
import lombok.Data;

@Data
public class DiagnosisTemplate {
    private Integer id;
    private String templateName;
    private String department;
    private String content;
}