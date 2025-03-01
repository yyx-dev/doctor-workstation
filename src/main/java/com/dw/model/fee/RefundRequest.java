// RefundRequest.java 退费申请模型类
package com.dw.model.fee;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class RefundRequest {
    private Integer id;
    private String refundNo;
    private String originalInvoiceNo;
    private Integer patientId;
    private BigDecimal amount;
    private String status; // PENDING, APPROVED, REJECTED
    private Integer doctorId;
    private String remark;
    private Timestamp createTime;
}