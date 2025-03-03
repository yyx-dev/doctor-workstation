// DailySettlementPanel.java 日结操作界面
package com.dw.gui.panel.fee;

import com.dw.dao.fee.DailySettlementDao;
import com.dw.dao.invoice.InvoiceDao;
import com.dw.dao.fee.RefundRequestDao;
import com.dw.model.fee.DailySettlement;
import com.dw.model.user.Doctor;
import com.dw.util.DBUtil;
import com.dw.util.UIUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DailySettlementPanel extends JPanel {
    private final Doctor doctor;
    private JLabel lblLastSettleTime;
    private JLabel lblTodayIncome;
    private JLabel lblTodayRefund;
    private JTextField tfCashAmount;
    private JTextArea taRemark;

    private DailySettlement currentSettlement;

    public DailySettlementPanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
        loadLastSettlement();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(UIUtil.createTitleLabel("费用日结"));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 统计信息面板
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder("本日统计"));

        statsPanel.add(UIUtil.createLabel("最后日结时间："));
        lblLastSettleTime = UIUtil.createLabel("");
        statsPanel.add(lblLastSettleTime);

        statsPanel.add(UIUtil.createLabel("本日总收入："));
        lblTodayIncome = UIUtil.createLabel("");
        statsPanel.add(lblTodayIncome);

        statsPanel.add(UIUtil.createLabel("本日总退费："));
        lblTodayRefund = UIUtil.createLabel("");
        statsPanel.add(lblTodayRefund);

        // 输入面板
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("结算信息"));

        inputPanel.add(UIUtil.createLabel("现金金额："));
        tfCashAmount = UIUtil.createTextField();
        inputPanel.add(tfCashAmount);

        inputPanel.add(UIUtil.createLabel("备注说明："));
        taRemark = UIUtil.createTextArea(3, 20);
        inputPanel.add(new JScrollPane(taRemark));

        // 操作按钮
        JButton btnSettle = UIUtil.createButton("执行日结");
        btnSettle.addActionListener(this::performSettlement);
        JButton btnPrint = UIUtil.createButton("打印报表");
        btnPrint.addActionListener(this::printReport);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSettle);
        buttonPanel.add(btnPrint);

        // 主内容面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadLastSettlement() {
        try {
            DailySettlement last = new DailySettlementDao().getLastSettlement();
            if (last != null && last.getSettleDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate = last.getSettleDate().format(formatter);
                lblLastSettleTime.setText(formattedDate);
            } else {
                lblLastSettleTime.setText("暂无历史日结记录");
            }
        } catch (SQLException e) {
            UIUtil.showError(this, "数据库错误：" + e.getMessage(), "加载失败");
            e.printStackTrace();
        } catch (Exception e) {
            UIUtil.showError(this, "系统错误：" + e.getMessage(), "加载失败");
            e.printStackTrace();
        }
    }

    private void performSettlement(ActionEvent e) {
        try {
            // 获取当日数据
            BigDecimal total = new InvoiceDao().getDailyTotal();
            BigDecimal refunds = new RefundRequestDao().getDailyRefundTotal();
            int invoiceCount = new InvoiceDao().getDailyCount();

            // 创建日结记录
            DailySettlement ds = new DailySettlement();
            ds.setSettlementNo("DS" + System.currentTimeMillis());
            ds.setSettleDate(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
            ds.setTotalAmount(total);
            ds.setRefundAmount(refunds);
            ds.setCashAmount(new BigDecimal(tfCashAmount.getText()));
            ds.setInvoiceCount(invoiceCount);
            ds.setOperatorId(doctor.getId());
            ds.setRemark(taRemark.getText());

            if (new DailySettlementDao().createSettlement(ds)) {
                this.currentSettlement = ds;

                JOptionPane.showMessageDialog(this, "日结操作成功完成");
                loadLastSettlement();
            }
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "现金金额格式错误", "输入错误");
        } catch (Exception ex) {
            UIUtil.showError(this, "日结失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void printReport(ActionEvent e) {
        try {
            if (currentSettlement == null) {
                UIUtil.showError(this, "请先完成日结操作", "无法打印");
                return;
            }

            JasperReport report = JasperCompileManager.compileReport("reports/daily.jrxml");

            Map<String, Object> params = new HashMap<>();
            params.put("settlementNo", currentSettlement.getSettlementNo());

            JasperPrint print = JasperFillManager.fillReport(report, params, DBUtil.getConnection());
            JasperViewer.viewReport(print, false);
        } catch (JRException ex) {
            UIUtil.showError(this, "打印失败：" + ex.getMessage(), "系统错误");
        } catch (SQLException ex) {
            UIUtil.showError(this, "打印失败：" + ex.getMessage(), "数据库错误");
        }
    }
}