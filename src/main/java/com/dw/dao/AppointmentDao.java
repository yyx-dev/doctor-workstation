package com.dw.dao;

import com.dw.model.Appointment;
import com.dw.model.Patient;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 挂号数据访问层
 */
public class AppointmentDao {

    /**
     * 添加挂号记录
     * @param appointment 挂号对象
     * @return 新增挂号记录的ID，失败返回-1
     */
    public int add(Appointment appointment) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int appointmentId = -1;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO appointment(patient_id, doctor_id, department, status, chief_complaint) VALUES(?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, appointment.getPatientId());

            // 处理doctor_id可能为null的情况
            if (appointment.getDoctorId() != null) {
                stmt.setInt(2, appointment.getDoctorId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, appointment.getDepartment());
            stmt.setString(4, appointment.getStatus());
            stmt.setString(5, appointment.getChiefComplaint());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    appointmentId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return appointmentId;
    }

    /**
     * 根据ID查找挂号记录
     * @param id 挂号记录ID
     * @return 挂号对象，如果不存在返回null
     */
    public Appointment findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Appointment appointment = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM appointment WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(rs.getInt("patient_id"));

                // 处理doctor_id可能为null的情况
                int doctorId = rs.getInt("doctor_id");
                if (!rs.wasNull()) {
                    appointment.setDoctorId(doctorId);
                }

                appointment.setDepartment(rs.getString("department"));
                appointment.setStatus(rs.getString("status"));
                appointment.setChiefComplaint(rs.getString("chief_complaint"));
                appointment.setAppointmentTime(rs.getTimestamp("appointment_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return appointment;
    }

    /**
     * 根据患者ID查找挂号记录
     * @param patientId 患者ID
     * @return 挂号记录列表
     */
    public List<Appointment> findByPatientId(int patientId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM appointment WHERE patient_id = ? ORDER BY appointment_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(rs.getInt("patient_id"));

                // 处理doctor_id可能为null的情况
                int doctorId = rs.getInt("doctor_id");
                if (!rs.wasNull()) {
                    appointment.setDoctorId(doctorId);
                }

                appointment.setDepartment(rs.getString("department"));
                appointment.setStatus(rs.getString("status"));
                appointment.setChiefComplaint(rs.getString("chief_complaint"));
                appointment.setAppointmentTime(rs.getTimestamp("appointment_time"));
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return appointments;
    }

    /**
     * 根据医生ID查找挂号记录
     * @param doctorId 医生ID
     * @return 挂号记录列表
     */
    public List<Appointment> findByDoctorId(int doctorId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT a.*, p.name as patient_name, p.gender as patient_gender, p.age as patient_age " +
                    "FROM appointment a " +
                    "JOIN patient p ON a.patient_id = p.id " +
                    "WHERE a.doctor_id = ? " +
                    "ORDER BY a.appointment_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);

            rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(rs.getInt("patient_id"));
                appointment.setDoctorId(rs.getInt("doctor_id"));
                appointment.setDepartment(rs.getString("department"));
                appointment.setStatus(rs.getString("status"));
                appointment.setChiefComplaint(rs.getString("chief_complaint"));
                appointment.setAppointmentTime(rs.getTimestamp("appointment_time"));

                // 添加患者基本信息
                Patient patient = new Patient();
                patient.setId(rs.getInt("patient_id"));
                patient.setName(rs.getString("patient_name"));
                patient.setGender(rs.getString("patient_gender"));
                patient.setAge(rs.getInt("patient_age"));
                appointment.setPatient(patient);

                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return appointments;
    }

    /**
     * 根据科室查找挂号记录
     * @param department 科室名称
     * @return 挂号记录列表
     */
    public List<Appointment> findByDepartment(String department) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT a.*, p.name as patient_name, p.gender as patient_gender, p.age as patient_age " +
                    "FROM appointment a " +
                    "JOIN patient p ON a.patient_id = p.id " +
                    "WHERE a.department = ? " +
                    "ORDER BY a.appointment_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, department);

            rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(rs.getInt("patient_id"));

                // 处理doctor_id可能为null的情况
                int doctorId = rs.getInt("doctor_id");
                if (!rs.wasNull()) {
                    appointment.setDoctorId(doctorId);
                }

                appointment.setDepartment(rs.getString("department"));
                appointment.setStatus(rs.getString("status"));
                appointment.setChiefComplaint(rs.getString("chief_complaint"));
                appointment.setAppointmentTime(rs.getTimestamp("appointment_time"));

                // 添加患者基本信息
                Patient patient = new Patient();
                patient.setId(rs.getInt("patient_id"));
                patient.setName(rs.getString("patient_name"));
                patient.setGender(rs.getString("patient_gender"));
                patient.setAge(rs.getInt("patient_age"));
                appointment.setPatient(patient);

                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return appointments;
    }

