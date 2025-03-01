// ReceptionPanel.java
package com.dw.gui;

import com.dw.dao.ConsultationDao;
import com.dw.model.ConsultationQueue;
import com.dw.model.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReceptionPanel extends JPanel {
    private final Doctor doctor;
    private ConsultationDao dao = new ConsultationDao();
    private JTable queueTable;
    private QueueTableModel tableModel;
    private JButton btnStart;
    private JButton btnComplete;
    private Timer refreshTimer;

    public ReceptionPanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
        startAutoRefresh();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = UIUtil.createPanel();
        titlePanel.add(UIUtil.createTitleLabel("当前接诊队列 - " + doctor.getName()));

        // 操作按钮
        JPanel controlPanel = UIUtil.createPanel();
        btnStart = UIUtil.createButton("开始接诊");
        btnComplete = UIUtil.createButton("完成接诊");

        btnStart.addActionListener(this::startConsultation);
        btnComplete.addActionListener(this::completeConsultation);

        controlPanel.add(btnStart);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(btnComplete);

        // 队列表格
        tableModel = new QueueTableModel();
        queueTable = new JTable(tableModel);
        queueTable.setFont(UIUtil.NORMAL_FONT);
        queueTable.setRowHeight(30);
        queueTable.getSelectionModel().addListSelectionListener(e -> updateButtonState());

        // 自动刷新控件
        JLabel refreshLabel = UIUtil.createLabel("自动刷新中...");
        JPanel statusPanel = UIUtil.createPanel();
        statusPanel.add(refreshLabel);

        add(titlePanel, BorderLayout.NORTH);
        add(new JScrollPane(queueTable), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(5000, e -> refreshData());
        refreshTimer.start();
    }

    public void refreshData() {
        try {
            List<ConsultationQueue> queue = dao.getCurrentQueue(doctor.getId());
            tableModel.setData(queue);
            updateButtonState();
        } catch (SQLException ex) {
            UIUtil.showError(this, "刷新队列失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void updateButtonState() {
        int selectedRow = queueTable.getSelectedRow();
        boolean hasSelection = selectedRow != -1;

        btnStart.setEnabled(hasSelection &&
                tableModel.getStatus(selectedRow).equals("等待中"));
        btnComplete.setEnabled(hasSelection &&
                tableModel.getStatus(selectedRow).equals("接诊中"));
    }

    private void startConsultation(ActionEvent e) {
        int selectedRow = queueTable.getSelectedRow();
        if (selectedRow == -1) return;

        try {
            int queueId = tableModel.getQueueId(selectedRow);
            dao.startConsultation(queueId);
            refreshData();
            UIUtil.showInfo(this, "已开始接诊 #" + queueId, "操作成功");
        } catch (SQLException ex) {
            UIUtil.showError(this, "操作失败：" + ex.getMessage(), "系统错误");
        }
    }

    private void completeConsultation(ActionEvent e) {
        int selectedRow = queueTable.getSelectedRow();
        if (selectedRow == -1) return;

        try {
            int queueId = tableModel.getQueueId(selectedRow);
            dao.completeConsultation(queueId);
            refreshData();
            UIUtil.showInfo(this, "已完成接诊 #" + queueId, "操作成功");
        } catch (SQLException ex) {
            UIUtil.showError(this, "操作失败：" + ex.getMessage(), "系统错误");
        }
    }

    class QueueTableModel extends AbstractTableModel {
        private List<ConsultationQueue> data = new ArrayList<>();
        private final String[] COLUMNS = {"队列号", "患者姓名", "当前状态", "等待时间"};

        public void setData(List<ConsultationQueue> data) {
            this.data = data;
            fireTableDataChanged();
        }

        String getStatus(int row) {
            return data.get(row).getStatus();
        }

        int getQueueId(int row) {
            return data.get(row).getId();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int col) { return COLUMNS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            ConsultationQueue item = data.get(row);
            return switch(col) {
                case 0 -> item.getQueueNumber();
                case 1 -> "患者姓名"; // 需连接患者表获取实际姓名
                case 2 -> item.getStatus();
                case 3 -> calculateWaitTime(item.getStartTime());
                default -> null;
            };
        }

        private String calculateWaitTime(Timestamp start) {
            if (start == null) return "-";
            long diff = System.currentTimeMillis() - start.getTime();
            long minutes = diff / 60000;
            return minutes + " 分钟";
        }
    }
}