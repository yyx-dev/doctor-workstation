package com.dw.gui.panel.form;

import com.dw.dao.form.AdmissionFormDao;
import com.dw.model.form.AdmissionForm;
import com.dw.model.user.Doctor;
import com.dw.util.UIUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdmissionFormPanel extends JPanel {

    private final Doctor doctor;

    private JTable table;
    private AdmissionFormTableModel tableModel;

    private JTextField tfAppointmentId;
    private JTextField tfPatientId;
    private JTextField tfDepartment;

    private JTextArea taDiagnosis;
    private JTextArea taAdmissionReason;

    JButton btnSave;

    public AdmissionFormPanel(Doctor doctor) {
        this.doctor = doctor;

        initUI();

        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 创建面板标题
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = UIUtil.createTitleLabel("入院单");
        titlePanel.add(titleLabel);

        // 创建信息面板
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        // 预约ID标签和输入框
        JLabel lAppointmentId = UIUtil.createLabel("预约ID：");
        tfAppointmentId = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        infoPanel.add(lAppointmentId, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        infoPanel.add(tfAppointmentId, gbc);

        // 患者ID标签和输入框
        JLabel lPatientId = UIUtil.createLabel("患者ID：");
        tfPatientId = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        infoPanel.add(lPatientId, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        infoPanel.add(tfPatientId, gbc);

        // 科室标签和输入框
        JLabel lDepartment = UIUtil.createLabel("科室：");
        tfDepartment = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        infoPanel.add(lDepartment, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        infoPanel.add(tfDepartment, gbc);

        // 入院理由标签和输入框
        JLabel lAdmissionReason = UIUtil.createLabel("入院理由：");
        taAdmissionReason = UIUtil.createTextArea(3, 10);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        infoPanel.add(lAdmissionReason, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        infoPanel.add(taAdmissionReason, gbc);

        // 诊断结果标签和输入框
        JLabel lDiagnosis = UIUtil.createLabel("诊断结果：");
        taDiagnosis = UIUtil.createTextArea(3, 10);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        infoPanel.add(lDiagnosis, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        infoPanel.add(taDiagnosis, gbc);

        // 保存按钮
        btnSave = UIUtil.createButton("保存");
        btnSave.addActionListener(this::saveForm);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(btnSave, gbc);

        // 创建展示面板
        JPanel showPanel = new JPanel(new GridBagLayout());
        showPanel.setBackground(Color.WHITE);
        showPanel.setBorder(BorderFactory.createTitledBorder("信息展示"));

        tableModel = new AdmissionFormTableModel();
        table = new JTable(tableModel);
        table.setFont(UIUtil.NORMAL_FONT);
        table.setRowHeight(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        showPanel.add(new JScrollPane(table), gbc);

        // 将信息面板和展示面板添加到主面板
        JPanel centralPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centralPanel.setBackground(Color.WHITE);
        centralPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centralPanel.add(infoPanel, BorderLayout.NORTH);
        centralPanel.add(showPanel, BorderLayout.CENTER);

        // 将所有面板添加到主面板
        add(titlePanel, BorderLayout.NORTH);
        add(centralPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        loadData();
    }

    private void loadData() {
        try {
            List<AdmissionForm> forms = new AdmissionFormDao().findByDoctorId(doctor.getId());
            tableModel.setData(forms);
        } catch (SQLException e) {
            UIUtil.showError(this, "数据加载失败：" + e.getMessage(), "系统错误");
        }
    }

    private void saveForm(ActionEvent e) {
        try {
            AdmissionForm form = new AdmissionForm();
            form.setAppointmentId(Integer.parseInt(tfAppointmentId.getText()));
            form.setPatientId(Integer.parseInt(tfPatientId.getText()));
            form.setDoctorId(doctor.getId());
            form.setDepartment(tfDepartment.getText());
            form.setDiagnosis(taDiagnosis.getText());
            form.setAdmissionReason(taAdmissionReason.getText());

            new AdmissionFormDao().add(form);
            loadData();
            clearForm();
            UIUtil.showInfo(this, "入院单保存成功", "操作成功");
        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "请输入有效的数字ID", "数据校验失败");
        } catch (SQLException ex) {
            UIUtil.showError(this, "数据库操作失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void clearForm() {
        tfAppointmentId.setText("");
        tfPatientId.setText("");
        tfDepartment.setText("");
        taDiagnosis.setText("");
        taAdmissionReason.setText("");
    }

    static class AdmissionFormTableModel extends AbstractTableModel {
        private List<AdmissionForm> data = new ArrayList<>();
        private final String[] COLUMNS = {"单据编号", "预约ID", "患者ID", "接诊科室", "入院时间", "入院理由"};

        public void setData(List<AdmissionForm> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Object getValueAt(int row, int column) {
            AdmissionForm form = data.get(row);
            return switch (column) {
                case 0 -> form.getId();
                case 1 -> form.getAppointmentId();
                case 2 -> form.getPatientId();
                case 3 -> form.getDepartment();
                case 4 -> form.getCreateTime();
                case 5 -> form.getAdmissionReason();
                default -> null;
            };
        }
    }
}