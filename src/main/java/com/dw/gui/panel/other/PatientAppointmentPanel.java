package com.dw.gui.panel.other;

import com.dw.dao.other.AppointmentDao;
import com.dw.model.other.Appointment;
import com.dw.model.user.Patient;
import com.dw.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 患者挂号面板
 */
public class PatientAppointmentPanel extends JPanel {

    private Patient patient; // 当前患者信息
    private JTable appointmentTable; // 表格组件
    private DefaultTableModel tableModel; // 表格模型
    private JButton refreshButton; // 刷新按钮

    // 数据访问对象
    private AppointmentDao appointmentDao = new AppointmentDao();

    /**
     * 构造函数
     * @param patient 患者对象
     */
    public PatientAppointmentPanel(Patient patient) {
        this.patient = patient;

        // 初始化界面
        initUI();

        // 加载数据
        refreshData();
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        setLayout(new BorderLayout());

        // 创建表格模型
        String[] columnNames = {"ID", "科室", "状态", "主诉", "挂号时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置单元格不可编辑
            }
        };

        // 创建表格
        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(UIUtil.NORMAL_FONT);
        appointmentTable.setRowHeight(25);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = UIUtil.createButton("刷新");
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 添加按钮事件
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
    }

    /**
     * 刷新挂号数据
     */
    public void refreshData() {
        // 清空表格数据
        tableModel.setRowCount(0);

        // 获取患者的挂号记录
        List<Appointment> appointments = appointmentDao.findByPatientId(patient.getId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 填充表格数据
        for (Appointment appointment : appointments) {
            Object[] rowData = {
                appointment.getId(),
                appointment.getDepartment(),
                appointment.getStatus(),
                appointment.getChiefComplaint(),
                dateFormat.format(appointment.getAppointmentTime())
            };
            tableModel.addRow(rowData);
        }
        // 重新应用按钮样式
        UIUtil.setDefaultStyle(refreshButton);
    }
}
