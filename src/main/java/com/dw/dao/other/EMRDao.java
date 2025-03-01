// EMRDao.java
package com.dw.dao.other;

import com.dw.model.other.EMRSection;
import com.dw.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EMRDao {
    public void saveSection(EMRSection section) throws SQLException {
        String sql = "INSERT INTO emr_sections (record_id, section_type, content) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, section.getRecordId());
            pstmt.setString(2, section.getSectionType());
            pstmt.setString(3, section.getContent());
            pstmt.executeUpdate();
        }
    }

    public List<EMRSection> getSections(int recordId) throws SQLException {
        String sql = "SELECT * FROM emr_sections WHERE record_id = ? ORDER BY created_at";
        List<EMRSection> sections = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, recordId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EMRSection section = new EMRSection();
                    section.setId(rs.getInt("id"));
                    section.setRecordId(rs.getInt("record_id"));
                    section.setSectionType(rs.getString("section_type"));
                    section.setContent(rs.getString("content"));
                    section.setCreatedAt(rs.getTimestamp("created_at"));
                    sections.add(section);
                }
            }
        }
        return sections;
    }
}