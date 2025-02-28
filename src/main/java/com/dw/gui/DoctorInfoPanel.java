package com.dw.gui;

import com.dw.dao.DoctorDao;
import com.dw.dao.UserDao;
import com.dw.model.Doctor;
import com.dw.model.User;
import com.dw.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 医生信息面板
 */
public class DoctorInfoPanel extends JPanel {

    private Doctor doctor; // 当前医生信息

    // 表单字段
    private JTextField nameField;
    private JTextField genderField;
    private JTextField departmentField;
    private JTextField doctorTitleField;
    private JTextField phoneField;

    // 修改密码字段
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    // 按钮
    private JButton updateInfoButton;
    private JButton changePasswordButton;

    // 数据访问对象
    private DoctorDao doctorDao = new DoctorDao();
    private UserDao userDao = new UserDao();

    /**
     * 构造函数
     * @param doctor 医生对象
     */
    public DoctorInfoPanel(Doctor doctor) {
        this.doctor = doctor;

        // 初始化界面
        initUI();

        // 添加事件监听器
        addListeners();

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

        JLabel titleLabel = new JLabel("个人信息管理");
        titleLabel.setFont(UIUtil.TITLE_FONT);
        titleLabel.setForeground(UIUtil.PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // 创建基本信息面板
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // 姓名标签和输入框
        JLabel nameLabel = UIUtil.createLabel("姓名：");
        nameField = UIUtil.createTextField();
        nameField.setText(doctor.getName());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        infoPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        infoPanel.add(nameField, gbc);

        // 性别标签和输入框
        JLabel genderLabel = UIUtil.createLabel("性别：");
        genderField = UIUtil.createTextField();
        genderField.setText(doctor.getGender());
        genderField.setEditable(false); // 性别不可修改

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        infoPanel.add(genderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        infoPanel.add(genderField, gbc);

        // 科室标签和输入框
        JLabel departmentLabel = UIUtil.createLabel("科室：");
        departmentField = UIUtil.createTextField();
        departmentField.setText(doctor.getDepartment());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        infoPanel.add(departmentLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        infoPanel.add(departmentField, gbc);

        // 职称标签和输入框
        JLabel doctorTitleLabel = UIUtil.createLabel("职称：");
        doctorTitleField = UIUtil.createTextField();
        doctorTitleField.setText(doctor.getTitle());

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        infoPanel.add(doctorTitleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        infoPanel.add(doctorTitleField, gbc);

        // 电话标签和输入框
        JLabel phoneLabel = UIUtil.createLabel("联系电话：");
        phoneField = UIUtil.createTextField();
        phoneField.setText(doctor.getPhone());

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        infoPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        infoPanel.add(phoneField, gbc);

        // 修改信息按钮
        updateInfoButton = UIUtil.createButton("更新信息");

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(updateInfoButton, gbc);

        // 创建修改密码面板
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createTitledBorder("修改密码"));

        // 旧密码标签和输入框
        JLabel oldPasswordLabel = UIUtil.createLabel("旧密码：");
        oldPasswordField = UIUtil.createPasswordField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordPanel.add(oldPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        passwordPanel.add(oldPasswordField, gbc);

        // 新密码标签和输入框
        JLabel newPasswordLabel = UIUtil.createLabel("新密码：");
        newPasswordField = UIUtil.createPasswordField();

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        passwordPanel.add(newPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        passwordPanel.add(newPasswordField, gbc);

        // 确认密码标签和输入框
        JLabel confirmPasswordLabel = UIUtil.createLabel("确认密码：");
        confirmPasswordField = UIUtil.createPasswordField();

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        passwordPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        passwordPanel.add(confirmPasswordField, gbc);

        // 修改密码按钮
        changePasswordButton = UIUtil.createButton("修改密码");

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        passwordPanel.add(changePasswordButton, gbc);

        // 将信息面板和密码面板添加到主面板
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(infoPanel);
        centerPanel.add(passwordPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 将所有面板添加到主面板
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * 添加事件监听器
     */
    private void addListeners() {
        // 更新信息按钮点击事件
        updateInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDoctorInfo();
            }
        });

        // 修改密码按钮点击事件
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
    }

    /**
     * 更新医生信息
     */
    private void updateDoctorInfo() {
        // 获取表单信息
        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String title = doctorTitleField.getText().trim();
        String phone = phoneField.getText().trim();

        // 验证必填字段
        if (name.isEmpty() || department.isEmpty() || phone.isEmpty()) {
            UIUtil.showError(this, "请填写所有必填字段！", "错误");
            return;
        }

        // 更新医生信息
        doctor.setName(name);
        doctor.setDepartment(department);
        doctor.setTitle(title);
        doctor.setPhone(phone);

        boolean success = doctorDao.update(doctor);

        if (success) {
            UIUtil.showInfo(this, "个人信息更新成功！", "成功");
        } else {
            UIUtil.showError(this, "个人信息更新失败！", "错误");
        }
    }

    /**
     * 修改密码
     */
    private void changePassword() {
        // 获取密码信息
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // 验证密码输入
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            UIUtil.showError(this, "请填写所有密码字段！", "错误");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            UIUtil.showError(this, "两次输入的新密码不一致！", "错误");
            return;
        }

        // 验证旧密码是否正确
        User user = userDao.findById(doctor.getUserId());
        if (user == null || !user.getPassword().equals(oldPassword)) {
            UIUtil.showError(this, "旧密码输入错误！", "错误");
            return;
        }

        // 更新密码
        boolean success = userDao.updatePassword(user.getId(), newPassword);

        if (success) {
            UIUtil.showInfo(this, "密码修改成功！", "成功");
            // 清空密码字段
            oldPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        } else {
            UIUtil.showError(this, "密码修改失败！", "错误");
        }
    }
}