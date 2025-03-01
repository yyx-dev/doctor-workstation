// RefundRequestPanel.java 退费申请界面
package com.dw.gui.panel.fee;

import com.dw.dao.invoice.InvoiceDao;
import com.dw.dao.fee.RefundRequestDao;
import com.dw.model.user.Doctor;
import com.dw.model.invoice.Invoice;
import com.dw.model.fee.RefundRequest;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;

public class RefundRequestPanel extends JPanel {
    private final Doctor doctor;
    private JTextField tfInvoiceNo;
    private JLabel lblPatientId;
    private JLabel lblOriginalAmount;
    private JTextField tfRefundAmount;
    private JTextArea taRemark;

    public RefundRequestPanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(UIUtil.createTitleLabel("退费申请"));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 主表单面板
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("退费信息"));

        // 原发票查询
        formPanel.add(UIUtil.createLabel("原发票号："));
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        tfInvoiceNo = UIUtil.createTextField();
        JButton btnSearch = UIUtil.createButton("查询");
        btnSearch.addActionListener(this::searchInvoice);
        searchPanel.add(tfInvoiceNo, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        formPanel.add(searchPanel);

        // 显示信息
        formPanel.add(UIUtil.createLabel("患者ID："));
        lblPatientId = UIUtil.createLabel("xx");
        formPanel.add(lblPatientId);

        formPanel.add(UIUtil.createLabel("原发票金额："));
        lblOriginalAmount = UIUtil.createLabel("xx");
        formPanel.add(lblOriginalAmount);

        // 退费金额
        formPanel.add(UIUtil.createLabel("退费金额："));
        tfRefundAmount = UIUtil.createTextField();
        formPanel.add(tfRefundAmount);

        // 退费原因
        formPanel.add(UIUtil.createLabel("退费原因："));
        taRemark = UIUtil.createTextArea(3, 20);
        formPanel.add(new JScrollPane(taRemark));

        // 操作按钮
        JButton btnSubmit = UIUtil.createButton("提交申请");
        btnSubmit.addActionListener(this::submitRequest);
        JButton btnReset = UIUtil.createButton("重置");
        btnReset.addActionListener(e -> resetForm());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSubmit);
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
            Invoice invoice = new InvoiceDao().getByInvoiceNo(invoiceNo);

            if (invoice == null) {
                UIUtil.showError(this, "发票不存在", "查询失败");
                return;
            }

            lblPatientId.setText(String.valueOf(invoice.getPatientId()));
            lblOriginalAmount.setText(invoice.getAmount() + " 元");
            tfRefundAmount.requestFocus();
        } catch (Exception ex) {
            UIUtil.showError(this, "查询失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void submitRequest(ActionEvent e) {
        try {
            BigDecimal refundAmount = new BigDecimal(tfRefundAmount.getText());
            BigDecimal originalAmount = new BigDecimal(lblOriginalAmount.getText().replace(" 元", ""));

            if (refundAmount.compareTo(originalAmount) > 0) {
                UIUtil.showError(this, "退费金额不能超过原发票金额", "数据校验失败");
                return;
            }

            RefundRequest request = new RefundRequest();
            request.setOriginalInvoiceNo(tfInvoiceNo.getText().trim());
            request.setPatientId(Integer.parseInt(lblPatientId.getText()));
            request.setAmount(refundAmount);
            request.setDoctorId(doctor.getId());
            request.setRemark(taRemark.getText());
            request.setRefundNo(generateRefundNo());

            int result = new RefundRequestDao().create(request);
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "退费申请已提交，编号：" + request.getRefundNo());
                resetForm();
            }
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "金额格式不正确", "输入错误");
        } catch (Exception ex) {
            UIUtil.showError(this, "提交失败：" + ex.getMessage(), "系统错误");
        }
    }

    private String generateRefundNo() {
        return "RF" + System.currentTimeMillis();
    }

    private void resetForm() {
        tfInvoiceNo.setText("");
        lblPatientId.setText("");
        lblOriginalAmount.setText("");
        tfRefundAmount.setText("");
        taRemark.setText("");
    }
}