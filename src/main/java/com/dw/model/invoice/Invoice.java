// Invoice.java 发票模型类
package com.dw.model.invoice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class Invoice {
    private Integer id;
    private String invoiceNo;
    private Integer patientId;
    private Integer doctorId;
    private BigDecimal amount;
    private String status; // NORMAL-正常, REISSUED-补打, VOID-作废
    private Timestamp createTime;
    private String pdfPath;
}