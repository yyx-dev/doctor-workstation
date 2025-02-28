package com.dw.dao;

import com.dw.model.DiseaseCertificate;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiseaseCertificateDao {
    public int add(DiseaseCertificate certificate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int certificateId = -1;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO disease_certificate(patient_id, issue_date, diagnosis, doctor_name) VALUES(?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, certificate.getPatientId());
            stmt.setDate(2, new Date(certificate.getIssueDate().getTime()));
            stmt.setString(3, certificate.getDiagnosis());
            stmt.setString(4, certificate.getDoctorName());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    certificateId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return certificateId;
    }

    public DiseaseCertificate findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DiseaseCertificate certificate = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM disease_certificate WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                certificate = new DiseaseCertificate();
                certificate.setId(rs.getInt("id"));
                certificate.setPatientId(rs.getInt("patient_id"));
                certificate.setIssueDate(rs.getDate("issue_date"));
                certificate.setDiagnosis(rs.getString("diagnosis"));
                certificate.setDoctorName(rs.getString("doctor_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return certificate;
    }

    public List<DiseaseCertificate> findByPatientId(int patientId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<DiseaseCertificate> certificates = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM disease_certificate WHERE patient_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                DiseaseCertificate certificate = new DiseaseCertificate();
                certificate.setId(rs.getInt("id"));
                certificate.setPatientId(rs.getInt("patient_id"));
                certificate.setIssueDate(rs.getDate("issue_date"));
                certificate.setDiagnosis(rs.getString("diagnosis"));
                certificate.setDoctorName(rs.getString("doctor_name"));
                certificates.add(certificate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return certificates;
    }
}