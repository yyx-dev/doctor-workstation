// DailySettlement.java 日结记录模型类
package com.dw.model.fee;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class DailySettlement {
    private Integer id;
    private String settlementNo;
    private Timestamp settleDate;
    private BigDecimal totalAmount;
    private BigDecimal cashAmount;
    private BigDecimal refundAmount;
    private Integer invoiceCount;
    private Integer operatorId;
    private String remark;
}
