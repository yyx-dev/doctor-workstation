package com.dw.gui.panel.other;

import com.dw.dao.other.AppointmentDao;
import com.dw.dao.record.MedicalRecordDao;
import com.dw.dao.record.MedicalRecordDetailDao;
import com.dw.model.other.Appointment;
import com.dw.model.user.Doctor;
import com.dw.model.user.Patient;
import com.dw.model.record.MedicalRecord;
import com.dw.model.record.MedicalRecordDetail;
import com.dw.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 诊断面板
 */
public class DiagnosisPanel extends JPanel {

    private Doctor doctor; // 当前医生信息

    // 表格组件
    private JTable patientTable;
    private DefaultTableModel tableModel;

    // 按钮
    private JButton diagnosisButton;
    private JButton historyButton;
    private JButton refreshButton;

    // 被选中的挂号ID
    private int selectedAppointmentId = -1;

    // 数据访问对象
    private AppointmentDao appointmentDao = new AppointmentDao();
    private MedicalRecordDao medicalRecordDao = new MedicalRecordDao();
    private MedicalRecordDetailDao medicalRecordDetailDao = new MedicalRecordDetailDao();

    /**
     * 构造函数
     *
     * @param doctor 医生对象
     */
    public DiagnosisPanel(Doctor doctor) {
        this.doctor = doctor;

        // 初始化界面
        initUI();

        // 添加事件监听器
        addListeners();

        // 加载数据
        refreshData();

        // 设置面板属性
        setBackground(Color.WHITE);
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        // 设置布局
        setLayout(new BorderLayout());

        // 创建面板标题
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("患者诊断");
        titleLabel.setFont(UIUtil.TITLE_FONT);
        titleLabel.setForeground(UIUtil.PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // 创建表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // 创建表格模型
        String[] columnNames = {"ID", "患者姓名", "性别", "年龄", "挂号时间", "状态", "主诉"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置单元格不可编辑
            }
        };

        // 创建表格
        patientTable = new JTable(tableModel);
        patientTable.setFont(UIUtil.NORMAL_FONT);
        patientTable.setRowHeight(25);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 设置表格列宽
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        patientTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        patientTable.getColumnModel().getColumn(6).setPreferredWidth(200);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBackground(Color.WHITE);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        diagnosisButton = UIUtil.createButton("开始诊断");
        historyButton = UIUtil.createButton("查看病历");
        refreshButton = UIUtil.createButton("刷新列表");

        buttonPanel.add(diagnosisButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(refreshButton);

        // 将所有面板添加到主面板
        add(titlePanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 添加事件监听器
     */
    private void addListeners() {
        // 表格行选择事件
        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = patientTable.getSelectedRow();
                if (row != -1) {
                    // 获取选中行的挂号ID
                    selectedAppointmentId = (int) patientTable.getValueAt(row, 0);
                }

                // 双击行时打开诊断
                if (e.getClickCount() == 2) {
                    openDiagnosisDialog(selectedAppointmentId);
                }
            }
        });

        // 诊断按钮点击事件
        diagnosisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDiagnosisDialog(selectedAppointmentId);
            }
        });

        // 查看病历按钮点击事件
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewMedicalRecord();
            }
        });

        // 刷新列表按钮点击事件
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
    }

    /**
     * 打开诊断对话框
     *
     * @param appointmentId 挂号ID
     */
    private void openDiagnosisDialog(int appointmentId) {
        if (appointmentId == -1) {
            UIUtil.showError(this, "请先选择一名患者！", "错误");
            return;
        }

        // 查询挂号详情
        Appointment appointment = appointmentDao.findById(appointmentId);
        if (appointment == null) {
            UIUtil.showError(this, "获取挂号信息失败！", "错误");
            return;
        }

        // 检查挂号状态
        if ("completed".equals(appointment.getStatus())) {
            UIUtil.showError(this, "该患者已完成诊断！", "错误");
            return;
        }

        // 检查医生是否匹配
        if (appointment.getDoctorId() == null || appointment.getDoctorId() != doctor.getId()) {
            UIUtil.showError(this, "您不是该患者的主治医生！", "错误");
            return;
        }

        // 打开诊断对话框
        DiagnosisDialog dialog = new DiagnosisDialog(SwingUtilities.getWindowAncestor(this), appointment, doctor);
        dialog.setVisible(true);

        // 对话框关闭后刷新数据
        refreshData();
    }

    /**
     * 查看患者病历
     */
    private void viewMedicalRecord() {
        if (selectedAppointmentId == -1) {
            UIUtil.showError(this, "请先选择一名患者！", "错误");
            return;
        }

        // 查询挂号详情
        Appointment appointment = appointmentDao.findById(selectedAppointmentId);
        if (appointment == null) {
            UIUtil.showError(this, "获取挂号信息失败！", "错误");
            return;
        }

        List<MedicalRecordDetail> records = null;
        try {
            // 查询患者所有病历
            records = medicalRecordDetailDao.findDetailByPatientId(appointment.getPatientId());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (records != null && records.isEmpty()) {
            UIUtil.showInfo(this, "该患者暂无病历记录！", "提示");
            return;
        }

        // 使用MedicalRecordDao获取主记录
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        try {
            medicalRecords = medicalRecordDao.findByPatientId(appointment.getPatientId());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (medicalRecords.isEmpty()) {
            UIUtil.showInfo(this, "该患者暂无病历记录！", "提示");
            return;
        }

        // 打开病历查看对话框
        MedicalRecordDialog dialog = new MedicalRecordDialog(SwingUtilities.getWindowAncestor(this), medicalRecords);
        dialog.setVisible(true);
    }

    /**
     * 刷新患者数据
     */
    public void refreshData() {
        // 清空表格数据
        tableModel = (DefaultTableModel) patientTable.getModel();
        tableModel.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 获取医生挂号列表
        List<Appointment> appointments = appointmentDao.findByDoctorId(doctor.getId());

        // 填充表格数据
        for (Appointment appointment : appointments) {
            Patient patient = appointment.getPatient();
            if (patient == null) continue;

            // 转换状态显示
            String statusText;
            switch (appointment.getStatus()) {
                case "waiting":
                    statusText = "等待中";
                    break;
                case "processing":
                    statusText = "处理中";
                    break;
                case "completed":
                    statusText = "已完成";
                    break;
                default:
                    statusText = appointment.getStatus();
                    break;
            }

            // 添加行数据
            Object[] rowData = {
                    appointment.getId(),
                    patient.getName(),
                    patient.getGender(),
                    patient.getAge(),
                    dateFormat.format(appointment.getAppointmentTime()),
                    statusText,
                    appointment.getChiefComplaint()
            };
            tableModel.addRow(rowData);
        }
    }
}