package com.dw.gui;

import com.dw.dao.MedicalRecordDao;
import com.dw.dao.PatientDao;
import com.dw.model.Appointment;
import com.dw.model.Doctor;
import com.dw.model.MedicalRecord;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class WriteRecordPanel extends JPanel {

    private final JTextArea taHistory = UIUtil.createTextArea(3, 30);
    private final JTextArea taDiagnosis = UIUtil.createTextArea(3, 30);

    private Appointment appointment = new Appointment();
    private Doctor doctor;

    public WriteRecordPanel(Doctor doctor) {
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

        JLabel titleLabel = UIUtil.createTitleLabel("病历书写");
        titlePanel.add(titleLabel);

        // 创建信息面板
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(createLabel("患者姓名："), gbc);

        gbc.gridx = 1;
        infoPanel.add(createValueLabel("1xxx"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(createLabel("主诉症状："), gbc);

        gbc.gridx = 1;
        infoPanel.add(createValueLabel(appointment.getChiefComplaint()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(createLabel("现病史："), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(new JScrollPane(taHistory), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        infoPanel.add(createLabel("初步诊断："), gbc);

        gbc.gridx = 1;
        infoPanel.add(new JScrollPane(taDiagnosis), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        JButton btnSave = UIUtil.createButton("保存病历");
        btnSave.addActionListener(e -> saveRecord());
        infoPanel.add(btnSave, gbc);

        // 将所有面板添加到主面板
        add(titlePanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIUtil.NORMAL_FONT);
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIUtil.NORMAL_FONT);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        label.setPreferredSize(new Dimension(200, 25));
        return label;
    }

    private void saveRecord() {
        if (taDiagnosis.getText().trim().isEmpty()) {
            UIUtil.showError(this, "诊断结果不能为空", "数据错误");
            return;
        }

        try {
            MedicalRecord record = new MedicalRecord();
            record.setAppointmentId(appointment.getId());
            record.setPatientId(appointment.getPatientId());
            record.setDiagnosis(taDiagnosis.getText());

            new MedicalRecordDao().add(record);
            UIUtil.showInfo(this, "病历保存成功", "操作成功");
        } catch (SQLException ex) {
            UIUtil.showError(this, "保存失败：" + ex.getMessage(), "数据库错误");
        }
    }
}