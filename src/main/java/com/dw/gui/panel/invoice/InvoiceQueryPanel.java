// InvoiceQueryPanel.java 发票查询界面
package com.dw.gui.panel.invoice;

import com.dw.dao.invoice.InvoiceDao;
import com.dw.model.invoice.Invoice;
import com.dw.model.user.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class InvoiceQueryPanel extends JPanel {
    private final Doctor doctor;
    private JTextField tfInvoiceNo;
    private JTextField tfPatientId;
    private JTextField tfStartDate;
    private JTextField tfEndDate;
    private JTable resultTable;

    public InvoiceQueryPanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(UIUtil.createTitleLabel("发票查询"));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 查询条件面板
        JPanel queryPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        queryPanel.setBackground(Color.WHITE);
        queryPanel.setBorder(BorderFactory.createTitledBorder("查询条件"));

        queryPanel.add(UIUtil.createLabel("发票号码："));
        tfInvoiceNo = UIUtil.createTextField();
        queryPanel.add(tfInvoiceNo);

        queryPanel.add(UIUtil.createLabel("患者ID："));
        tfPatientId = UIUtil.createTextField();
        queryPanel.add(tfPatientId);

        queryPanel.add(UIUtil.createLabel("开始日期："));
        tfStartDate = UIUtil.createTextField();
        queryPanel.add(tfStartDate);

        queryPanel.add(UIUtil.createLabel("结束日期："));
        tfEndDate = UIUtil.createTextField();
        queryPanel.add(tfEndDate);

        // 操作按钮
        JButton btnSearch = UIUtil.createButton("查询");
        btnSearch.addActionListener(this::performSearch);

        JButton btnReset = UIUtil.createButton("重置条件");
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
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(queryPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void performSearch(ActionEvent e) {
        try {
            String invoiceNo = tfInvoiceNo.getText().trim();
            String patientId = tfPatientId.getText().trim();
            String startDate = tfStartDate.getText().trim();
            String endDate = tfEndDate.getText().trim();

            List<Invoice> results = new InvoiceDao().searchInvoices(
                    invoiceNo.isEmpty() ? null : invoiceNo,
                    patientId.isEmpty() ? null : Integer.parseInt(patientId),
                    startDate.isEmpty() ? null : startDate,
                    endDate.isEmpty() ? null : endDate
            );

            updateResultTable(results);
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "患者ID必须为数字", "输入错误");
        } catch (Exception ex) {
            UIUtil.showError(this, "查询失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void updateResultTable(List<Invoice> invoices) {
        String[] columns = {"发票号码", "患者ID", "金额", "状态", "创建时间"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Invoice inv : invoices) {
            model.addRow(new Object[]{
                    inv.getInvoiceNo(),
                    inv.getPatientId(),
                    inv.getAmount(),
                    inv.getStatus(),
                    inv.getCreateTime()
            });
        }

        resultTable.setModel(model);
    }

    private void resetCriteria() {
        tfInvoiceNo.setText("");
        tfPatientId.setText("");
        tfStartDate.setText("");
        tfEndDate.setText("");
        resultTable.setModel(new DefaultTableModel());
    }
}