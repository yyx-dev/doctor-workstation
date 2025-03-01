package com.dw.gui.panel.other;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.dw.model.other.Appointment;
import com.dw.dao.other.AppointmentDao;
import com.dw.model.user.Patient;
import com.dw.util.UIUtil;
import java.util.List;
import com.dw.model.user.Doctor;
import com.dw.dao.user.DoctorDao;

public class RegistrationPanel extends JPanel {
    private JComboBox<String> departmentComboBox;
    private JComboBox<String> doctorComboBox;
    private JButton registerButton;
    private AppointmentDao appointmentDao = new AppointmentDao();
    private Patient patient; // 当前患者信息
    private DoctorDao doctorDao = new DoctorDao();
    private JRadioButton departmentRadioButton;
    private JRadioButton doctorRadioButton;
    private ButtonGroup group;

    public RegistrationPanel(Patient patient) {
        this.patient = patient;
        setLayout(new GridBagLayout()); // 使用GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 初始化组件
        departmentComboBox = new JComboBox<>();
        doctorComboBox = new JComboBox<>();
        registerButton = new JButton("挂号");

        // 添加科室挂号和医生挂号的单选按钮
        departmentRadioButton = new JRadioButton("科室挂号");
        doctorRadioButton = new JRadioButton("医生挂号");
        group = new ButtonGroup();
        group.add(departmentRadioButton);
        group.add(doctorRadioButton);

        // 默认选择医生挂号
        doctorRadioButton.setSelected(true);

        // 设置下拉框大小
        departmentComboBox.setPreferredSize(new Dimension(200, 30));
        doctorComboBox.setPreferredSize(new Dimension(200, 30));

        // 添加组件到面板
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(departmentRadioButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(departmentComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(doctorRadioButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(doctorComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(registerButton, gbc);

        // 加载科室和医生信息
        loadDepartments();
        loadDoctors();

        // 添加事件监听器
        departmentComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDoctors(); // 选择科室后加载医生
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerAppointment();
            }
        });
    }

    private void loadDepartments() {
        // 从数据库加载科室信息
        List<String> departments = appointmentDao.getDepartments(); // 假设有这个方法
        for (String department : departments) {
            departmentComboBox.addItem(department);
        }
    }

    private void loadDoctors() {
        // 从数据库加载医生信息
        List<Doctor> doctors = doctorDao.findByDepartment((String) departmentComboBox.getSelectedItem()); // 根据科室加载医生
        doctorComboBox.removeAllItems(); // 清空现有医生
        for (Doctor doctor : doctors) {
            doctorComboBox.addItem(doctor.getId() + " - " + doctor.getName()); // 显示医生ID和姓名
        }
    }

    private void registerAppointment() {
        String selectedDepartment = (String) departmentComboBox.getSelectedItem();
        String selectedDoctor = (String) doctorComboBox.getSelectedItem();

        Appointment appointment = new Appointment();
        appointment.setPatientId(patient.getId());
        appointment.setDepartment(selectedDepartment);

        if (doctorRadioButton.isSelected()) {
            appointment.setDoctorId(selectedDoctor != null ? Integer.parseInt(selectedDoctor.split(" - ")[0]) : null);
        } else {
            appointment.setDoctorId(null); // 科室挂号不指定医生
        }
        appointment.setStatus("waiting");

        // 保存挂号信息
        int result = appointmentDao.add(appointment);
        if (result > 0) {
            UIUtil.showInfo(this, "挂号成功！", "成功");
        } else {
            UIUtil.showError(this, "挂号失败，请重试！", "错误");
        }
    }
} 