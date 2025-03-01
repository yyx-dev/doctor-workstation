package com.dw.model.other;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class ConsultationQueue {
    private Integer id;
    private Integer appointmentId;
    private Integer queueNumber;
    private String status;
    private Timestamp startTime;
    private Timestamp endTime;
}