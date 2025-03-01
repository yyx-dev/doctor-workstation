// AdmissionFormDao.java
package com.dw.dao.form;

import com.dw.model.form.AdmissionForm;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdmissionFormDao {
    public int add(AdmissionForm form) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT INTO admission_form (appointment_id, doctor_id, patient_id, department, diagnosis, admission_reason) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, form.getAppointmentId());
            pstmt.setInt(2, form.getDoctorId());
            pstmt.setInt(3, form.getPatientId());
            pstmt.setString(4, form.getDepartment());
            pstmt.setString(5, form.getDiagnosis());
            pstmt.setString(6, form.getAdmissionReason());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    public List<AdmissionForm> findByDoctorId(int doctorId) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "SELECT * FROM admission_form WHERE doctor_id = ? ORDER BY create_time DESC";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<AdmissionForm> list = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, doctorId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                AdmissionForm form = new AdmissionForm();
                form.setId(rs.getInt("id"));
                form.setAppointmentId(rs.getInt("appointment_id"));
                form.setDoctorId(rs.getInt("doctor_id"));
                form.setPatientId(rs.getInt("patient_id"));
                form.setDepartment(rs.getString("department"));
                form.setDiagnosis(rs.getString("diagnosis"));
                form.setAdmissionReason(rs.getString("admission_reason"));
                form.setCreateTime(rs.getTimestamp("create_time"));
                list.add(form);
            }
            return list;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}