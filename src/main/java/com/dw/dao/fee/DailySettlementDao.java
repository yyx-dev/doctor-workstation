// DailySettlementDao.java 日结数据访问类
package com.dw.dao.fee;

import com.dw.model.fee.DailySettlement;
import com.dw.util.DBUtil;
import java.sql.*;

public class DailySettlementDao {
    public boolean createSettlement(DailySettlement settlement) throws SQLException {
        String sql = "INSERT INTO daily_settlement (settlement_no, settle_date, total_amount, "
                + "cash_amount, refund_amount, invoice_count, operator_id, remark) "
                + "VALUES (?,?,?,?,?,?,?,?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, settlement.getSettlementNo());
            pstmt.setTimestamp(2, settlement.getSettleDate());
            pstmt.setBigDecimal(3, settlement.getTotalAmount());
            pstmt.setBigDecimal(4, settlement.getCashAmount());
            pstmt.setBigDecimal(5, settlement.getRefundAmount());
            pstmt.setInt(6, settlement.getInvoiceCount());
            pstmt.setInt(7, settlement.getOperatorId());
            pstmt.setString(8, settlement.getRemark());

            return pstmt.executeUpdate() > 0;
        }
    }

    public DailySettlement getLastSettlement() throws SQLException {
        String sql = "SELECT * FROM daily_settlement ORDER BY settle_date DESC LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                DailySettlement ds = new DailySettlement();
                ds.setId(rs.getInt("id"));
                ds.setSettlementNo(rs.getString("settlement_no"));
                ds.setSettleDate(rs.getTimestamp("settle_date"));
                ds.setTotalAmount(rs.getBigDecimal("total_amount"));
                ds.setCashAmount(rs.getBigDecimal("cash_amount"));
                ds.setRefundAmount(rs.getBigDecimal("refund_amount"));
                ds.setInvoiceCount(rs.getInt("invoice_count"));
                ds.setOperatorId(rs.getInt("operator_id"));
                ds.setRemark(rs.getString("remark"));
                return ds;
            }
        }
        return null;
    }
}