// InvoicePanel.java 发票打印界面
package com.dw.gui.panel.invoice;

import com.dw.dao.invoice.InvoiceDao;
import com.dw.dao.user.PatientDao;
import com.dw.model.invoice.Invoice;
import com.dw.model.user.Doctor;
import com.dw.util.PDFUtil;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;

public class InvoicePanel extends JPanel {
    private final Doctor doctor;
    private JTextField tfPatientId;
    private JTextField tfAmount;
    private JTextField tfInvoiceNo;
    private JTextArea taRemarks;

    public InvoicePanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 创建面板标题
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = UIUtil.createTitleLabel("发票打印");
        titlePanel.add(titleLabel);

        // 表单面板（使用5行2列的网格布局）
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));

        // 患者ID
        infoPanel.add(UIUtil.createLabel("患者ID："));
        tfPatientId = UIUtil.createTextField();
        infoPanel.add(tfPatientId);

        // 发票金额
        infoPanel.add(UIUtil.createLabel("金额（元）："));
        tfAmount = UIUtil.createTextField();
        infoPanel.add(tfAmount);

        // 发票号码
        infoPanel.add(UIUtil.createLabel("发票号码："));
        tfInvoiceNo = UIUtil.createTextField();
        infoPanel.add(tfInvoiceNo);

        // 备注信息
        infoPanel.add(UIUtil.createLabel("备注："));
        taRemarks = UIUtil.createTextArea(3, 20);
        infoPanel.add(new JScrollPane(taRemarks));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton btnGenerate = UIUtil.createButton("生成发票");
        JButton btnClear = UIUtil.createButton("清空重填");
        btnGenerate.addActionListener(e -> generateInvoice());
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnGenerate);
        buttonPanel.add(btnClear);

        JPanel centralPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centralPanel.setBackground(Color.WHITE);
        centralPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centralPanel.add(infoPanel, BorderLayout.CENTER);
        centralPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 组装主界面
        add(titlePanel, BorderLayout.NORTH);
        add(centralPanel, BorderLayout.CENTER);
    }

    private void generateInvoice() {
        try {
            // 验证患者存在性
            int patientId = Integer.parseInt(tfPatientId.getText());
            if (new PatientDao().findById(patientId) == null) {
                JOptionPane.showMessageDialog(this, "患者ID不存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 创建发票对象
            Invoice invoice = new Invoice();
            invoice.setPatientId(patientId);
            invoice.setDoctorId(doctor.getId());
            invoice.setAmount(new BigDecimal(tfAmount.getText()));
            invoice.setInvoiceNo(tfInvoiceNo.getText().trim());

            // 生成PDF文件
            String pdfPath = PDFUtil.generateInvoicePDF(invoice, taRemarks.getText());
            invoice.setPdfPath(pdfPath);

            // 保存到数据库
            int invoiceId = new InvoiceDao().createInvoice(invoice);
            if (invoiceId > 0) {
                JOptionPane.showMessageDialog(this, "发票生成成功\n文件路径：" + pdfPath);
                clearForm();
            }
        } catch (NumberFormatException e) {
            UIUtil.showError(this, "金额格式不正确", "系统错误");
        } catch (SQLException e) {
            UIUtil.showError(this, "数据库操作失败：" + e.getMessage(), "数据校验失败");
        } catch (Exception e) {
            UIUtil.showError(this, "PDF生成失败：" + e.getMessage(), "PDF生成失败");
        }
    }

    private void clearForm() {
        tfPatientId.setText("");
        tfAmount.setText("");
        tfInvoiceNo.setText("");
        taRemarks.setText("");
    }
}