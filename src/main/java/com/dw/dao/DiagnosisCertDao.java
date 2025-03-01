// DiagnosisCertDao.java
package com.dw.dao;

import com.dw.model.DiagnosisCertificate;
import com.dw.util.DBUtil;
import java.sql.*;

public class DiagnosisCertDao {
    public int createCertificate(DiagnosisCertificate cert) throws SQLException {
        String sql = "INSERT INTO diagnosis_certificate (record_id, doctor_id, diagnosis_details, "
                + "treatment_plan, medication, follow_up, issue_date, signature) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, cert.getRecordId());
            pstmt.setInt(2, cert.getDoctorId());
            pstmt.setString(3, cert.getDiagnosisDetails());
            pstmt.setString(4, cert.getTreatmentPlan());
            pstmt.setString(5, cert.getMedication());
            pstmt.setString(6, cert.getFollowUp());
            pstmt.setDate(7, cert.getIssueDate());
            pstmt.setString(8, cert.getSignature());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }
}

