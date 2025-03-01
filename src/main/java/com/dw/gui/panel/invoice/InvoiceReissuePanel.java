// InvoiceReissuePanel.java 补打发票界面
package com.dw.gui.panel.invoice;

import com.dw.dao.invoice.InvoiceDao;
import com.dw.model.invoice.Invoice;
import com.dw.model.user.Doctor;
import com.dw.util.PDFUtil;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class InvoiceReissuePanel extends JPanel {
    private final Doctor doctor;
    private JTextField tfOriginalNo;
    private JTextField tfNewNo;
    private JLabel lblPatientId;
    private JLabel lblOriginalAmount;
    private JTextArea taRemark;
    private Invoice originalInvoice;

    public InvoiceReissuePanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(UIUtil.createTitleLabel("发票补打"));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 主表单面板
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("补打信息"));

        // 原发票号查询
        formPanel.add(UIUtil.createLabel("原发票号："));
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        tfOriginalNo = UIUtil.createTextField();
        JButton btnSearch = UIUtil.createButton("查询");
        btnSearch.addActionListener(this::searchOriginal);
        searchPanel.add(tfOriginalNo, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);
        formPanel.add(searchPanel);

        // 显示信息
        formPanel.add(UIUtil.createLabel("患者ID："));
        lblPatientId = UIUtil.createLabel("xx");
        formPanel.add(lblPatientId);

        formPanel.add(UIUtil.createLabel("原发票金额："));
        lblOriginalAmount = UIUtil.createLabel("xx");
        formPanel.add(lblOriginalAmount);

        // 新发票号
        formPanel.add(UIUtil.createLabel("新发票号："));
        tfNewNo = UIUtil.createTextField();
        formPanel.add(tfNewNo);

        // 补打备注
        formPanel.add(UIUtil.createLabel("补打原因："));
        taRemark = UIUtil.createTextArea(3, 20);
        formPanel.add(new JScrollPane(taRemark));

        // 操作按钮
        JButton btnReissue = UIUtil.createButton("补打发票");
        btnReissue.addActionListener(this::reissueInvoice);
        JButton btnReset = UIUtil.createButton("重置");
        btnReset.addActionListener(e -> resetForm());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnReissue);
        buttonPanel.add(btnReset);

        // 组装界面
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void searchOriginal(ActionEvent e) {
        try {
            String invoiceNo = tfOriginalNo.getText().trim();
            originalInvoice = new InvoiceDao().getByInvoiceNo(invoiceNo);

            if (originalInvoice == null) {
                UIUtil.showError(this, "发票不存在", "查询失败");
                return;
            }

            if ("VOID".equals(originalInvoice.getStatus())) {
                UIUtil.showError(this, "该发票已作废，不能补打", "操作禁止");
                return;
            }

            lblPatientId.setText(String.valueOf(originalInvoice.getPatientId()));
            lblOriginalAmount.setText(originalInvoice.getAmount() + " 元");
            tfNewNo.requestFocus();
        } catch (SQLException ex) {
            UIUtil.showError(this, "数据库查询失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void reissueInvoice(ActionEvent e) {
        try {
            if (originalInvoice == null) {
                UIUtil.showError(this, "请先查询原发票信息", "数据校验失败");
                return;
            }

            if (tfNewNo.getText().trim().isEmpty()) {
                UIUtil.showError(this, "新发票号不能为空", "输入错误");
                return;
            }

            // 生成新PDF
            String newPdfPath = PDFUtil.generateInvoicePDF(originalInvoice,
                    "补打原因：" + taRemark.getText() + "\n原发票号：" + originalInvoice.getInvoiceNo());

            // 创建新发票记录
            Invoice newInvoice = new Invoice();
            newInvoice.setInvoiceNo(tfNewNo.getText().trim());
            newInvoice.setPatientId(originalInvoice.getPatientId());
            newInvoice.setDoctorId(doctor.getId());
            newInvoice.setAmount(originalInvoice.getAmount());
            newInvoice.setStatus("REISSUED");
            newInvoice.setPdfPath(newPdfPath);

            // 事务处理
            boolean success = new InvoiceDao().reissueInvoice(originalInvoice.getId(), newInvoice);

            if (success) {
                JOptionPane.showMessageDialog(this, "补打成功\n新发票路径：" + newPdfPath);
                resetForm();
            }
        } catch (Exception ex) {
            UIUtil.showError(this, "补打失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void resetForm() {
        tfOriginalNo.setText("");
        tfNewNo.setText("");
        lblPatientId.setText("");
        lblOriginalAmount.setText("");
        taRemark.setText("");
        originalInvoice = null;
    }
}