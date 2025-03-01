package com.dw.dao;

import com.dw.model.DiagnosisTemplate;
import com.dw.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiagnosisTemplateDao {
    public List<DiagnosisTemplate> findByDepartment(String department) throws SQLException {
        String sql = "SELECT * FROM diagnosis_template WHERE department = ?";
        List<DiagnosisTemplate> templates = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, department);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DiagnosisTemplate template = new DiagnosisTemplate();
                    template.setId(rs.getInt("id"));
                    template.setTemplateName(rs.getString("template_name"));
                    template.setDepartment(rs.getString("department"));
                    template.setContent(rs.getString("content"));
                    templates.add(template);
                }
            }
        }
        return templates;
    }
}