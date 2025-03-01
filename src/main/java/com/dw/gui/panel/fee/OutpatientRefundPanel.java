// OutpatientRefundPanel.java 门诊退费界面
package com.dw.gui.panel.fee;


import com.dw.dao.fee.ChargeRecordDao;
import com.dw.dao.fee.RefundRequestDao;
import com.dw.dao.invoice.InvoiceDao;
import com.dw.dao.user.PatientDao;
import com.dw.model.fee.ChargeRecord;
import com.dw.model.fee.RefundRequest;
import com.dw.model.user.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.SQLException;

public class OutpatientRefundPanel extends JPanel {
    private final Doctor doctor;
    private JTextField tfChargeNo;
    private JLabel lblPatientInfo;
    private JLabel lblOriginalAmount;
    private JTextField tfRefundAmount;
    private JTextArea taReason;

    public OutpatientRefundPanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(UIUtil.createTitleLabel("门诊退费"));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 主表单面板
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("退费信息"));

        // 收费单号查询
        formPanel.add(UIUtil.createLabel("原收费单号："));
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        tfChargeNo = UIUtil.createTextField();
        JButton btnSearch = UIUtil.createButton("查询");
        btnSearch.addActionListener(this::searchCharge);
        searchPanel.add(tfChargeNo, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        formPanel.add(searchPanel);

        // 显示信息
        formPanel.add(UIUtil.createLabel("患者信息："));
        lblPatientInfo = UIUtil.createLabel("");
        formPanel.add(lblPatientInfo);

        formPanel.add(UIUtil.createLabel("原收费金额："));
        lblOriginalAmount = UIUtil.createLabel("");
        formPanel.add(lblOriginalAmount);

        // 退费金额
        formPanel.add(UIUtil.createLabel("退费金额："));
        tfRefundAmount = UIUtil.createTextField();
        formPanel.add(tfRefundAmount);

        // 退费原因
        formPanel.add(UIUtil.createLabel("退费原因："));
        taReason = UIUtil.createTextArea(3, 20);
        formPanel.add(new JScrollPane(taReason));

        // 操作按钮
        JButton btnRefund = UIUtil.createButton("执行退费");
        btnRefund.addActionListener(this::performRefund);
        JButton btnReset = UIUtil.createButton("重置");
        btnReset.addActionListener(e -> resetForm());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnRefund);
        buttonPanel.add(btnReset);

        // 组装界面
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void searchCharge(ActionEvent e) {
        try {
            String chargeNo = tfChargeNo.getText().trim();
            ChargeRecord record = new ChargeRecordDao().getByChargeNo(chargeNo);

            if (record == null) {
                UIUtil.showError(this, "收费记录不存在", "查询失败");
                return;
            }

            String patientInfo = new PatientDao().findById(record.getPatientId()).getName();
            lblPatientInfo.setText(patientInfo);
            lblOriginalAmount.setText(record.getAmount() + " 元");
            tfRefundAmount.requestFocus();
        } catch (Exception ex) {
            UIUtil.showError(this, "查询失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void performRefund(ActionEvent e) {
        try {
            String chargeNo = tfChargeNo.getText().trim();
            BigDecimal refundAmount = new BigDecimal(tfRefundAmount.getText());
            ChargeRecord original = new ChargeRecordDao().getByChargeNo(chargeNo);

            // 校验退费金额
            if (refundAmount.compareTo(original.getAmount()) > 0) {
                UIUtil.showError(this, "退费金额不能超过原金额", "数据校验失败");
                return;
            }

            // 创建退费记录
            RefundRequest request = new RefundRequest();
            request.setOriginalInvoiceNo(getRelatedInvoiceNo(chargeNo));
            request.setPatientId(original.getPatientId());
            request.setAmount(refundAmount);
            request.setDoctorId(doctor.getId());
            request.setRemark(taReason.getText());
            request.setRefundNo(generateRefundNo());

            // 执行退费操作
            new RefundRequestDao().create(request);
            JOptionPane.showMessageDialog(this, "退费申请已提交");
            resetForm();
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "金额格式不正确", "输入错误");
        } catch (Exception ex) {
            UIUtil.showError(this, "退费失败：" + ex.getMessage(), "系统错误");
        }
    }

    private String getRelatedInvoiceNo(String chargeNo) throws SQLException {
        // 根据收费单号获取关联发票号（需实现相关逻辑）
        return new InvoiceDao().getByChargeNo(chargeNo).getInvoiceNo();
    }

    private String generateRefundNo() {
        return "REF" + System.currentTimeMillis();
    }

    private void resetForm() {
        tfChargeNo.setText("");
        lblPatientInfo.setText("");
        lblOriginalAmount.setText("");
        tfRefundAmount.setText("");
        taReason.setText("");
    }
}