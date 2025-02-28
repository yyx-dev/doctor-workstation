package com.dw.gui;

import com.dw.dao.AdmissionFormDao;
import com.dw.model.AdmissionForm;
import com.dw.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class AdmissionFormPanel extends JPanel {
    private JTextField patientIdField;
    private JTextField departmentField;
    private JTextArea reasonTextArea;
    private JButton submitButton;
    private AdmissionFormDao admissionFormDao = new AdmissionFormDao();

    public AdmissionFormPanel() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientIdLabel = new JLabel("患者ID:");
        patientIdField = new JTextField(15);
        JLabel departmentLabel = new JLabel("科室:");
        departmentField = new JTextField(15);
        JLabel reasonLabel = new JLabel("入院原因:");
        reasonTextArea = new JTextArea(5, 15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(patientIdLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(patientIdField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(departmentLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(departmentField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(reasonLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(new JScrollPane(reasonTextArea), gbc);

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
        String department = departmentField.getText();
        String reason = reasonTextArea.getText();
        Date admissionDate = new Date();

        AdmissionForm form = new AdmissionForm();
        form.setPatientId(patientId);
        form.setDepartment(department);
        form.setReason(reason);
        form.setAdmissionDate(admissionDate);

        int formId = admissionFormDao.add(form);
        if (formId != -1) {
            UIUtil.showInfo(this, "入院单提交成功！", "成功");
        } else {
            UIUtil.showError(this, "入院单提交失败，请重试！", "错误");
        }
    }
}