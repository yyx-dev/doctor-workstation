// FeeQueryPanel.java 费用查询界面
package com.dw.gui.panel.fee;

import com.dw.dao.invoice.InvoiceDao;
import com.dw.dao.fee.RefundRequestDao;
import com.dw.model.invoice.Invoice;
import com.dw.model.fee.RefundRequest;
import com.dw.model.user.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class FeeQueryPanel extends JPanel {
    private JTextField tfPatientId;
    private JTextField tfInvoiceNo;
    private JTextField tfStartDate;
    private JTextField tfEndDate;
    private JComboBox<String> cbFeeType;
    private JTable resultTable;

    public FeeQueryPanel(Doctor doctor) {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(UIUtil.createTitleLabel("费用查询"));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 查询条件面板
        JPanel queryPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        queryPanel.setBackground(Color.WHITE);
        queryPanel.setBorder(BorderFactory.createTitledBorder("查询条件"));

        queryPanel.add(UIUtil.createLabel("患者ID："));
        tfPatientId = UIUtil.createTextField();
        queryPanel.add(tfPatientId);

        queryPanel.add(UIUtil.createLabel("发票号："));
        tfInvoiceNo = UIUtil.createTextField();
        queryPanel.add(tfInvoiceNo);

        queryPanel.add(UIUtil.createLabel("开始日期："));
        tfStartDate = UIUtil.createTextField();
        queryPanel.add(tfStartDate);

        queryPanel.add(UIUtil.createLabel("结束日期："));
        tfEndDate = UIUtil.createTextField();
        queryPanel.add(tfEndDate);

        queryPanel.add(UIUtil.createLabel("费用类型："));
        cbFeeType = UIUtil.createComboBox(new String[]{"全部", "收费", "退费"});
        queryPanel.add(cbFeeType);

        // 操作按钮
        JButton btnSearch = UIUtil.createButton("查询");
        btnSearch.addActionListener(this::performSearch);
        JButton btnReset = UIUtil.createButton("重置");
        btnReset.addActionListener(e -> resetCriteria());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnReset);

        // 结果表格
        resultTable = new JTable();
        resultTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(resultTable);

        // 主内容面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(queryPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void performSearch(ActionEvent e) {
        try {
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"日期", "类型", "票据号", "患者ID", "金额"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            String feeType = (String) cbFeeType.getSelectedItem();

            // 查询发票记录
            if (feeType.equals("全部") || feeType.equals("收费")) {
                List<Invoice> invoices = new InvoiceDao().search(
                        tfInvoiceNo.getText().trim(),
                        tfPatientId.getText().trim().isEmpty() ? null : Integer.parseInt(tfPatientId.getText()),
                        tfStartDate.getText().trim(),
                        tfEndDate.getText().trim()
                );
                for (Invoice inv : invoices) {
                    model.addRow(new Object[]{
                            inv.getCreateTime(),
                            "收费",
                            inv.getInvoiceNo(),
                            inv.getPatientId(),
                            "+" + inv.getAmount()
                    });
                }
            }

            // 查询退费记录
            if (feeType.equals("全部") || feeType.equals("退费")) {
                List<RefundRequest> refunds = new RefundRequestDao().search(
                        tfPatientId.getText().trim().isEmpty() ? null : Integer.parseInt(tfPatientId.getText()),
                        tfStartDate.getText().trim(),
                        tfEndDate.getText().trim()
                );
                for (RefundRequest rr : refunds) {
                    model.addRow(new Object[]{
                            rr.getCreateTime(),
                            "退费",
                            rr.getRefundNo(),
                            rr.getPatientId(),
                            "-" + rr.getAmount()
                    });
                }
            }

            resultTable.setModel(model);
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "患者ID必须为数字", "输入错误");
        } catch (Exception ex) {
            UIUtil.showError(this, "查询失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void resetCriteria() {
        tfPatientId.setText("");
        tfInvoiceNo.setText("");
        tfStartDate.setText("");
        tfEndDate.setText("");
        cbFeeType.setSelectedIndex(0);
        resultTable.setModel(new DefaultTableModel());
    }
}