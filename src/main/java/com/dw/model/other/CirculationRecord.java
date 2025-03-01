// CirculationRecord.java 流通记录模型
package com.dw.model.other;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;

@Data
public class CirculationRecord {
    private int recordId;
    private String borrower;
    private Date borrowDate;
    private Date returnDate;
    private String status;

    // Getters and Setters
    public String getFormattedBorrowDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(borrowDate);
    }

    public String getFormattedReturnDate() {
        return returnDate != null ?
                new SimpleDateFormat("yyyy-MM-dd").format(returnDate) : "尚未归还";
    }
}