package com.dw.dao;

import com.dw.model.MedicalRecord;
import com.dw.model.Patient;
import com.dw.model.Doctor;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 病历数据访问层
 */
public class MedicalRecordDao {

    /**
     * 添加病历记录
     * @param medicalRecord 病历对象
     * @return 新增病历记录的ID，失败返回-1
     */
    public int add(MedicalRecord medicalRecord) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int medicalRecordId = -1;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO medical_record(appointment_id, doctor_id, patient_id, diagnosis, prescription) VALUES(?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, medicalRecord.getAppointmentId());
            stmt.setInt(2, medicalRecord.getDoctorId());
            stmt.setInt(3, medicalRecord.getPatientId());
            stmt.setString(4, medicalRecord.getDiagnosis());
            stmt.setString(5, medicalRecord.getPrescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    medicalRecordId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return medicalRecordId;
    }

    /**
     * 根据ID查找病历记录
     * @param id 病历记录ID
     * @return 病历对象，如果不存在返回null
     */
    public MedicalRecord findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        MedicalRecord medicalRecord = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM medical_record WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                medicalRecord = new MedicalRecord();
                medicalRecord.setId(rs.getInt("id"));
                medicalRecord.setAppointmentId(rs.getInt("appointment_id"));
                medicalRecord.setDoctorId(rs.getInt("doctor_id"));
                medicalRecord.setPatientId(rs.getInt("patient_id"));
                medicalRecord.setDiagnosis(rs.getString("diagnosis"));
                medicalRecord.setPrescription(rs.getString("prescription"));
                medicalRecord.setCreateTime(rs.getTimestamp("create_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return medicalRecord;
    }

    /**
     * 根据挂号ID查找病历记录
     * @param appointmentId 挂号记录ID
     * @return 病历对象，如果不存在返回null
     */
    public MedicalRecord findByAppointmentId(int appointmentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        MedicalRecord medicalRecord = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM medical_record WHERE appointment_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, appointmentId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                medicalRecord = new MedicalRecord();
                medicalRecord.setId(rs.getInt("id"));
                medicalRecord.setAppointmentId(rs.getInt("appointment_id"));
                medicalRecord.setDoctorId(rs.getInt("doctor_id"));
                medicalRecord.setPatientId(rs.getInt("patient_id"));
                medicalRecord.setDiagnosis(rs.getString("diagnosis"));
                medicalRecord.setPrescription(rs.getString("prescription"));
                medicalRecord.setCreateTime(rs.getTimestamp("create_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return medicalRecord;
    }

    /**
     * 根据患者ID查找病历记录
     * @param patientId 患者ID
     * @return 病历记录列表
     */
    public List<MedicalRecord> findByPatientId(int patientId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<MedicalRecord> medicalRecords = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM medical_record WHERE patient_id = ? ORDER BY create_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                MedicalRecord medicalRecord = new MedicalRecord();
                medicalRecord.setId(rs.getInt("id"));
                medicalRecord.setAppointmentId(rs.getInt("appointment_id"));
                medicalRecord.setDoctorId(rs.getInt("doctor_id"));
                medicalRecord.setPatientId(rs.getInt("patient_id"));
                medicalRecord.setDiagnosis(rs.getString("diagnosis"));
                medicalRecord.setPrescription(rs.getString("prescription"));
                medicalRecord.setCreateTime(rs.getTimestamp("create_time"));
                medicalRecords.add(medicalRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return medicalRecords;
    }

    /**
     * 根据医生ID查找病历记录
     * @param doctorId 医生ID
     * @return 病历记录列表
     */
    public List<MedicalRecord> findByDoctorId(int doctorId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<MedicalRecord> medicalRecords = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM medical_record WHERE doctor_id = ? ORDER BY create_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                MedicalRecord medicalRecord = new MedicalRecord();
                medicalRecord.setId(rs.getInt("id"));
                medicalRecord.setAppointmentId(rs.getInt("appointment_id"));
                medicalRecord.setDoctorId(rs.getInt("doctor_id"));
                medicalRecord.setPatientId(rs.getInt("patient_id"));
                medicalRecord.setDiagnosis(rs.getString("diagnosis"));
                medicalRecord.setPrescription(rs.getString("prescription"));
                medicalRecord.setCreateTime(rs.getTimestamp("create_time"));
                medicalRecords.add(medicalRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return medicalRecords;
    }

    /**
     * 获取所有病历记录
     * @return 病历记录列表
     */
    public List<MedicalRecord> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<MedicalRecord> medicalRecords = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM medical_record ORDER BY create_time DESC";
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();
            while (rs.next()) {
                MedicalRecord medicalRecord = new MedicalRecord();
                medicalRecord.setId(rs.getInt("id"));
                medicalRecord.setAppointmentId(rs.getInt("appointment_id"));
                medicalRecord.setDoctorId(rs.getInt("doctor_id"));
                medicalRecord.setPatientId(rs.getInt("patient_id"));
                medicalRecord.setDiagnosis(rs.getString("diagnosis"));
                medicalRecord.setPrescription(rs.getString("prescription"));
                medicalRecord.setCreateTime(rs.getTimestamp("create_time"));
                medicalRecords.add(medicalRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return medicalRecords;
    }

    /**
     * 获取患者详细信息的病历记录列表
     * @param patientId 患者ID
     * @return 包含详细信息的病历记录列表
     */
    public List<MedicalRecord> findDetailByPatientId(int patientId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<MedicalRecord> medicalRecords = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT mr.*, d.name as doctor_name, d.department, a.chief_complaint " +
                    "FROM medical_record mr " +
                    "JOIN doctor d ON mr.doctor_id = d.id " +
                    "JOIN appointment a ON mr.appointment_id = a.id " +
                    "WHERE mr.patient_id = ? " +
                    "ORDER BY mr.create_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                MedicalRecord medicalRecord = new MedicalRecord();
                medicalRecord.setId(rs.getInt("id"));
                medicalRecord.setAppointmentId(rs.getInt("appointment_id"));
                medicalRecord.setDoctorId(rs.getInt("doctor_id"));
                medicalRecord.setPatientId(rs.getInt("patient_id"));
                medicalRecord.setDiagnosis(rs.getString("diagnosis"));
                medicalRecord.setPrescription(rs.getString("prescription"));
                medicalRecord.setCreateTime(rs.getTimestamp("create_time"));

                // 设置医生信息
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("doctor_id"));
                doctor.setName(rs.getString("doctor_name"));
                doctor.setDepartment(rs.getString("department"));
                medicalRecord.setDoctor(doctor);

                medicalRecords.add(medicalRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return medicalRecords;
    }
}