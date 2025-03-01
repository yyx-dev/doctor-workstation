package com.dw.dao;

import com.dw.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class NumberSourceDao {
    public void insertTimeSlot(int doctorId, String timeSlot, Date date) throws SQLException {
        String sql = "INSERT INTO number_source (doctor_id, time_slot, date) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            stmt.setString(2, timeSlot);
            stmt.setDate(3, new java.sql.Date(date.getTime()));
            stmt.executeUpdate();
        }
    }
}