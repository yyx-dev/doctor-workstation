package com.dw.dao.record;

import com.dw.model.record.MedicalRecordDetail;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDetailDao {
    public void addDetail(MedicalRecordDetail detail) throws SQLException {
        String sql = "INSERT INTO medical_record_detail (record_id, section_type, content) "
                + "VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, detail.getRecordId());
            pstmt.setString(2, detail.getSectionType());
            pstmt.setString(3, detail.getContent());

            pstmt.executeUpdate();
        }
    }

    // 添加缺失的方法
    public List<MedicalRecordDetail> findDetailByPatientId(int patientId) throws SQLException {
        String sql = "SELECT mrd.* FROM medical_record_detail mrd "
                + "JOIN medical_record mr ON mrd.record_id = mr.id "
                + "WHERE mr.patient_id = ?";

        List<MedicalRecordDetail> details = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MedicalRecordDetail detail = new MedicalRecordDetail();
                    detail.setId(rs.getInt("id"));
                    detail.setRecordId(rs.getInt("record_id"));
                    detail.setSectionType(rs.getString("section_type"));
                    detail.setContent(rs.getString("content"));
                    detail.setCreatedAt(rs.getTimestamp("created_at"));
                    details.add(detail);
                }
            }
        }
        return details;
    }
}