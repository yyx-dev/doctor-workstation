package com.dw.gui;

import com.dw.dao.SickLeaveFormDao;
import com.dw.model.SickLeaveForm;
import com.dw.model.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SickLeaveFormPanel extends JPanel {
    private final Doctor doctor;
    private JTable table;
    private SickLeaveTableModel tableModel;
    private JTextField tfAppointmentId;
    private JTextField tfPatientId;
    private JSpinner spDays;
    private JTextArea taMedicalAdvice;

    public SickLeaveFormPanel(Doctor doctor) {
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
        titlePanel.add(UIUtil.createTitleLabel("病假单"));

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

        // Days
        JLabel lDays = UIUtil.createLabel("病假天数：");
        spDays = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        UIUtil.setDefaultStyle(spDays);
        addFormRow(infoPanel, gbc, 2, lDays, spDays);

        // Medical Advice
        JLabel lMedicalAdvice = UIUtil.createLabel("医嘱说明：");
        taMedicalAdvice = UIUtil.createTextArea(3, 20);
        addFormRow(infoPanel, gbc, 3, lMedicalAdvice, new JScrollPane(taMedicalAdvice));

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

        tableModel = new SickLeaveTableModel();
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
            List<SickLeaveForm> forms = new SickLeaveFormDao().findByDoctorId(doctor.getId());
            tableModel.setData(forms);
        } catch (SQLException e) {
            UIUtil.showError(this, "数据加载失败：" + e.getMessage(), "系统错误");
        }
    }

    private void saveForm(ActionEvent e) {
        try {
            SickLeaveForm form = new SickLeaveForm();
            form.setAppointmentId(Integer.parseInt(tfAppointmentId.getText()));
            form.setPatientId(Integer.parseInt(tfPatientId.getText()));
            form.setDoctorId(doctor.getId());
            form.setDays((Integer)spDays.getValue());
            form.setMedicalAdvice(taMedicalAdvice.getText());

            new SickLeaveFormDao().add(form);
            loadData();
            clearForm();
            UIUtil.showInfo(this, "病假单保存成功", "操作成功");
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "请输入有效的数字ID", "数据校验失败");
        } catch (SQLException ex) {
            UIUtil.showError(this, "数据库操作失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void clearForm() {
        tfAppointmentId.setText("");
        tfPatientId.setText("");
        spDays.setValue(1);
        taMedicalAdvice.setText("");
    }

    class SickLeaveTableModel extends AbstractTableModel {
        private List<SickLeaveForm> data = new ArrayList<>();
        private final String[] COLUMNS = {"单据编号", "预约ID", "患者ID", "病假天数", "创建时间"};

        public void setData(List<SickLeaveForm> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int col) { return COLUMNS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            SickLeaveForm form = data.get(row);
            return switch(col) {
                case 0 -> form.getId();
                case 1 -> form.getAppointmentId();
                case 2 -> form.getPatientId();
                case 3 -> form.getDays();
                case 4 -> form.getCreateTime();
                default -> null;
            };
        }
    }
}