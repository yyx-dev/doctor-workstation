package com.dw.gui;

import com.dw.dao.MedicalCertificateDao;
import com.dw.model.MedicalCertificate;
import com.dw.model.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicalCertificatePanel extends JPanel {
    private final Doctor doctor;
    private JTable table;
    private CertTableModel tableModel;
    private JTextField tfAppointmentId;
    private JTextField tfPatientId;
    private JTextArea taDiagnosis;
    private JTextArea taTreatmentAdvice;

    public MedicalCertificatePanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(UIUtil.createTitleLabel("疾病证明单"));

        // Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // Appointment ID
        JLabel lAppointmentId = UIUtil.createLabel("预约ID：");
        tfAppointmentId = UIUtil.createTextField();
        addFormRow(infoPanel, gbc, 0, lAppointmentId, tfAppointmentId);

        // Patient ID
        JLabel lPatientId = UIUtil.createLabel("患者ID：");
        tfPatientId = UIUtil.createTextField();
        addFormRow(infoPanel, gbc, 1, lPatientId, tfPatientId);

        // Diagnosis
        JLabel lDiagnosis = UIUtil.createLabel("诊断结果：");
        taDiagnosis = UIUtil.createTextArea(3, 20);
        addFormRow(infoPanel, gbc, 2, lDiagnosis, new JScrollPane(taDiagnosis));

        // Treatment Advice
        JLabel lTreatmentAdvice = UIUtil.createLabel("治疗建议：");
        taTreatmentAdvice = UIUtil.createTextArea(5, 20);
        addFormRow(infoPanel, gbc, 3, lTreatmentAdvice, new JScrollPane(taTreatmentAdvice));

        // Save Button
        JButton btnSave = UIUtil.createButton("保存");
        btnSave.addActionListener(this::saveForm);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(btnSave, gbc);

        // Table Panel
        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("信息展示"));

        tableModel = new CertTableModel();
        table = new JTable(tableModel);
        table.setFont(UIUtil.NORMAL_FONT);
        table.setRowHeight(20);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        tablePanel.add(new JScrollPane(table), gbc);

        // Main Container
        JPanel centralPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centralPanel.setBackground(Color.WHITE);
        centralPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centralPanel.add(infoPanel);
        centralPanel.add(tablePanel);

        add(titlePanel, BorderLayout.NORTH);
        add(centralPanel, BorderLayout.CENTER);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, Component label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
    }

    public void refreshData() {
        loadData();
    }

    private void loadData() {
        try {
            List<MedicalCertificate> certs = new MedicalCertificateDao().findByDoctorId(doctor.getId());
            tableModel.setData(certs);
        } catch (SQLException e) {
            UIUtil.showError(this, "数据加载失败：" + e.getMessage(), "系统错误");
        }
    }

    private void saveForm(ActionEvent e) {
        try {
            MedicalCertificate cert = new MedicalCertificate();
            cert.setAppointmentId(Integer.parseInt(tfAppointmentId.getText()));
            cert.setPatientId(Integer.parseInt(tfPatientId.getText()));
            cert.setDoctorId(doctor.getId());
            cert.setDiagnosis(taDiagnosis.getText());
            cert.setTreatmentAdvice(taTreatmentAdvice.getText());

            new MedicalCertificateDao().add(cert);
            loadData();
            clearForm();
            UIUtil.showInfo(this, "疾病证明保存成功", "操作成功");
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "请输入有效的数字ID", "数据校验失败");
        } catch (SQLException ex) {
            UIUtil.showError(this, "数据库操作失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void clearForm() {
        tfAppointmentId.setText("");
        tfPatientId.setText("");
        taDiagnosis.setText("");
        taTreatmentAdvice.setText("");
    }

    class CertTableModel extends AbstractTableModel {
        private List<MedicalCertificate> data = new ArrayList<>();
        private final String[] COLUMNS = {"单据编号", "预约ID", "患者ID", "诊断结果", "创建时间"};

        public void setData(List<MedicalCertificate> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int col) { return COLUMNS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            MedicalCertificate cert = data.get(row);
            return switch(col) {
                case 0 -> cert.getId();
                case 1 -> cert.getAppointmentId();
                case 2 -> cert.getPatientId();
                case 3 -> cert.getDiagnosis();
                case 4 -> cert.getCreateTime();
                default -> null;
            };
        }
    }
}