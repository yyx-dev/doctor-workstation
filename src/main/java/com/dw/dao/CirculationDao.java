// CirculationDAO.java 流通记录数据访问
package com.dw.dao;

import com.dw.model.CirculationRecord;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CirculationDao {
    public List<CirculationRecord> getRecordsByMedicalRecord(int recordId) throws SQLException {
        String sql = "SELECT * FROM circulation_records WHERE medical_record_id = ?";
        List<CirculationRecord> records = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CirculationRecord record = new CirculationRecord();
                record.setRecordId(rs.getInt("medical_record_id"));
                record.setBorrower(rs.getString("borrower"));
                record.setBorrowDate(rs.getDate("borrow_date"));
                record.setReturnDate(rs.getDate("return_date"));
                record.setStatus(rs.getString("status"));
                records.add(record);
            }
        }
        return records;
    }
}