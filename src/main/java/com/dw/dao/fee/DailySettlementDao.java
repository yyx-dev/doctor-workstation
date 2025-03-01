// DailySettlementDao.java 日结数据访问类
package com.dw.dao.fee;

import com.dw.model.fee.DailySettlement;
import com.dw.util.DBUtil;
import java.sql.*;
import java.time.LocalDateTime;

public class DailySettlementDao {
    public boolean createSettlement(DailySettlement settlement) throws SQLException {
        String sql = "INSERT INTO daily_settlement (settlement_no, settle_date, total_amount, "
                + "cash_amount, refund_amount, invoice_count, operator_id, remark) "
                + "VALUES (?,?,?,?,?,?,?,?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, settlement.getSettlementNo());
            pstmt.setTimestamp(2, Timestamp.valueOf(settlement.getSettleDate()));
            pstmt.setBigDecimal(3, settlement.getTotalAmount());
            pstmt.setBigDecimal(4, settlement.getCashAmount());
            pstmt.setBigDecimal(5, settlement.getRefundAmount());
            pstmt.setInt(6, settlement.getInvoiceCount());
            pstmt.setInt(7, settlement.getOperatorId());
            pstmt.setString(8, settlement.getRemark());

            return pstmt.executeUpdate() > 0;
        }
    }

    // 在DailySettlementDao的getLastSettlement方法中
    public DailySettlement getLastSettlement() throws SQLException {
        String sql = "SELECT * FROM daily_settlement ORDER BY settle_date DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                DailySettlement ds = new DailySettlement();
                ds.setSettlementNo(rs.getString("settlement_no"));

                Timestamp timestamp = rs.getTimestamp("settle_date");
                if (timestamp != null) {
                    ds.setSettleDate(timestamp.toLocalDateTime());
                } else {
                    // 处理无日期的情况
                    ds.setSettleDate(LocalDateTime.MIN);
                    // 或抛出明确异常：throw new SQLException("settle_date字段为空");
                }
                return ds;
            }
            return null; // 明确返回null表示无记录
        } catch (SQLException e) {
            // 记录具体错误
            System.err.println("获取日结记录失败: " + e.getMessage());
            throw e; // 抛出异常让上层处理
        }
    }
}