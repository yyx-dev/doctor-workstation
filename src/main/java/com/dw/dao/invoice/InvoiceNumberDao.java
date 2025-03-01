// InvoiceNumberDao.java 发票号管理DAO
package com.dw.dao.invoice;

import com.dw.util.DBUtil;
import java.sql.*;

public class InvoiceNumberDao {
    public int allocateNumbers(String prefix, int start, int end) throws SQLException {
        String sql = "INSERT INTO invoice_number (full_number) VALUES (?)";
        int count = 0;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = start; i <= end; i++) {
                String number = String.format("%s%08d", prefix, i);
                pstmt.setString(1, number);
                pstmt.addBatch();

                if (i % 100 == 0) { // 分批提交
                    pstmt.executeBatch();
                }
            }
            int[] results = pstmt.executeBatch();
            for (int r : results) {
                count += r;
            }
        }
        return count;
    }

    public String checkAvailability() throws SQLException {
        String sql = "SELECT prefix, MIN(seq_num) as start, MAX(seq_num) as end, COUNT(*) as total "
                + "FROM (SELECT SUBSTR(full_number,1,3) as prefix, "
                + "CAST(SUBSTR(full_number,4) AS UNSIGNED) as seq_num "
                + "FROM invoice_number WHERE is_used = 0) t "
                + "GROUP BY prefix";

        StringBuilder result = new StringBuilder();
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.append(String.format("前缀：%s 可用号段：%08d-%08d 总数：%d\n",
                        rs.getString("prefix"),
                        rs.getInt("start"),
                        rs.getInt("end"),
                        rs.getInt("total")));
            }
        }
        return result.toString();
    }
}