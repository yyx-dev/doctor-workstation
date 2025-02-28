package com.dw.gui;

import com.dw.dao.AppointmentDao;
import com.dw.model.Appointment;
import com.dw.model.Doctor;
import com.dw.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 挂号处理面板
 */
public class AppointmentPanel extends JPanel {

    private Doctor doctor; // 当前医生信息

    // 表格组件
    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    // 选项卡
    private JTabbedPane tabbedPane;

    // 按钮
    private JButton processButton;
    private JButton refreshButton;

    // 被选中的挂号ID
    private int selectedAppointmentId = -1;

    // 数据访问对象
    private AppointmentDao appointmentDao = new AppointmentDao();

    /**
     * 构造函数
     * @param doctor 医生对象
     */
    public AppointmentPanel(Doctor doctor) {
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

        JLabel titleLabel = new JLabel("挂号处理");
        titleLabel.setFont(UIUtil.TITLE_FONT);
        titleLabel.setForeground(UIUtil.PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtil.NORMAL_FONT);

        // 创建科室挂号面板
        JPanel departmentPanel = createAppointmentListPanel("department");

        // 创建医生挂号面板
        JPanel doctorPanel = createAppointmentListPanel("doctor");

        // 添加选项卡
        tabbedPane.addTab("本科室挂号", departmentPanel);
        tabbedPane.addTab("我的挂号", doctorPanel);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        processButton = UIUtil.createButton("处理挂号");
        refreshButton = UIUtil.createButton("刷新列表");

        buttonPanel.add(processButton);
        buttonPanel.add(refreshButton);

        // 将所有面板添加到主面板
        add(titlePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建挂号列表面板
     * @param type 面板类型，"department"表示科室挂号，"doctor"表示医生挂号
     * @return 挂号列表面板
     */
    private JPanel createAppointmentListPanel(String type) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // 创建表格模型
        String[] columnNames = {"ID", "患者姓名", "性别", "年龄", "挂号时间", "状态", "主诉"};
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
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 设置表格列宽
        appointmentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        appointmentTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        appointmentTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        appointmentTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        appointmentTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        appointmentTable.getColumnModel().getColumn(6).setPreferredWidth(200);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 添加事件监听器
     */
    private void addListeners() {
        // 表格行选择事件
        appointmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = appointmentTable.getSelectedRow();
                if (row != -1) {
                    // 获取选中行的挂号ID
                    selectedAppointmentId = (int) appointmentTable.getValueAt(row, 0);
                }
            }
        });

        // 处理挂号按钮点击事件
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processAppointment();
            }
        });

        // 刷新列表按钮点击事件
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });

        // 选项卡切换事件
        tabbedPane.addChangeListener(e -> refreshData());
    }

    /**
     * 处理选中的挂号
     */
    private void processAppointment() {
        if (selectedAppointmentId == -1) {
            UIUtil.showError(this, "请先选择一条挂号记录！", "错误");
            return;
        }

        // 获取选中行数据
        Appointment appointment = appointmentDao.findById(selectedAppointmentId);
        if (appointment == null) {
            UIUtil.showError(this, "获取挂号信息失败！", "错误");
            return;
        }

        // 确认接诊
        if (UIUtil.showConfirm(this, "确定接诊该患者吗？", "确认")) {
            appointment.setStatus("processing");
            appointmentDao.update(appointment);
            UIUtil.showInfo(this, "接诊成功！", "成功");
            refreshData();
        }
    }

    /**
     * 打开诊断对话框
     * @param appointmentId 挂号ID
     */
    private void openDiagnosisDialog(int appointmentId) {
        // 查询挂号详情
        Appointment appointment = appointmentDao.findById(appointmentId);
        if (appointment == null) {
            UIUtil.showError(this, "获取挂号信息失败！", "错误");
            return;
        }

        // 打开诊断对话框
        DiagnosisDialog dialog = new DiagnosisDialog(SwingUtilities.getWindowAncestor(this), appointment, doctor);
        dialog.setVisible(true);

        // 对话框关闭后刷新数据
        refreshData();
    }

    /**
     * 刷新挂号数据
     */
    public void refreshData() {
        // 清空表格数据
        tableModel.setRowCount(0);

        // 获取当前选项卡索引
        int tabIndex = tabbedPane.getSelectedIndex();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Appointment> appointments;

        if (tabIndex == 0) { // 科室挂号
            // 获取科室挂号列表（筛选未处理的）
            appointments = appointmentDao.findByDepartment(doctor.getDepartment());
        } else { // 医生挂号
            appointments = appointmentDao.findByDoctorId(doctor.getId()); // 获取医生的挂号
        }

        for (Appointment appointment : appointments) {
            Object[] rowData = {
                appointment.getId(),
                appointment.getPatient().getName(),
                appointment.getPatient().getGender(),
                appointment.getPatient().getAge(),
                dateFormat.format(appointment.getAppointmentTime()),
                appointment.getStatus(),
                appointment.getChiefComplaint()
            };
            tableModel.addRow(rowData);
        }
    }
}