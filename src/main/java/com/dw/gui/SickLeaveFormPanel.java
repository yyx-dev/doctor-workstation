package com.dw.gui;

import com.dw.dao.SickLeaveFormDao;
import com.dw.model.SickLeaveForm;
import com.dw.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class SickLeaveFormPanel extends JPanel {
    private JTextField patientIdField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextArea reasonTextArea;
    private JButton submitButton;
    private SickLeaveFormDao sickLeaveFormDao = new SickLeaveFormDao();

    public SickLeaveFormPanel() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel patientIdLabel = new JLabel("患者ID:");
        patientIdField = new JTextField(15);
        JLabel startDateLabel = new JLabel("开始日期:");
        startDateField = new JTextField(15);
        JLabel endDateLabel = new JLabel("结束日期:");
        endDateField = new JTextField(15);
        JLabel reasonLabel = new JLabel("病假原因:");
        reasonTextArea = new JTextArea(5, 15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(patientIdLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(patientIdField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(startDateLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(startDateField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(endDateLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(endDateField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(reasonLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(new JScrollPane(reasonTextArea), gbc);

        submitButton = new JButton("提交");
        gbc.gridx = 1;
        gbc.gridy = 4;
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
        Date startDate = UIUtil.parseDate(startDateField.getText());
        Date endDate = UIUtil.parseDate(endDateField.getText());
        String reason = reasonTextArea.getText();

        SickLeaveForm form = new SickLeaveForm();
        form.setPatientId(patientId);
        form.setStartDate(startDate);
        form.setEndDate(endDate);
        form.setReason(reason);

        int formId = sickLeaveFormDao.add(form);
        if (formId != -1) {
            UIUtil.showInfo(this, "病假单提交成功！", "成功");
        } else {
            UIUtil.showError(this, "病假单提交失败，请重试！", "错误");
        }
    }
}