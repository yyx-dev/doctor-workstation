// ChargeRecordDao.java 门诊收费数据访问类
package com.dw.dao.fee;


import com.dw.model.fee.ChargeRecord;
import com.dw.util.DBUtil;
import java.sql.*;

public class ChargeRecordDao {
    public int createCharge(ChargeRecord record) throws SQLException {
        String sql = "INSERT INTO charge_record (charge_no, patient_id, doctor_id, amount, "
                + "charge_type, payment_method, remark) VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, record.getChargeNo());
            pstmt.setInt(2, record.getPatientId());
            pstmt.setInt(3, record.getDoctorId());
            pstmt.setBigDecimal(4, record.getAmount());
            pstmt.setString(5, record.getChargeType());
            pstmt.setString(6, record.getPaymentMethod());
            pstmt.setString(7, record.getRemark());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public ChargeRecord getByChargeNo(String chargeNo) throws SQLException {
        String sql = "SELECT * FROM charge_record WHERE charge_no = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, chargeNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ChargeRecord cr = new ChargeRecord();
                    cr.setId(rs.getInt("id"));
                    cr.setChargeNo(rs.getString("charge_no"));
                    cr.setPatientId(rs.getInt("patient_id"));
                    cr.setDoctorId(rs.getInt("doctor_id"));
                    cr.setAmount(rs.getBigDecimal("amount"));
                    cr.setChargeType(rs.getString("charge_type"));
                    cr.setPaymentMethod(rs.getString("payment_method"));
                    cr.setChargeTime(rs.getTimestamp("charge_time"));
                    cr.setRemark(rs.getString("remark"));
                    return cr;
                }
            }
        }
        return null;
    }

    public int createWithConnection(Connection conn, ChargeRecord record) throws SQLException {
        String sql = "INSERT INTO charge_record (charge_no, patient_id, doctor_id, amount, "
                + "charge_type, payment_method, remark) VALUES (?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 参数设置保持不变...
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }
}