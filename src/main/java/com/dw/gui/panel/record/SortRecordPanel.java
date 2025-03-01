// SortRecordPanel.java 病案整序面板
package com.dw.gui.panel.record;

import com.dw.dao.record.MedicalRecordDao;
import com.dw.model.user.Doctor;
import com.dw.model.record.MedicalRecord;
import com.dw.util.UIUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class SortRecordPanel extends JPanel {

    private final JComboBox<String> comboSort = UIUtil.createComboBox(
            new String[]{"按入院日期", "按患者姓名", "按科室"}
    );

    private final JTable table = new JTable();
    private final MedicalRecordTableModel tableModel = new MedicalRecordTableModel();
    private JButton btnRefresh;

    public SortRecordPanel(Doctor doctor) {
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

        JLabel titleLabel = UIUtil.createTitleLabel("病案整序");
        titlePanel.add(titleLabel);

        // 排序控制面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(UIUtil.createLabel("排序方式："));
        controlPanel.add(comboSort);

        btnRefresh = UIUtil.createButton("刷新列表");
        btnRefresh.addActionListener(e -> refreshData());
        controlPanel.add(btnRefresh);

        // 表格配置
        table.setModel(new MedicalRecordTableModel());
        table.setFont(UIUtil.NORMAL_FONT);
        table.setRowHeight(25);

        JPanel centralPanel = new JPanel(new BorderLayout(10, 10));
        centralPanel.setBackground(Color.WHITE);
        centralPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centralPanel.add(controlPanel, BorderLayout.NORTH);
        centralPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // 将所有面板添加到主面板
        add(titlePanel, BorderLayout.NORTH);
        add(centralPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        loadData();
    }

    private void loadData() {
        try {
            String sortType = (String) comboSort.getSelectedItem();
            List<MedicalRecord> records = new MedicalRecordDao().getSortedRecords(sortType);
            tableModel.setData(records);
        } catch (SQLException e) {
            UIUtil.showError(this, "加载病案数据失败: " + e.getMessage(), "数据库错误");
        }
    }

    static class MedicalRecordTableModel extends AbstractTableModel {
        private List<MedicalRecord> data;
        private final String[] COLUMNS = {"病案号", "患者姓名", "入院日期", "诊断结果"};

        public void setData(List<MedicalRecord> data) {
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
            MedicalRecord record = data.get(row);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            return switch(column) {
                case 0 -> record.getId();
                case 1 -> record.getPatientId();
                case 2 -> dateFormat.format(record.getAdmissionDate());
                case 3 -> record.getDiagnosis();
                default -> null;
            };
        }
    }

}

