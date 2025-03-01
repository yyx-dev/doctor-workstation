// SchedulePlanDao.java
package com.dw.dao;

import com.dw.model.SchedulePlan;
import com.dw.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SchedulePlanDao {
    public int create(SchedulePlan plan) throws SQLException {
        String sql = "INSERT INTO schedule_plan (doctor_id, department, schedule_date, shift_type, start_time, end_time, max_patients) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, plan.getDoctorId());
            pstmt.setString(2, plan.getDepartment());
            pstmt.setDate(3, plan.getScheduleDate());
            pstmt.setString(4, plan.getShiftType());
            pstmt.setTime(5, plan.getStartTime());
            pstmt.setTime(6, plan.getEndTime());
            pstmt.setInt(7, plan.getMaxPatients());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public List<SchedulePlan> findByDoctor(int doctorId) throws SQLException {
        String sql = "SELECT * FROM schedule_plan WHERE doctor_id = ?";
        List<SchedulePlan> plans = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SchedulePlan plan = new SchedulePlan();
                    plan.setId(rs.getInt("id"));
                    plan.setDoctorId(rs.getInt("doctor_id"));
                    plan.setDepartment(rs.getString("department"));
                    plan.setScheduleDate(rs.getDate("schedule_date"));
                    plan.setShiftType(rs.getString("shift_type"));
                    plan.setStartTime(rs.getTime("start_time"));
                    plan.setEndTime(rs.getTime("end_time"));
                    plan.setMaxPatients(rs.getInt("max_patients"));
                    plans.add(plan);
                }
            }
        }
        return plans;
    }
}