package com.dw.gui;

import javax.swing.*;
import java.awt.*;

import com.dw.dao.MedicalRecordDao;
import com.dw.model.MedicalRecord;

import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * 病历面板
 */
public class PatientMedicalRecordPanel extends JPanel {
    private JTable recordTable;
    private DefaultTableModel tableModel;
    private MedicalRecordDao medicalRecordDao = new MedicalRecordDao();
    private int patientId; // 当前患者ID

    public PatientMedicalRecordPanel(int patientId) {
        this.patientId = patientId;
        setLayout(new BorderLayout());
        JLabel label = new JLabel("病历信息");
        label.setFont(new Font("微软雅黑", Font.BOLD, 16));
        add(label, BorderLayout.NORTH);

        // 创建表格模型
        String[] columnNames = {"ID", "诊断", "药方", "创建时间"};
        tableModel = new DefaultTableModel(columnNames, 0);
        recordTable = new JTable(tableModel);
        add(new JScrollPane(recordTable), BorderLayout.CENTER);
    }

    public void refreshData() {
        // 清空表格数据
        tableModel.setRowCount(0);

        // 从数据库加载病历信息

        List<MedicalRecord> records = null;
        try {
            records = medicalRecordDao.findByPatientId(patientId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (records == null) {
            return;
        }

        for (MedicalRecord record : records) {
            Object[] rowData = {
                    record.getId(),
                    record.getDiagnosis(),
                    record.getPrescription(),
                    record.getCreatedAt()
            };
            tableModel.addRow(rowData);
        }
    }
} 