    /**
     * 获取所有挂号记录(包含患者信息)
     * @return 挂号记录列表
     */
    public List<Appointment> findAllWithPatientInfo() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT a.*, p.name as patient_name, p.gender as patient_gender, p.age as patient_age " +
                    "FROM appointment a " +
                    "JOIN patient p ON a.patient_id = p.id " +
                    "ORDER BY a.appointment_time DESC";
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(rs.getInt("patient_id"));

                // 处理doctor_id可能为null的情况
                int doctorId = rs.getInt("doctor_id");
                if (!rs.wasNull()) {
                    appointment.setDoctorId(doctorId);
                }

                appointment.setDepartment(rs.getString("department"));
                appointment.setStatus(rs.getString("status"));
                appointment.setChiefComplaint(rs.getString("chief_complaint"));
                appointment.setAppointmentTime(rs.getTimestamp("appointment_time"));

                // 添加患者基本信息
                Patient patient = new Patient();
                patient.setId(rs.getInt("patient_id"));
                patient.setName(rs.getString("patient_name"));
                patient.setGender(rs.getString("patient_gender"));
                patient.setAge(rs.getInt("patient_age"));
                appointment.setPatient(patient);

                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return appointments;
    }

    /**
     * 根据状态查找挂号记录(包含患者信息)
     * @param status 状态('waiting', 'processing', 'completed')
     * @return 挂号记录列表
     */
    public List<Appointment> findByStatusWithPatientInfo(String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Appointment> appointments = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT a.*, p.name as patient_name, p.gender as patient_gender, p.age as patient_age " +
                    "FROM appointment a " +
                    "JOIN patient p ON a.patient_id = p.id " +
                    "WHERE a.status = ? " +
                    "ORDER BY a.appointment_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);

            rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setPatientId(rs.getInt("patient_id"));

                // 处理doctor_id可能为null的情况
                int doctorId = rs.getInt("doctor_id");
                if (!rs.wasNull()) {
                    appointment.setDoctorId(doctorId);
                }

                appointment.setDepartment(rs.getString("department"));
                appointment.setStatus(rs.getString("status"));
                appointment.setChiefComplaint(rs.getString("chief_complaint"));
                appointment.setAppointmentTime(rs.getTimestamp("appointment_time"));

                // 添加患者基本信息
                Patient patient = new Patient();
                patient.setId(rs.getInt("patient_id"));
                patient.setName(rs.getString("patient_name"));
                patient.setGender(rs.getString("patient_gender"));
                patient.setAge(rs.getInt("patient_age"));
                appointment.setPatient(patient);

                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return appointments;
    }

    /**
     * 更新挂号记录状态
     * @param id 挂号记录ID
     * @param status 新状态
     * @return 成功返回true，失败返回false
     */
    public boolean updateStatus(int id, String status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE appointment SET status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, null);
        }

        return success;
    }

    /**
     * 更新挂号记录的主诉
     * @param id 挂号记录ID
     * @param chiefComplaint 主诉内容
     * @return 成功返回true，失败返回false
     */
    public boolean updateChiefComplaint(int id, String chiefComplaint) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE appointment SET chief_complaint = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, chiefComplaint);
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, null);
        }

        return success;
    }

    /**
     * 分配医生给挂号记录
     * @param id 挂号记录ID
     * @param doctorId 医生ID
     * @return 成功返回true，失败返回false
     */
    public boolean assignDoctor(int id, int doctorId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE appointment SET doctor_id = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, doctorId);
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, null);
        }

        return success;
    }

    /**
     * 更新挂号记录
     * @param appointment 挂号记录
     * @return 成功返回true，失败返回false
     */
    public boolean update(Appointment appointment) {
        String sql = "UPDATE appointment SET status = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, appointment.getStatus());
            statement.setInt(2, appointment.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getDepartments() {
        List<String> departments = new ArrayList<>();
        String sql = "SELECT DISTINCT department FROM appointment";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                departments.add(rs.getString("department"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    public List<Appointment> getCancelableAppointments() throws SQLException {
        String sql = "SELECT a.*, p.name as patient_name " +
                "FROM appointment a " +
                "JOIN patient p ON a.patient_id = p.id " +
                "WHERE a.status IN ('PENDING', 'CONFIRMED') " +
                "ORDER BY a.appointment_time DESC";

        List<Appointment> appointments = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Appointment app = new Appointment();
                app.setId(rs.getInt("id"));
                app.setPatientId(rs.getInt("patient_id"));
                app.setAppointmentTime(rs.getTimestamp("appointment_time"));
                app.setStatus(rs.getString("status"));
                appointments.add(app);
            }
        }
        return appointments;
    }

    public void cancelAppointment(int appId, String reason) throws SQLException {
        String sql = "UPDATE appointment SET status = 'CANCELED', cancel_reason = ?, cancel_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reason);
            stmt.setInt(2, appId);
            int affected = stmt.executeUpdate();

            if (affected == 0) {
                throw new SQLException("未找到可退号的预约记录");
            }
        }
    }
}