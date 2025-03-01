// ChargeRecord.java 门诊收费记录模型类
package com.dw.model.fee;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class ChargeRecord {
    private Integer id;
    private String chargeNo;
    private Integer patientId;
    private Integer doctorId;
    private BigDecimal amount;
    private String chargeType; // 门诊/急诊/住院
    private String paymentMethod; // 现金/支付宝/微信
    private Timestamp chargeTime;
    private String remark;
}