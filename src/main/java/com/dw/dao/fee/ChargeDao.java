// ChargeService.java 新增服务类处理复杂事务
package com.dw.dao.fee;

import com.dw.dao.invoice.InvoiceDao;
import com.dw.model.fee.ChargeRecord;
import com.dw.model.invoice.Invoice;
import com.dw.util.DBUtil;
import java.sql.Connection;
import java.sql.SQLException;

public class ChargeDao {
    private final ChargeRecordDao chargeDao = new ChargeRecordDao();
    private final InvoiceDao invoiceDao = new InvoiceDao();

    public void createChargeWithInvoice(ChargeRecord record, Invoice invoice) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 保存收费记录
            chargeDao.createWithConnection(conn, record);
            // 保存发票
            invoiceDao.createWithConnection(conn, invoice);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }
}