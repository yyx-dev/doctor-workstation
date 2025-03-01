// DiagnosisDocumentDAO.java 数据访问类
package com.dw.dao;

import com.dw.model.DiagnosisDocument;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiagnosisDocumentDao {
    public void create(DiagnosisDocument doc) throws SQLException {
        String sql = "INSERT INTO diagnosis_document " +
                "(record_id, patient_id, doctor_id, diagnosis, treatment, medication, issue_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doc.getRecordId());
            stmt.setInt(2, doc.getPatientId());
            stmt.setInt(3, doc.getDoctorId());
            stmt.setString(4, doc.getDiagnosis());
            stmt.setString(5, doc.getTreatment());
            stmt.setString(6, doc.getMedication());
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            stmt.executeUpdate();
        }
    }

    public List<DiagnosisDocument> findByPatient(int patientId) throws SQLException {
        String sql = "SELECT * FROM diagnosis_document WHERE patient_id = ?";
        List<DiagnosisDocument> docs = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DiagnosisDocument doc = new DiagnosisDocument();
                doc.setId(rs.getInt("id"));
                doc.setRecordId(rs.getInt("record_id"));
                doc.setPatientId(rs.getInt("patient_id"));
                doc.setDoctorId(rs.getInt("doctor_id"));
                doc.setDiagnosis(rs.getString("diagnosis"));
                doc.setTreatment(rs.getString("treatment"));
                doc.setMedication(rs.getString("medication"));
                doc.setIssueDate(rs.getTimestamp("issue_date"));
                docs.add(doc);
            }
        }
        return docs;
    }
}