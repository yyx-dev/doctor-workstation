package com.dw.dao.user;

import com.dw.model.user.Doctor;
import com.dw.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 医生数据访问层
 */
public class DoctorDao {

    /**
     * 添加医生
     * @param doctor 医生对象
     * @return 新增医生的ID，失败返回-1
     */
    public int add(Doctor doctor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int doctorId = -1;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO doctor(user_id, name, gender, department, title, phone) VALUES(?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, doctor.getUserId());
            stmt.setString(2, doctor.getName());
            stmt.setString(3, doctor.getGender());
            stmt.setString(4, doctor.getDepartment());
            stmt.setString(5, doctor.getTitle());
            stmt.setString(6, doctor.getPhone());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    doctorId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return doctorId;
    }

    /**
     * 根据用户ID查找医生
     * @param userId 用户ID
     * @return 医生对象，如果不存在返回null
     */
    public Doctor findByUserId(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Doctor doctor = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM doctor WHERE user_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setUserId(rs.getInt("user_id"));
                doctor.setName(rs.getString("name"));
                doctor.setGender(rs.getString("gender"));
                doctor.setDepartment(rs.getString("department"));
                doctor.setTitle(rs.getString("title"));
                doctor.setPhone(rs.getString("phone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return doctor;
    }

    /**
     * 根据ID查找医生
     * @param id 医生ID
     * @return 医生对象，如果不存在返回null
     */
    public Doctor findById(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Doctor doctor = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM doctor WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setUserId(rs.getInt("user_id"));
                doctor.setName(rs.getString("name"));
                doctor.setGender(rs.getString("gender"));
                doctor.setDepartment(rs.getString("department"));
                doctor.setTitle(rs.getString("title"));
                doctor.setPhone(rs.getString("phone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return doctor;
    }

    /**
     * 根据科室查找医生列表
     * @param department 科室名称
     * @return 医生列表
     */
    public List<Doctor> findByDepartment(String department) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Doctor> doctors = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM doctor WHERE department = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, department);

            rs = stmt.executeQuery();
            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setUserId(rs.getInt("user_id"));
                doctor.setName(rs.getString("name"));
                doctor.setGender(rs.getString("gender"));
                doctor.setDepartment(rs.getString("department"));
                doctor.setTitle(rs.getString("title"));
                doctor.setPhone(rs.getString("phone"));
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return doctors;
    }

    /**
     * 获取所有医生
     * @return 医生列表
     */
    public List<Doctor> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Doctor> doctors = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM doctor";
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();
            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(rs.getInt("id"));
                doctor.setUserId(rs.getInt("user_id"));
                doctor.setName(rs.getString("name"));
                doctor.setGender(rs.getString("gender"));
                doctor.setDepartment(rs.getString("department"));
                doctor.setTitle(rs.getString("title"));
                doctor.setPhone(rs.getString("phone"));
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return doctors;
    }

    /**
     * 获取所有科室列表
     * @return 科室名称列表
     */
    public List<String> findAllDepartments() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<String> departments = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT DISTINCT department FROM doctor";
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();
            while (rs.next()) {
                departments.add(rs.getString("department"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, rs);
        }

        return departments;
    }

    /**
     * 更新医生信息
     * @param doctor 医生对象
     * @return 成功返回true，失败返回false
     */
    public boolean update(Doctor doctor) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE doctor SET name = ?, gender = ?, department = ?, title = ?, phone = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getGender());
            stmt.setString(3, doctor.getDepartment());
            stmt.setString(4, doctor.getTitle());
            stmt.setString(5, doctor.getPhone());
            stmt.setInt(6, doctor.getId());

            int affectedRows = stmt.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, stmt, null);
        }

        return success;
    }

    public Doctor findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM doctor WHERE user_id = (SELECT id FROM user WHERE username = ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Doctor doctor = new Doctor();
                    doctor.setId(rs.getInt("id"));
                    doctor.setName(rs.getString("name"));
                    doctor.setDepartment(rs.getString("department"));
                    // 设置其他字段...
                    return doctor;
                }
                return null;
            }
        }
    }
}