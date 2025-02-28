package com.dw.dao;

import com.dw.model.SickLeaveForm;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SickLeaveFormDao {
    public int add(SickLeaveForm form) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int formId = -1;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO sick_leave_form(patient_id, start_date, end_date, reason) VALUES(?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, form.getPatientId());
            stmt.setDate(2, new Date(form.getStartDate().getTime()));
            stmt.setDate(3, new Date(form.getEndDate().getTime()));
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

    public SickLeaveForm findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        SickLeaveForm form = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM sick_leave_form WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                form = new SickLeaveForm();
                form.setId(rs.getInt("id"));
                form.setPatientId(rs.getInt("patient_id"));
                form.setStartDate(rs.getDate("start_date"));
                form.setEndDate(rs.getDate("end_date"));
                form.setReason(rs.getString("reason"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return form;
    }

    public List<SickLeaveForm> findByPatientId(int patientId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<SickLeaveForm> forms = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM sick_leave_form WHERE patient_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                SickLeaveForm form = new SickLeaveForm();
                form.setId(rs.getInt("id"));
                form.setPatientId(rs.getInt("patient_id"));
                form.setStartDate(rs.getDate("start_date"));
                form.setEndDate(rs.getDate("end_date"));
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