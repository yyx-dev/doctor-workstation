// SchedulePlanPanel.java 排版计划
package com.dw.gui;

import com.dw.dao.SchedulePlanDao;
import com.dw.model.Doctor;
import com.dw.model.SchedulePlan;
import com.dw.service.LoginService;
import com.dw.util.UIUtil;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SchedulePlanPanel extends JPanel {
    private SchedulePlanDao dao = new SchedulePlanDao();
    private JTable table;
    private ScheduleTableModel tableModel;
    private JButton btnRefresh;
    private final Doctor doctor;
    public SchedulePlanPanel(Doctor doctor) {
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
        titlePanel.add(UIUtil.createTitleLabel("排班计划管理"));

        // 工具栏
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolPanel.setBackground(Color.WHITE);
        btnRefresh = UIUtil.createButton("刷新");
        btnRefresh.addActionListener(e -> refreshData());
        toolPanel.add(btnRefresh);
        // 表格
        tableModel = new ScheduleTableModel();
        table = new JTable(tableModel);
        table.setFont(UIUtil.NORMAL_FONT);
        table.setRowHeight(50);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 800));

        JPanel centralPanel = new JPanel(new BorderLayout(10, 10));
        centralPanel.setBackground(Color.WHITE);
        centralPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centralPanel.add(toolPanel, BorderLayout.NORTH);
        centralPanel.add(scrollPane, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(centralPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        loadData();
    }

    private void loadData() {
        try {
            List<SchedulePlan> plans = dao.findByDoctor(LoginService.currentDoctor().getId());
            tableModel.setData(plans);
        } catch (SQLException e) {
            UIUtil.showError(this, "加载排班数据失败: " + e.getMessage(), "数据库错误");
        }
    }

    static class ScheduleTableModel extends AbstractTableModel {
        private List<SchedulePlan> data = new ArrayList<>();
        private final String[] COLUMNS = {"日期", "班次类型", "开始时间", "结束时间", "最大接诊量"};

        public void setData(List<SchedulePlan> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int col) { return COLUMNS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            SchedulePlan plan = data.get(row);
            return switch(col) {
                case 0 -> plan.getScheduleDate();
                case 1 -> plan.getShiftType();
                case 2 -> plan.getStartTime();
                case 3 -> plan.getEndTime();
                case 4 -> plan.getMaxPatients();
                default -> null;
            };
        }
    }
}