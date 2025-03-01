// CreateRecordPanel.java 病案建立面板
package com.dw.gui;

import com.dw.dao.MedicalRecordDao;
import com.dw.model.Doctor;
import com.dw.model.MedicalRecord;
import com.dw.util.UIUtil;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;

public class CreateRecordPanel extends JPanel {
    private final JTextField tfPatientId = UIUtil.createTextField();
    private final DatePicker datePicker = new DatePicker();
    private final JTextArea taDiagnosis = UIUtil.createTextArea(3, 20);
    private final Doctor doctor;

    public CreateRecordPanel(Doctor doctor) {
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

        JLabel titleLabel = UIUtil.createTitleLabel("病案建立");
        titlePanel.add(titleLabel);

        // 创建信息面板
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("新建病案"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 患者ID输入行
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(UIUtil.createLabel("患者ID："), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfPatientId.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                try {
                    Integer.parseInt(tfPatientId.getText());
                    tfPatientId.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    return true;
                } catch (NumberFormatException e) {
                    tfPatientId.setBorder(BorderFactory.createLineBorder(Color.RED));
                    return false;
                }
            }
        });
        infoPanel.add(tfPatientId, gbc);

        // 入院日期行
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        infoPanel.add(UIUtil.createLabel("入院日期："), gbc);

        gbc.gridx = 1;
        infoPanel.add(datePicker, gbc);

        // 诊断结果行
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(UIUtil.createLabel("诊断结果："), gbc);

        gbc.gridx = 1;
        JScrollPane scrollPane = new JScrollPane(taDiagnosis);
        scrollPane.setPreferredSize(new Dimension(250, 80));
        infoPanel.add(scrollPane, gbc);

        // 保存按钮行
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        JButton btnSave = new JButton("保存病案");
        btnSave.addActionListener(e -> saveRecord());
        infoPanel.add(btnSave, gbc);

        // 将所有面板添加到主面板
        add(titlePanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
    }

    private void saveRecord() {
        try {
            MedicalRecord record = new MedicalRecord();
            record.setPatientId(Integer.parseInt(tfPatientId.getText()));
            record.setAdmissionDate(Date.valueOf(datePicker.getDate()));
            record.setDiagnosis(taDiagnosis.getText());

            new MedicalRecordDao().add(record);
            UIUtil.showInfo(this, "病案创建成功", "操作成功");
            clearForm();
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "患者ID必须为数字", "输入错误");
        } catch (SQLException ex) {
            UIUtil.showError(this, "保存失败：" + ex.getMessage(), "数据库错误");
        }
    }

    private void clearForm() {
        tfPatientId.setText("");
        datePicker.setDate(null);
        taDiagnosis.setText("");
    }
}
