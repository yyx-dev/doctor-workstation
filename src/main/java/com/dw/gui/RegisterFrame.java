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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * 用户注册界面
 */
public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JButton backButton;

    // 医生信息输入字段（动态显示）
    private JTextField nameField;
    private JComboBox<String> genderComboBox;
    private JTextField departmentField;
    private JTextField registerTitleField;
    private JTextField phoneField;

    // 包含动态显示的医生信息面板
    private JPanel doctorInfoPanel;
    private JPanel mainPanel;

    // 数据访问对象
    private UserDao userDao = new UserDao();
    private DoctorDao doctorDao = new DoctorDao();

    /**
     * 构造函数
     */
    public RegisterFrame() {
        super("门诊医生工作站系统 - 注册");

        // 初始化界面
        initUI();

        // 添加事件监听器
        addListeners();

        // 设置窗口属性
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null); // 居中显示
        setResizable(false);
        setVisible(true);
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        // 设置布局
        setLayout(new BorderLayout());

        // 创建标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(UIUtil.PRIMARY_COLOR);
        JLabel titleLabel = UIUtil.createTitleLabel("用户注册");
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // 创建主面板，使用CardLayout来切换不同的注册信息面板
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // 创建基本信息面板
        JPanel basicInfoPanel = new JPanel(new GridBagLayout());
        basicInfoPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // 用户名标签和输入框
        JLabel usernameLabel = UIUtil.createLabel("用户名：");
        usernameField = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        basicInfoPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        basicInfoPanel.add(usernameField, gbc);

        // 密码标签和输入框
        JLabel passwordLabel = UIUtil.createLabel("密码：");
        passwordField = UIUtil.createPasswordField();

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        basicInfoPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        basicInfoPanel.add(passwordField, gbc);

        // 确认密码标签和输入框
        JLabel confirmPasswordLabel = UIUtil.createLabel("确认密码：");
        confirmPasswordField = UIUtil.createPasswordField();

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        basicInfoPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        basicInfoPanel.add(confirmPasswordField, gbc);

        // 角色标签和选择框
        JLabel roleLabel = UIUtil.createLabel("身份：");
        String[] roles = {"医生", "患者"};
        roleComboBox = UIUtil.createComboBox(roles);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        basicInfoPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        basicInfoPanel.add(roleComboBox, gbc);

        // 创建医生信息面板
        doctorInfoPanel = new JPanel(new GridBagLayout());
        doctorInfoPanel.setBackground(Color.WHITE);
        doctorInfoPanel.setBorder(BorderFactory.createTitledBorder("医生信息"));

        // 姓名标签和输入框
        JLabel nameLabel = UIUtil.createLabel("姓名：");
        nameField = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        doctorInfoPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        doctorInfoPanel.add(nameField, gbc);

        // 性别标签和选择框
        JLabel genderLabel = UIUtil.createLabel("性别：");
        String[] genders = {"男", "女"};
        genderComboBox = UIUtil.createComboBox(genders);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        doctorInfoPanel.add(genderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        doctorInfoPanel.add(genderComboBox, gbc);

        // 科室标签和输入框
        JLabel departmentLabel = UIUtil.createLabel("科室：");
        departmentField = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        doctorInfoPanel.add(departmentLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        doctorInfoPanel.add(departmentField, gbc);

        // 职称标签和输入框
        JLabel registerTitleLabel = UIUtil.createLabel("职称：");
        registerTitleField = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        doctorInfoPanel.add(registerTitleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        doctorInfoPanel.add(registerTitleField, gbc);

        // 联系电话标签和输入框
        JLabel phoneLabel = UIUtil.createLabel("联系电话：");
        phoneField = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        doctorInfoPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        doctorInfoPanel.add(phoneField, gbc);

        // 初始默认不显示医生信息面板
        doctorInfoPanel.setVisible(false);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        registerButton = UIUtil.createButton("注册");
        backButton = UIUtil.createButton("返回");

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        // 将所有面板添加到主面板
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.add(basicInfoPanel, BorderLayout.NORTH);
        formPanel.add(doctorInfoPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 添加到主窗口
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * 添加事件监听器
     */
    private void addListeners() {
        // 角色选择变化监听
        roleComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selectedRole = (String) e.getItem();
                    // 如果选择医生，显示医生信息面板
                    doctorInfoPanel.setVisible("医生".equals(selectedRole));
                    pack(); // 根据内容调整窗口大小
                    setSize(500, doctorInfoPanel.isVisible() ? 600 : 400);
                    setLocationRelativeTo(null); // 重新居中
                }
            }
        });

        // 注册按钮点击事件
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        // 返回按钮点击事件
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
    }

    /**
     * 注册处理
     */
    private void register() {
        // 获取基本信息
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String roleText = (String) roleComboBox.getSelectedItem();
        String role = "医生".equals(roleText) ? "doctor" : "patient";

        // 验证基本信息
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            UIUtil.showError(this, "用户名和密码不能为空！", "错误");
            return;
        }

        if (!password.equals(confirmPassword)) {
            UIUtil.showError(this, "两次输入的密码不一致！", "错误");
            return;
        }

        // 检查用户名是否已存在
        if (userDao.isUsernameExists(username)) {
            UIUtil.showError(this, "用户名已存在，请选择其他用户名！", "错误");
            return;
        }

        // 如果是医生，验证医生信息
        if ("doctor".equals(role)) {
            String name = nameField.getText().trim();
            String gender = (String) genderComboBox.getSelectedItem();
            String department = departmentField.getText().trim();
            String title = registerTitleField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || department.isEmpty() || phone.isEmpty()) {
                UIUtil.showError(this, "医生信息不完整，请填写所有必填字段！", "错误");
                return;
            }

            // 注册用户
            User user = new User(username, password, role);
            int userId = userDao.add(user);

            if (userId > 0) {
                // 注册医生信息
                Doctor doctor = new Doctor(userId, name, gender, department, title, phone);
                int doctorId = doctorDao.add(doctor);

                if (doctorId > 0) {
                    UIUtil.showInfo(this, "医生注册成功！请使用新账号登录。", "注册成功");
                    goBack(); // 返回登录界面
                } else {
                    UIUtil.showError(this, "医生信息保存失败！", "注册失败");
                }
            } else {
                UIUtil.showError(this, "用户注册失败！", "注册失败");
            }
        } else {
            // 注册患者用户
            User user = new User(username, password, role);
            int userId = userDao.add(user);

            if (userId > 0) {
                UIUtil.showInfo(this, "患者账号注册成功！请登录后完善个人信息。", "注册成功");
                goBack(); // 返回登录界面
            } else {
                UIUtil.showError(this, "用户注册失败！", "注册失败");
            }
        }
    }

    /**
     * 返回登录界面
     */
    private void goBack() {
        new LoginFrame();
        dispose(); // 关闭当前窗口
    }
}