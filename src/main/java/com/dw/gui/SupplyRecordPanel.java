package com.dw.gui;

import com.dw.dao.MedicalRecordDao;
import com.dw.model.Doctor;
import com.dw.model.MedicalRecord;
import com.dw.util.UIUtil;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class SupplyRecordPanel extends JPanel {
    private final MedicalRecordDao recordDao = new MedicalRecordDao();
    private JTable recordTable;
    private JButton btnRefresh;
    private MedicalRecordTableModel tableModel;

    private Doctor doctor;

    public SupplyRecordPanel(Doctor doctor) {
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
        titlePanel.add(UIUtil.createTitleLabel("病案供应管理"));

        // 表格配置
        tableModel = new MedicalRecordTableModel();
        recordTable = new JTable(tableModel);
        recordTable.setRowHeight(30);
        recordTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        recordTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        // 工具栏
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolPanel.setBackground(Color.WHITE);
        btnRefresh = UIUtil.createButton("刷新列表");
        btnRefresh.addActionListener(e -> refreshData());
        toolPanel.add(btnRefresh);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(toolPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(recordTable), BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        loadData();
    }

    private void loadData() {
        try {
            List<MedicalRecord> records = recordDao.getAllActiveRecords();
            tableModel.setData(records);
        } catch (SQLException e) {
            UIUtil.showError(this, "加载病案失败: " + e.getMessage(), "数据库错误");
        }
    }

    static class MedicalRecordTableModel extends AbstractTableModel {
        private List<MedicalRecord> data;
        private final String[] COLUMNS = {"病案号", "患者姓名", "入院日期", "诊断结果", "操作"};

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
            return switch (column) {
                case 0 -> record.getId();
                case 1 -> record.getPatientId();
                case 2 -> record.getAdmissionDate();
                case 3 -> record.getDiagnosis();
                case 4 -> "撤回";
                default -> null;
            };
        }
    }

    // 撤回按钮渲染器
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("撤回");
            return this;
        }
    }

    // 撤回按钮编辑器
    class ButtonEditor extends DefaultCellEditor {
        private String label;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            label = (value == null) ? "" : value.toString();
            return new JButton(label);
        }

        public Object getCellEditorValue() {
            int recordId = (int) tableModel.getValueAt(currentRow, 0);
            try {
                if (UIUtil.showConfirm(null, "确定要撤回该病案吗？", "确认操作")) {
                    recordDao.withdrawRecord(recordId);
                    loadData();
                }
            } catch (SQLException e) {
                UIUtil.showError(null, "撤回失败: " + e.getMessage(), "系统错误");
            }
            return label;
        }
    }
}