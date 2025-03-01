// ConsultationDao.java
package com.dw.dao;

import com.dw.model.ConsultationQueue;
import com.dw.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDao {
    public List<ConsultationQueue> getCurrentQueue(int doctorId) throws SQLException {
        String sql = "SELECT cq.* FROM consultation_queue cq "
                + "JOIN appointment a ON cq.appointment_id = a.id "
                + "WHERE a.doctor_id = ? AND cq.status IN ('等待中', '接诊中') "
                + "ORDER BY cq.queue_number";
        List<ConsultationQueue> queue = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ConsultationQueue item = new ConsultationQueue();
                    item.setId(rs.getInt("id"));
                    item.setAppointmentId(rs.getInt("appointment_id"));
                    item.setQueueNumber(rs.getInt("queue_number"));
                    item.setStatus(rs.getString("status"));
                    item.setStartTime(rs.getTimestamp("start_time"));
                    queue.add(item);
                }
            }
        }
        return queue;
    }

    public void startConsultation(int queueId) throws SQLException {
        String sql = "UPDATE consultation_queue SET status = '接诊中', start_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, queueId);
            pstmt.executeUpdate();
        }
    }

    public void completeConsultation(int queueId) throws SQLException {
        String sql = "UPDATE consultation_queue SET status = '已完成', end_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, queueId);
            pstmt.executeUpdate();
        }
    }
}