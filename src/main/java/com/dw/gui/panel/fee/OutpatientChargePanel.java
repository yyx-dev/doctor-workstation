// OutpatientChargePanel.java 门诊收费界面
package com.dw.gui.panel.fee;

import com.dw.dao.fee.ChargeDao;
import com.dw.dao.user.PatientDao;
import com.dw.model.fee.ChargeRecord;
import com.dw.model.user.Doctor;
import com.dw.model.invoice.Invoice;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.SQLException;

public class OutpatientChargePanel extends JPanel {
    private final Doctor doctor;
    private JTextField tfPatientId;
    private JLabel lblPatientName;
    private JTextField tfAmount;
    private JComboBox<String> cbChargeType;
    private JComboBox<String> cbPaymentMethod;
    private JTextArea taRemark;

    public OutpatientChargePanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(UIUtil.createTitleLabel("门诊收费"));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 主表单面板
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("收费信息"));

        // 患者信息
        formPanel.add(UIUtil.createLabel("患者ID："));
        JPanel patientPanel = new JPanel(new BorderLayout(5, 0));
        tfPatientId = UIUtil.createTextField();
        JButton btnVerify = UIUtil.createButton("验证");
        btnVerify.addActionListener(this::verifyPatient);
        patientPanel.add(tfPatientId, BorderLayout.CENTER);
        patientPanel.add(btnVerify, BorderLayout.EAST);
        formPanel.add(patientPanel);

        formPanel.add(UIUtil.createLabel("患者姓名："));
        lblPatientName = UIUtil.createLabel("");
        formPanel.add(lblPatientName);

        // 收费信息
        formPanel.add(UIUtil.createLabel("收费金额："));
        tfAmount = UIUtil.createTextField();
        formPanel.add(tfAmount);

        formPanel.add(UIUtil.createLabel("收费类型："));
        cbChargeType = UIUtil.createComboBox(new String[]{"普通门诊", "专家门诊", "急诊"});
        formPanel.add(cbChargeType);

        formPanel.add(UIUtil.createLabel("支付方式："));
        cbPaymentMethod = UIUtil.createComboBox(new String[]{"现金", "支付宝", "微信支付"});
        formPanel.add(cbPaymentMethod);

        // 备注信息
        formPanel.add(UIUtil.createLabel("备注："));
        taRemark = UIUtil.createTextArea(3, 20);
        formPanel.add(new JScrollPane(taRemark));

        // 操作按钮
        JButton btnCharge = UIUtil.createButton("确认收费");
        btnCharge.addActionListener(this::performCharge);
        JButton btnReset = UIUtil.createButton("重置");
        btnReset.addActionListener(e -> resetForm());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnCharge);
        buttonPanel.add(btnReset);

        // 组装界面
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void verifyPatient(ActionEvent e) {
        try {
            int patientId = Integer.parseInt(tfPatientId.getText());
            String patientName = new PatientDao().findById(patientId).getName();
            lblPatientName.setText(patientName);
            tfAmount.requestFocus();
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "患者ID必须为数字", "输入错误");
        } catch (Exception ex) {
            UIUtil.showError(this, "患者信息验证失败", "数据错误");
        }
    }

    // OutpatientChargePanel.java 重构后的收费方法
    private void performCharge(ActionEvent e) {
        try {
            // 创建业务对象
            ChargeRecord record = createChargeRecord();
            Invoice invoice = createInvoice(record);

            // 调用服务类处理事务
            new ChargeDao().createChargeWithInvoice(record, invoice);

            JOptionPane.showMessageDialog(this, "收费成功！发票号：" + invoice.getInvoiceNo());
            resetForm();
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "金额格式不正确", "输入错误");
        } catch (SQLException ex) {
            UIUtil.showError(this, "数据库操作失败：" + ex.getMessage(), "系统错误");
        } catch (Exception ex) {
            UIUtil.showError(this, "收费失败：" + ex.getMessage(), "系统错误");
        }
    }

    private ChargeRecord createChargeRecord() {
        ChargeRecord record = new ChargeRecord();
        record.setPatientId(Integer.parseInt(tfPatientId.getText()));
        record.setDoctorId(doctor.getId());
        record.setAmount(new BigDecimal(tfAmount.getText()));
        record.setChargeType((String) cbChargeType.getSelectedItem());
        record.setPaymentMethod((String) cbPaymentMethod.getSelectedItem());
        record.setRemark(taRemark.getText());
        record.setChargeNo(generateChargeNo());
        return record;
    }

    private Invoice createInvoice(ChargeRecord record) {
        Invoice invoice = new Invoice();
        invoice.setPatientId(record.getPatientId());
        invoice.setDoctorId(record.getDoctorId());
        invoice.setAmount(record.getAmount());
        invoice.setInvoiceNo(generateInvoiceNo());
        invoice.setStatus("NORMAL");
        return invoice;
    }

    private String generateChargeNo() {
        return "CH" + System.currentTimeMillis();
    }

    private String generateInvoiceNo() {
        return "INV" + System.currentTimeMillis();
    }

    private void resetForm() {
        tfPatientId.setText("");
        lblPatientName.setText("");
        tfAmount.setText("");
        cbChargeType.setSelectedIndex(0);
        cbPaymentMethod.setSelectedIndex(0);
        taRemark.setText("");
    }
}