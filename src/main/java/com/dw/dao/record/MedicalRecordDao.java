package com.dw.dao.record;

import com.dw.model.record.MedicalRecord;
import com.dw.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDao {
    // 创建病历
    public int add(MedicalRecord record) throws SQLException {
        String sql = "INSERT INTO medical_record (appointment_id, patient_id, doctor_id, "
                + "admission_date, discharge_date, diagnosis, status, prescription) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, record.getAppointmentId());
            pstmt.setInt(2, record.getPatientId());
            pstmt.setInt(3, record.getDoctorId());
            pstmt.setDate(4, record.getAdmissionDate());
            pstmt.setDate(5, record.getDischargeDate());
            pstmt.setString(6, record.getDiagnosis());
            pstmt.setString(7, record.getStatus());
            pstmt.setString(8, record.getPrescription());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    // 根据ID获取病历
    public MedicalRecord findById(int id) throws SQLException {
        String sql = "SELECT * FROM medical_record WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedicalRecord(rs);
                }
                return null;
            }
        }
    }

    // 更新病历
    public boolean update(MedicalRecord record) throws SQLException {
        String sql = "UPDATE medical_record SET "
                + "discharge_date = ?, diagnosis = ?, status = ?, prescription = ? "
                + "WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, record.getDischargeDate());
            pstmt.setString(2, record.getDiagnosis());
            pstmt.setString(3, record.getStatus());
            pstmt.setString(4, record.getPrescription());
            pstmt.setInt(5, record.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // 根据患者ID查询病历
    public List<MedicalRecord> findByPatientId(int patientId) throws SQLException {
        String sql = "SELECT * FROM medical_record WHERE patient_id = ? ORDER BY admission_date DESC";
        return executeQueryWithParam(sql, patientId);
    }

    // 根据医生ID查询病历
    public List<MedicalRecord> findByDoctorId(int doctorId) throws SQLException {
        String sql = "SELECT * FROM medical_record WHERE doctor_id = ? ORDER BY admission_date DESC";
        return executeQueryWithParam(sql, doctorId);
    }

    // 公共查询方法
    private List<MedicalRecord> executeQueryWithParam(String sql, int param) throws SQLException {
        List<MedicalRecord> records = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToMedicalRecord(rs));
                }
            }
        }
        return records;
    }

    // 结果集映射
    private MedicalRecord mapResultSetToMedicalRecord(ResultSet rs) throws SQLException {
        MedicalRecord record = new MedicalRecord();
        record.setId(rs.getInt("id"));
        record.setAppointmentId(rs.getInt("appointment_id"));
        record.setPatientId(rs.getInt("patient_id"));
        record.setDoctorId(rs.getInt("doctor_id"));
        record.setAdmissionDate(rs.getDate("admission_date"));
        record.setDischargeDate(rs.getDate("discharge_date"));
        record.setDiagnosis(rs.getString("diagnosis"));
        record.setStatus(rs.getString("status"));
        record.setPrescription(rs.getString("prescription"));
        record.setCreatedAt(rs.getTimestamp("created_at"));
        return record;
    }

    public List<MedicalRecord> getSortedRecords(String sortType) throws SQLException {
        String sql = "SELECT m.*, p.name as patient_name " +
                "FROM medical_record m " +
                "JOIN patient p ON m.patient_id = p.id " +
                getOrderClause(sortType);

        List<MedicalRecord> records = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MedicalRecord record = new MedicalRecord();
                record.setId(rs.getInt("id"));
                record.setPatientId(rs.getInt("patient_id"));
                record.setAdmissionDate(rs.getDate("admission_date"));
                record.setDiagnosis(rs.getString("diagnosis"));
                records.add(record);
            }
        }
        return records;
    }

    private String getOrderClause(String sortType) {
        return switch (sortType) {
            case "按患者姓名" -> " ORDER BY p.name";
            case "按科室" -> " ORDER BY m.department";
            default -> " ORDER BY m.admission_date";
        };
    }

    private String getSortColumn(String sortType) {
        switch (sortType) {
            case "按患者姓名": return "patient_name";
            case "按科室": return "department";
            default: return "admission_date";
        }
    }

    public List<MedicalRecord> getAllActiveRecords() throws SQLException {
        String sql = "SELECT m.id, m.patient_id, p.name AS patient_name, "
                + "m.admission_date, m.diagnosis, m.status "
                + "FROM medical_record m "
                + "JOIN patient p ON m.patient_id = p.id "
                + "WHERE m.status = 'ACTIVE' "
                + "ORDER BY m.admission_date DESC";

        List<MedicalRecord> records = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                MedicalRecord record = new MedicalRecord();
                record.setId(rs.getInt("id"));
                record.setPatientId(rs.getInt("patient_id"));
                record.setAdmissionDate(rs.getDate("admission_date"));
                record.setDiagnosis(rs.getString("diagnosis"));
                record.setStatus(rs.getString("status"));
                records.add(record);
            }
        }
        return records;
    }

    public void withdrawRecord(int recordId) throws SQLException {
        String sql = "UPDATE medical_record SET status = 'WITHDRAWN' WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            stmt.executeUpdate();
        }
    }
}