package com.dw.gui;

import com.dw.dao.DiagnosisDocumentDao;
import com.dw.model.*;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class DiagnosisDocumentPanel extends JPanel {
    private JTextArea taDiagnosis;
    private JTextArea taTreatment;
    private JTextArea taMedication;
    private MedicalRecord record;
    private final Doctor doctor;

    public DiagnosisDocumentPanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板（顶部）
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(UIUtil.createTitleLabel("下诊断书"));

        // 主内容面板（使用GridBagLayout）
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第一行：患者信息
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(UIUtil.createLabel("患者姓名："), gbc);

        gbc.gridx = 1;
        JLabel lblPatient = new JLabel("Patientxxx");
        if (record != null) {
            lblPatient = UIUtil.createLabel(String.valueOf(record.getPatientId()));
        }
        lblPatient.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        contentPanel.add(lblPatient, gbc);

        // 第二行：诊断结论
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(UIUtil.createLabel("诊断结论："), gbc);

        gbc.gridx = 1;
        taDiagnosis = UIUtil.createTextArea(3, 30);
        contentPanel.add(new JScrollPane(taDiagnosis), gbc);

        // 第三行：治疗方案
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(UIUtil.createLabel("治疗方案："), gbc);

        gbc.gridx = 1;
        taTreatment = UIUtil.createTextArea(3, 30);
        contentPanel.add(new JScrollPane(taTreatment), gbc);

        // 第四行：处方药物
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(UIUtil.createLabel("处方药物："), gbc);

        gbc.gridx = 1;
        taMedication = UIUtil.createTextArea(3, 30);
        contentPanel.add(new JScrollPane(taMedication), gbc);

        // 第五行：保存按钮
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        JButton btnSave = UIUtil.createButton("签发诊断书");
        btnSave.addActionListener(e -> saveDocument());
        contentPanel.add(btnSave, gbc);

        // 组装主界面
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void saveDocument() {
        if (taDiagnosis.getText().trim().isEmpty()) {
            UIUtil.showError(this, "诊断结论不能为空", "输入错误");
            return;
        }

        try {
            DiagnosisDocument doc = new DiagnosisDocument();
            doc.setRecordId(record.getId());
            doc.setPatientId(record.getPatientId());
            doc.setDoctorId(doctor.getId());
            doc.setDiagnosis(taDiagnosis.getText());
            doc.setTreatment(taTreatment.getText());
            doc.setMedication(taMedication.getText());

            new DiagnosisDocumentDao().create(doc);
            UIUtil.showInfo(this, "诊断书签发成功", "操作成功");
            clearForm();
        } catch (SQLException ex) {
            UIUtil.showError(this, "保存失败：" + ex.getMessage(), "数据库错误");
        }
    }

    private void clearForm() {
        taDiagnosis.setText("");
        taTreatment.setText("");
        taMedication.setText("");
    }
}