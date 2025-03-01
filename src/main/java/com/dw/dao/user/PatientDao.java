package com.dw.dao.user;

import com.dw.model.user.Patient;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 患者数据访问层
 */
public class PatientDao {

    /**
     * 添加患者
     * @param patient 患者对象
     * @return 新增患者的ID，失败返回-1
     */
    public int add(Patient patient) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int patientId = -1;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO patient(user_id, name, gender, age, phone, address) VALUES(?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, patient.getUserId());
            stmt.setString(2, patient.getName());
            stmt.setString(3, patient.getGender());
            stmt.setInt(4, patient.getAge());
            stmt.setString(5, patient.getPhone());
            stmt.setString(6, patient.getAddress());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    patientId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return patientId;
    }

    /**
     * 根据用户ID查找患者
     * @param userId 用户ID
     * @return 患者对象，如果不存在返回null
     */
    public Patient findByUserId(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Patient patient = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM patient WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                patient = new Patient();
                patient.setId(rs.getInt("id"));
                patient.setUserId(rs.getInt("user_id"));
                patient.setName(rs.getString("name"));
                patient.setGender(rs.getString("gender"));
                patient.setAge(rs.getInt("age"));
                patient.setPhone(rs.getString("phone"));
                patient.setAddress(rs.getString("address"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return patient;
    }

    /**
     * 根据ID查找患者
     * @param id 患者ID
     * @return 患者对象，如果不存在返回null
     */
    public Patient findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Patient patient = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM patient WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                patient = new Patient();
                patient.setId(rs.getInt("id"));
                patient.setUserId(rs.getInt("user_id"));
                patient.setName(rs.getString("name"));
                patient.setGender(rs.getString("gender"));
                patient.setAge(rs.getInt("age"));
                patient.setPhone(rs.getString("phone"));
                patient.setAddress(rs.getString("address"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return patient;
    }

    /**
     * 获取所有患者
     * @return 患者列表
     */
    public List<Patient> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Patient> patients = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM patient";
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();
            while (rs.next()) {
                Patient patient = new Patient();
                patient.setId(rs.getInt("id"));
                patient.setUserId(rs.getInt("user_id"));
                patient.setName(rs.getString("name"));
                patient.setGender(rs.getString("gender"));
                patient.setAge(rs.getInt("age"));
                patient.setPhone(rs.getString("phone"));
                patient.setAddress(rs.getString("address"));
                patients.add(patient);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return patients;
    }

    /**
     * 更新患者信息
     * @param patient 患者对象
     * @return 成功返回true，失败返回false
     */
    public boolean update(Patient patient) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE patient SET name = ?, gender = ?, age = ?, phone = ?, address = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getGender());
            stmt.setInt(3, patient.getAge());
            stmt.setString(4, patient.getPhone());
            stmt.setString(5, patient.getAddress());
            stmt.setInt(6, patient.getId());

            int affectedRows = stmt.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, null);
        }

        return success;
    }
}