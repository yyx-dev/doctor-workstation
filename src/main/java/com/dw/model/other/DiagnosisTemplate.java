package com.dw.model.other;
import lombok.Data;

@Data
public class DiagnosisTemplate {
    private Integer id;
    private String templateName;
    private String department;
    private String content;
}