package com.dw.gui;

import com.dw.dao.AppointmentDao;
import com.dw.model.Appointment;
import com.dw.model.Doctor;
import com.dw.util.UIUtil;
import lombok.Getter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiagnosisCancelPanel extends JPanel {
    private JTable appointmentTable;
    private AppointmentTableModel tableModel;
    private Doctor doctor;

    public DiagnosisCancelPanel(Doctor doctor) {
        this.doctor = doctor;

        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        titlePanel.add(UIUtil.createTitleLabel("诊间退号"));

        // 工具栏
        JButton btnRefresh = UIUtil.createButton("刷新列表");
        JButton btnCancel = UIUtil.createButton("执行退号");
        btnRefresh.addActionListener(e -> loadData());
        btnCancel.addActionListener(e -> cancelRegistration());

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolPanel.setBackground(Color.WHITE);
        toolPanel.add(btnRefresh);
        toolPanel.add(btnCancel);

        // 表格配置
        tableModel = new AppointmentTableModel();
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(toolPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void loadData() {
        try {
            List<Appointment> appointments = new AppointmentDao().getCancelableAppointments();
            tableModel.setData(appointments);
        } catch (SQLException e) {
            UIUtil.showError(this, "加载数据失败: " + e.getMessage(), "数据库错误");
        }
    }

    private void cancelRegistration() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtil.showError(this, "请先选择要退号的预约", "操作错误");
            return;
        }

        Appointment app = tableModel.getData().get(selectedRow);
        String reason = JOptionPane.showInputDialog(this, "请输入退号原因:", "退号确认", JOptionPane.PLAIN_MESSAGE);

        if (reason != null && !reason.trim().isEmpty()) {
            try {
                new AppointmentDao().cancelAppointment(app.getId(), reason.trim());
                UIUtil.showInfo(this, "退号操作成功", "操作完成");
                loadData();
            } catch (SQLException ex) {
                UIUtil.showError(this, "退号失败: " + ex.getMessage(), "系统错误");
            }
        }
    }

    static class AppointmentTableModel extends AbstractTableModel {
        @Getter
        private List<Appointment> data;
        private final String[] COLUMNS = {"预约号", "患者姓名", "预约时间", "当前状态"};

        public void setData(List<Appointment> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() { return data != null ? data.size() : 0; }

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
            Appointment app = data.get(row);
            return switch (column) {
                case 0 -> app.getId();
                case 1 -> app.getPatientId();
                case 2 -> app.getFormattedTime();
                case 3 -> app.getStatus();
                default -> null;
            };
        }
    }
}