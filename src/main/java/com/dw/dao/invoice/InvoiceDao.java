// InvoiceDao.java 发票数据访问类
package com.dw.dao.invoice;

import com.dw.model.invoice.Invoice;
import com.dw.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDao {
    public int createInvoice(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoice (invoice_no, patient_id, doctor_id, amount, status, pdf_path) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, invoice.getInvoiceNo());
            pstmt.setInt(2, invoice.getPatientId());
            pstmt.setInt(3, invoice.getDoctorId());
            pstmt.setBigDecimal(4, invoice.getAmount());
            pstmt.setString(5, "NORMAL");
            pstmt.setString(6, invoice.getPdfPath());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public Invoice getById(int id) throws SQLException {
        String sql = "SELECT * FROM invoice WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setId(rs.getInt("id"));
                    inv.setInvoiceNo(rs.getString("invoice_no"));
                    inv.setPatientId(rs.getInt("patient_id"));
                    inv.setDoctorId(rs.getInt("doctor_id"));
                    inv.setAmount(rs.getBigDecimal("amount"));
                    inv.setStatus(rs.getString("status"));
                    inv.setCreateTime(rs.getTimestamp("create_time"));
                    inv.setPdfPath(rs.getString("pdf_path"));
                    return inv;
                }
            }
        }
        return null;
    }

    public List<Invoice> searchInvoices(String invoiceNo, Integer patientId, String startDate, String endDate) throws SQLException {
        List<Invoice> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM invoice WHERE 1=1");

        if (invoiceNo != null) {
            sql.append(" AND invoice_no = ?");
        }
        if (patientId != null) {
            sql.append(" AND patient_id = ?");
        }
        if (startDate != null) {
            sql.append(" AND create_time >= ?");
        }
        if (endDate != null) {
            sql.append(" AND create_time <= ?");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (invoiceNo != null) {
                pstmt.setString(paramIndex++, invoiceNo);
            }
            if (patientId != null) {
                pstmt.setInt(paramIndex++, patientId);
            }
            if (startDate != null) {
                pstmt.setString(paramIndex++, startDate);
            }
            if (endDate != null) {
                pstmt.setString(paramIndex++, endDate);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setId(rs.getInt("id"));
                    inv.setInvoiceNo(rs.getString("invoice_no"));
                    inv.setPatientId(rs.getInt("patient_id"));
                    inv.setDoctorId(rs.getInt("doctor_id"));
                    inv.setAmount(rs.getBigDecimal("amount"));
                    inv.setStatus(rs.getString("status"));
                    inv.setCreateTime(rs.getTimestamp("create_time"));
                    inv.setPdfPath(rs.getString("pdf_path"));
                    results.add(inv);
                }
            }
        }
        return results;
    }

    public boolean reissueInvoice(int originalId, Invoice newInvoice) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 标记原发票状态
            String updateSql = "UPDATE invoice SET status = 'REISSUED' WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, originalId);
                pstmt.executeUpdate();
            }

            // 插入新发票
            String insertSql = "INSERT INTO invoice (invoice_no, patient_id, doctor_id, amount, status, pdf_path) "
                    + "VALUES (?,?,?,?,?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, newInvoice.getInvoiceNo());
                pstmt.setInt(2, newInvoice.getPatientId());
                pstmt.setInt(3, newInvoice.getDoctorId());
                pstmt.setBigDecimal(4, newInvoice.getAmount());
                pstmt.setString(5, newInvoice.getStatus());
                pstmt.setString(6, newInvoice.getPdfPath());
                pstmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            throw ex;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public Invoice getByInvoiceNo(String invoiceNo) throws SQLException {
        String sql = "SELECT * FROM invoice WHERE invoice_no = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, invoiceNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setId(rs.getInt("id"));
                    inv.setInvoiceNo(rs.getString("invoice_no"));
                    inv.setPatientId(rs.getInt("patient_id"));
                    inv.setDoctorId(rs.getInt("doctor_id"));
                    inv.setAmount(rs.getBigDecimal("amount"));
                    inv.setStatus(rs.getString("status"));
                    inv.setCreateTime(rs.getTimestamp("create_time"));
                    inv.setPdfPath(rs.getString("pdf_path"));
                    return inv;
                }
            }
        }
        return null;
    }

    public boolean voidInvoice(int invoiceId, int operatorId, String reason) throws SQLException {
        String sql = "UPDATE invoice SET status = 'VOID', pdf_path = CONCAT(pdf_path, '_VOID'), "
                + "remarks = CONCAT(IFNULL(remarks,''), ' | 作废操作：', ?) WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "操作人：" + operatorId + "，原因：" + reason);
            pstmt.setInt(2, invoiceId);

            return pstmt.executeUpdate() > 0;
        }
    }

    public BigDecimal getDailyTotal() throws SQLException {
        String sql = "SELECT SUM(amount) FROM invoice WHERE DATE(create_time) = CURDATE()";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    public int getDailyCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM invoice WHERE DATE(create_time) = CURDATE()";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Invoice> search(String invoiceNo, Integer patientId, String startDate, String endDate) throws SQLException {
        List<Invoice> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM invoice WHERE 1=1");

        if (invoiceNo != null && !invoiceNo.isEmpty()) {
            sql.append(" AND invoice_no = ?");
        }
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
            if (invoiceNo != null && !invoiceNo.isEmpty()) {
                pstmt.setString(paramIndex++, invoiceNo);
            }
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
                    Invoice inv = new Invoice();
                    // 填充Invoice对象...
                    results.add(inv);
                }
            }
        }
        return results;
    }

    public Invoice getByChargeNo(String chargeNo) throws SQLException {
        String sql = "SELECT * FROM invoice WHERE remark LIKE ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%ChargeNo:" + chargeNo + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? extractInvoiceFromResultSet(rs) : null;
            }
        }
    }

    public int createWithConnection(Connection conn, Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoice (invoice_no, patient_id, doctor_id, amount, status, pdf_path) "
                + "VALUES (?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 参数设置保持不变...
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    private Invoice extractInvoiceFromResultSet(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setId(rs.getInt("id"));
        inv.setInvoiceNo(rs.getString("invoice_no"));
        inv.setPatientId(rs.getInt("patient_id"));
        inv.setDoctorId(rs.getInt("doctor_id"));
        inv.setAmount(rs.getBigDecimal("amount"));
        inv.setStatus(rs.getString("status"));
        inv.setCreateTime(rs.getTimestamp("create_time"));
        inv.setPdfPath(rs.getString("pdf_path"));
        return inv;
    }
}