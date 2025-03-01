package com.dw.model;

import java.sql.Date;
import java.sql.Time;
import lombok.Data;

@Data
public class SchedulePlan {
    private Integer id;
    private Integer doctorId;
    private String department;
    private Date scheduleDate;
    private String shiftType;
    private Time startTime;
    private Time endTime;
    private Integer maxPatients;
}