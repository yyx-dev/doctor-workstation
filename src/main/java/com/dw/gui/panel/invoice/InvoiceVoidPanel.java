// InvoiceVoidPanel.java 发票作废界面
package com.dw.gui.panel.invoice;

import com.dw.dao.invoice.InvoiceDao;
import com.dw.model.invoice.Invoice;
import com.dw.model.user.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class InvoiceVoidPanel extends JPanel {
    private final Doctor doctor;
    private JTextField tfInvoiceNo;
    private JLabel lblPatientId;
    private JLabel lblAmount;
    private JLabel lblStatus;
    private JTextArea taVoidReason;
    private Invoice targetInvoice;

    public InvoiceVoidPanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(UIUtil.createTitleLabel("发票作废"));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 主表单面板
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("作废信息"));

        // 发票号查询
        formPanel.add(UIUtil.createLabel("发票号码："));
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        tfInvoiceNo = UIUtil.createTextField();
        JButton btnSearch = UIUtil.createButton("查询");
        btnSearch.addActionListener(this::searchInvoice);
        searchPanel.add(tfInvoiceNo, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        formPanel.add(searchPanel);

        // 显示信息
        formPanel.add(UIUtil.createLabel("患者ID："));
        lblPatientId = UIUtil.createLabel("");
        formPanel.add(lblPatientId);

        formPanel.add(UIUtil.createLabel("发票金额："));
        lblAmount = UIUtil.createLabel("");
        formPanel.add(lblAmount);

        formPanel.add(UIUtil.createLabel("当前状态："));
        lblStatus = UIUtil.createLabel("");
        formPanel.add(lblStatus);

        // 作废原因
        formPanel.add(UIUtil.createLabel("作废原因："));
        taVoidReason = UIUtil.createTextArea(3, 20);
        formPanel.add(new JScrollPane(taVoidReason));

        // 操作按钮
        JButton btnVoid = UIUtil.createButton("确认作废");
        btnVoid.addActionListener(this::performVoid);
        JButton btnReset = UIUtil.createButton("重置");
        btnReset.addActionListener(e -> resetForm());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnVoid);
        buttonPanel.add(btnReset);

        // 组装界面
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void searchInvoice(ActionEvent e) {
        try {
            String invoiceNo = tfInvoiceNo.getText().trim();
            targetInvoice = new InvoiceDao().getByInvoiceNo(invoiceNo);

            if (targetInvoice == null) {
                UIUtil.showError(this, "发票不存在", "查询失败");
                return;
            }

            if ("VOID".equals(targetInvoice.getStatus())) {
                UIUtil.showError(this, "该发票已作废", "操作禁止");
                return;
            }

            updateDisplayInfo();
            taVoidReason.requestFocus();
        } catch (SQLException ex) {
            UIUtil.showError(this, "数据库查询失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void updateDisplayInfo() {
        lblPatientId.setText(String.valueOf(targetInvoice.getPatientId()));
        lblAmount.setText(targetInvoice.getAmount() + " 元");
        lblStatus.setText(targetInvoice.getStatus());
    }

    private void performVoid(ActionEvent e) {
        try {
            if (targetInvoice == null) {
                UIUtil.showError(this, "请先查询要作废的发票", "数据校验失败");
                return;
            }

            if (taVoidReason.getText().trim().isEmpty()) {
                UIUtil.showError(this, "必须填写作废原因", "输入错误");
                return;
            }

            boolean success = new InvoiceDao().voidInvoice(
                    targetInvoice.getId(),
                    doctor.getId(),
                    taVoidReason.getText().trim()
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "发票作废成功", "操作成功", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
            }
        } catch (SQLException ex) {
            UIUtil.showError(this, "作废操作失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void resetForm() {
        tfInvoiceNo.setText("");
        lblPatientId.setText("");
        lblAmount.setText("");
        lblStatus.setText("");
        taVoidReason.setText("");
        targetInvoice = null;
    }
}