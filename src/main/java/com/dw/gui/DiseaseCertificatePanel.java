package com.dw.gui;

import com.dw.dao.DiseaseCertificateDao;
import com.dw.model.DiseaseCertificate;
import com.dw.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class DiseaseCertificatePanel extends JPanel {
    private JTextField patientIdField;
    private JTextField diagnosisField;
    private JTextField doctorNameField;
    private JButton submitButton;
    private DiseaseCertificateDao diseaseCertificateDao = new DiseaseCertificateDao();

    public DiseaseCertificatePanel() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientIdLabel = new JLabel("患者ID:");
        patientIdField = new JTextField(15);
        JLabel diagnosisLabel = new JLabel("诊断:");
        diagnosisField = new JTextField(15);
        JLabel doctorNameLabel = new JLabel("医生姓名:");
        doctorNameField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(patientIdLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(patientIdField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(diagnosisLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(diagnosisField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(doctorNameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(doctorNameField, gbc);

        submitButton = new JButton("提交");
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(submitButton, gbc);

        add(formPanel, BorderLayout.CENTER);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        int patientId = Integer.parseInt(patientIdField.getText());
        String diagnosis = diagnosisField.getText();
        String doctorName = doctorNameField.getText();
        Date issueDate = new Date();

        DiseaseCertificate certificate = new DiseaseCertificate();
        certificate.setPatientId(patientId);
        certificate.setDiagnosis(diagnosis);
        certificate.setDoctorName(doctorName);
        certificate.setIssueDate(issueDate);

        int certificateId = diseaseCertificateDao.add(certificate);
        if (certificateId != -1) {
            UIUtil.showInfo(this, "疾病证明提交成功！", "成功");
        } else {
            UIUtil.showError(this, "疾病证明提交失败，请重试！", "错误");
        }
    }
}