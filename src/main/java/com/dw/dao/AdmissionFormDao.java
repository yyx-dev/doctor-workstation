package com.dw.dao;

import com.dw.model.AdmissionForm;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdmissionFormDao {
    public int add(AdmissionForm form) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int formId = -1;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO admission_form(patient_id, admission_date, department, reason) VALUES(?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, form.getPatientId());
            stmt.setDate(2, new Date(form.getAdmissionDate().getTime()));
            stmt.setString(3, form.getDepartment());
            stmt.setString(4, form.getReason());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    formId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return formId;
    }

    public AdmissionForm findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        AdmissionForm form = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM admission_form WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                form = new AdmissionForm();
                form.setId(rs.getInt("id"));
                form.setPatientId(rs.getInt("patient_id"));
                form.setAdmissionDate(rs.getDate("admission_date"));
                form.setDepartment(rs.getString("department"));
                form.setReason(rs.getString("reason"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return form;
    }

    public List<AdmissionForm> findByPatientId(int patientId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<AdmissionForm> forms = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM admission_form WHERE patient_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                AdmissionForm form = new AdmissionForm();
                form.setId(rs.getInt("id"));
                form.setPatientId(rs.getInt("patient_id"));
                form.setAdmissionDate(rs.getDate("admission_date"));
                form.setDepartment(rs.getString("department"));
                form.setReason(rs.getString("reason"));
                forms.add(form);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return forms;
    }
}