// MedicalCertificateDao.java
package com.dw.dao;

import com.dw.model.MedicalCertificate;
import com.dw.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalCertificateDao {
    public int add(MedicalCertificate certificate) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT INTO medical_certificate (appointment_id, doctor_id, patient_id, diagnosis, treatment_advice) VALUES (?,?,?,?,?)";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, certificate.getAppointmentId());
            pstmt.setInt(2, certificate.getDoctorId());
            pstmt.setInt(3, certificate.getPatientId());
            pstmt.setString(4, certificate.getDiagnosis());
            pstmt.setString(5, certificate.getTreatmentAdvice());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    public List<MedicalCertificate> findByDoctorId(int doctorId) throws SQLException {
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<MedicalCertificate> list = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement("SELECT * FROM medical_certificate WHERE doctor_id=?");
            pstmt.setInt(1, doctorId);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                MedicalCertificate cert = new MedicalCertificate();
                cert.setId(rs.getInt("id"));
                cert.setAppointmentId(rs.getInt("appointment_id"));
                cert.setDoctorId(rs.getInt("doctor_id"));
                cert.setPatientId(rs.getInt("patient_id"));
                cert.setDiagnosis(rs.getString("diagnosis"));
                cert.setTreatmentAdvice(rs.getString("treatment_advice"));
                cert.setCreateTime(rs.getTimestamp("create_time"));
                list.add(cert);
            }
            return list;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}