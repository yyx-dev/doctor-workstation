// RefundRequestDao.java 退费申请数据访问类
package com.dw.dao.fee;

import com.dw.model.fee.RefundRequest;
import com.dw.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RefundRequestDao {
    public int create(RefundRequest request) throws SQLException {
        String sql = "INSERT INTO refund_request (refund_no, original_invoice_no, patient_id, amount, status, doctor_id, remark) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, request.getRefundNo());
            pstmt.setString(2, request.getOriginalInvoiceNo());
            pstmt.setInt(3, request.getPatientId());
            pstmt.setBigDecimal(4, request.getAmount());
            pstmt.setString(5, "PENDING");
            pstmt.setInt(6, request.getDoctorId());
            pstmt.setString(7, request.getRemark());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public boolean updateStatus(String refundNo, String status) throws SQLException {
        String sql = "UPDATE refund_request SET status = ? WHERE refund_no = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setString(2, refundNo);
            return pstmt.executeUpdate() > 0;
        }
    }

    public BigDecimal getDailyRefundTotal() throws SQLException {
        String sql = "SELECT SUM(amount) FROM refund_request WHERE DATE(create_time) = CURDATE()";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    public List<RefundRequest> search(Integer patientId, String startDate, String endDate) throws SQLException {
        List<RefundRequest> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM refund_request WHERE 1=1");

        if (patientId != null) {
            sql.append(" AND patient_id = ?");
        }
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND create_time >= ?");
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND create_time <= ?");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (patientId != null) {
                pstmt.setInt(paramIndex++, patientId);
            }
            if (startDate != null && !startDate.isEmpty()) {
                pstmt.setString(paramIndex++, startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                pstmt.setString(paramIndex++, endDate);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RefundRequest rr = new RefundRequest();
                    // 填充RefundRequest对象...
                    results.add(rr);
                }
            }
        }
        return results;
    }
}