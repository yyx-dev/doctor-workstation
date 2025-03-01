// PermissionDao.java
package com.dw.dao.user;

import com.dw.model.user.Permission;
import com.dw.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionDao {
    public List<Permission> getAllPermissions() throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT * FROM permission";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Permission p = new Permission();
                p.setId(rs.getInt("id"));
                p.setPermissionCode(rs.getString("permission_code"));
                p.setDescription(rs.getString("description"));
                permissions.add(p);
            }
        }
        return permissions;
    }

    public boolean assignPermissions(int userId, List<Integer> permissionIds) throws SQLException {
        String sql = "INSERT INTO user_permission (user_id, permission_id) VALUES (?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            // 先删除旧权限
            clearPermissions(conn, userId);

            // 插入新权限
            for (Integer pid : permissionIds) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, pid);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            return true;
        }
    }

    private void clearPermissions(Connection conn, int userId) throws SQLException {
        String sql = "DELETE FROM user_permission WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }
}