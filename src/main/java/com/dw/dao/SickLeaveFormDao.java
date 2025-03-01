// SickLeaveFormDao.java
package com.dw.dao;

import com.dw.model.SickLeaveForm;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SickLeaveFormDao {
    public int add(SickLeaveForm form) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT INTO sick_leave_form (appointment_id, doctor_id, patient_id, days, medical_advice) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, form.getAppointmentId());
            pstmt.setInt(2, form.getDoctorId());
            pstmt.setInt(3, form.getPatientId());
            pstmt.setInt(4, form.getDays());
            pstmt.setString(5, form.getMedicalAdvice());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    public List<SickLeaveForm> findByDoctorId(int doctorId) throws SQLException {
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM sick_leave_form WHERE doctor_id = ?");
            pstmt.setInt(1, doctorId);
            rs = pstmt.executeQuery();
            List<SickLeaveForm> list = new ArrayList<>();
            while (rs.next()) {
                SickLeaveForm form = new SickLeaveForm();
                form.setId(rs.getInt("id"));
                form.setAppointmentId(rs.getInt("appointment_id"));
                form.setDoctorId(rs.getInt("doctor_id"));
                form.setPatientId(rs.getInt("patient_id"));
                form.setDays(rs.getInt("days"));
                form.setMedicalAdvice(rs.getString("medical_advice"));
                form.setCreateTime(rs.getTimestamp("create_time"));
                list.add(form);
            }
            return list;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}