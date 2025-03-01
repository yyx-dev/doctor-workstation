// DiagnosisCertPanel.java
package com.dw.gui.panel.other;

import com.dw.dao.other.DiagnosisCertDao;
import com.dw.dao.other.DiagnosisTemplateDao;
import com.dw.model.other.DiagnosisCertificate;
import com.dw.model.other.DiagnosisTemplate;
import com.dw.model.user.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class DiagnosisCertPanel extends JPanel {
    private final Doctor doctor;
    private JTextField tfRecordId;
    private JTextArea taDiagnosis;
    private JTextArea taTreatment;
    private JTextArea taMedication;
    private JTextArea taFollowUp;
    private JComboBox<String> templateCombo;

    public DiagnosisCertPanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 模板选择面板
        JPanel templatePanel = createTemplatePanel();

        // 表单面板
        JPanel formPanel = createFormPanel();

        // 操作按钮
        JButton btnSave = UIUtil.createButton("签发诊断书");
        btnSave.addActionListener(this::saveCertificate);

        add(templatePanel, BorderLayout.NORTH);
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(btnSave, BorderLayout.SOUTH);
    }

    private JPanel createTemplatePanel() {
        JPanel panel = UIUtil.createPanel();
        panel.add(UIUtil.createLabel("使用模板："));

        templateCombo = new JComboBox<>();
        templateCombo.addItem("-- 选择模板 --");
        try {
            new DiagnosisTemplateDao()
                    .findByDepartment(doctor.getDepartment())
                    .forEach(t -> templateCombo.addItem(t.getTemplateName()));
        } catch (SQLException e) {
            UIUtil.showError(this, "加载模板失败", "系统错误");
        }
        templateCombo.addActionListener(e -> applyTemplate());
        panel.add(templateCombo);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = UIUtil.createPanel();
        panel.setLayout(new GridLayout(0, 1, 10, 10));

        // 病历号
        panel.add(UIUtil.createLabel("关联病历号："));
        tfRecordId = UIUtil.createTextField();
        panel.add(tfRecordId);

        // 诊断详情
        panel.add(UIUtil.createLabel("诊断详情："));
        taDiagnosis = UIUtil.createTextArea(5, 60);
        panel.add(new JScrollPane(taDiagnosis));

        // 治疗方案
        panel.add(UIUtil.createLabel("治疗方案："));
        taTreatment = UIUtil.createTextArea(5, 60);
        panel.add(new JScrollPane(taTreatment));

        // 用药建议
        panel.add(UIUtil.createLabel("用药建议："));
        taMedication = UIUtil.createTextArea(3, 60);
        panel.add(new JScrollPane(taMedication));

        // 随访要求
        panel.add(UIUtil.createLabel("随访要求："));
        taFollowUp = UIUtil.createTextArea(3, 60);
        panel.add(new JScrollPane(taFollowUp));

        return panel;
    }

    private void applyTemplate() {
        String selected = (String) templateCombo.getSelectedItem();
        if (selected == null || selected.startsWith("--")) return;

        try {
            DiagnosisTemplate template = new DiagnosisTemplateDao()
                    .findByDepartment(doctor.getDepartment()).stream()
                    .filter(t -> t.getTemplateName().equals(selected))
                    .findFirst().orElse(null);

            if (template != null) {
                taDiagnosis.setText(template.getContent());
            }
        } catch (SQLException ex) {
            UIUtil.showError(this, "模板加载失败", "系统错误");
        }
    }

    private void saveCertificate(ActionEvent e) {
        try {
            DiagnosisCertificate cert = new DiagnosisCertificate();
            cert.setRecordId(Integer.parseInt(tfRecordId.getText()));
            cert.setDoctorId(doctor.getId());
            cert.setDiagnosisDetails(taDiagnosis.getText());
            cert.setTreatmentPlan(taTreatment.getText());
            cert.setMedication(taMedication.getText());
            cert.setFollowUp(taFollowUp.getText());
            cert.setIssueDate(Date.valueOf(LocalDate.now()));
            cert.setSignature(doctor.getName() + " 签名");

            int certId = new DiagnosisCertDao().createCertificate(cert);
            UIUtil.showInfo(this, "诊断书已签发，编号：" + certId, "操作成功");
            clearForm();
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "病历号必须为数字", "输入错误");
        } catch (SQLException ex) {
            UIUtil.showError(this, "保存失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void clearForm() {
        tfRecordId.setText("");
        taDiagnosis.setText("");
        taTreatment.setText("");
        taMedication.setText("");
        taFollowUp.setText("");
        templateCombo.setSelectedIndex(0);
    }
}