package com.dw.model;

import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;

@Data
public class EMRSection {
    private Integer id;
    private Integer recordId;
    private String sectionType;
    private String content;
    private Timestamp createdAt;

}