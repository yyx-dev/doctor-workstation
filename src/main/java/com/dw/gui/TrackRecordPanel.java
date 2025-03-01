// TrackRecordPanel.java 病案示踪面板
package com.dw.gui;

import com.dw.dao.CirculationDao;
import com.dw.model.CirculationRecord;
import com.dw.model.Doctor;
import com.dw.util.UIUtil;

import java.sql.SQLException;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class TrackRecordPanel extends JPanel {
    private final JTextField tfRecordId = UIUtil.createTextField();
    private final JTable recordTable = new JTable();
    private final CirculationDao circulationDao = new CirculationDao();
    private JButton btnSearch;

    public TrackRecordPanel(Doctor doctor) {
        initUI();
        setupListeners();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(UIUtil.createTitleLabel("病案示踪"));

        // 搜索条件面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        tfRecordId.setColumns(10);
        searchPanel.add(UIUtil.createLabel("病案号："));
        searchPanel.add(tfRecordId);

        btnSearch = UIUtil.createButton("查询流通记录");
        searchPanel.add(btnSearch);

        // 表格配置
        recordTable.setModel(new CirculationTableModel());
        recordTable.setFont(UIUtil.NORMAL_FONT);
        recordTable.setRowHeight(25);

        JPanel centralPanel = new JPanel(new BorderLayout(10, 10));
        centralPanel.setBackground(Color.WHITE);
        centralPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centralPanel.add(searchPanel, BorderLayout.NORTH);
        centralPanel.add(new JScrollPane(recordTable), BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(centralPanel, BorderLayout.CENTER);
    }

    private void setupListeners() {
        btnSearch.addActionListener(e -> {
            try {
                int recordId = Integer.parseInt(tfRecordId.getText());
                List<CirculationRecord> records = circulationDao.getRecordsByMedicalRecord(recordId);
                ((CirculationTableModel)recordTable.getModel()).setData(records);
            } catch (NumberFormatException ex) {
                UIUtil.showError(this, "请输入有效的病案号", "输入错误");
                tfRecordId.setBorder(BorderFactory.createLineBorder(UIUtil.ERROR_COLOR));
            } catch (SQLException ex) {
                UIUtil.showError(this, "查询失败：" + ex.getMessage(), "数据库错误");
            }
        });
    }

    static class CirculationTableModel extends AbstractTableModel {
        private final String[] COLUMNS = {"病案号", "借阅人", "借出日期", "归还日期", "状态"};
        private List<CirculationRecord> data;

        public void setData(List<CirculationRecord> data) {
            this.data = data;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data != null ? data.size() : 0;
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
            CirculationRecord record = data.get(row);
            return switch (column) {
                case 0 -> record.getRecordId();
                case 1 -> record.getBorrower();
                case 2 -> record.getFormattedBorrowDate();
                case 3 -> record.getFormattedReturnDate();
                case 4 -> record.getStatus();
                default -> null;
            };
        }
    }
}