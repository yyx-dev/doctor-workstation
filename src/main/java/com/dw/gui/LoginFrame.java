package com.dw.gui;

import com.dw.dao.DoctorDao;
import com.dw.dao.PatientDao;
import com.dw.dao.UserDao;
import com.dw.model.Doctor;
import com.dw.model.Patient;
import com.dw.model.User;
import com.dw.service.LoginService;
import com.dw.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录界面
 */
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JButton registerButton;

    // 数据访问对象
    private UserDao userDao = new UserDao();
    private DoctorDao doctorDao = new DoctorDao();
    private PatientDao patientDao = new PatientDao();

    /**
     * 构造函数
     */
    public LoginFrame() {
        // 设置窗口标题
        super("门诊医生工作站系统 - 登录");

        // 初始化界面
        initUI();

        // 添加事件监听器
        addListeners();

        // 设置窗口属性
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
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
        JLabel titleLabel = UIUtil.createTitleLabel("门诊医生工作站系统");
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // 创建登录表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // 用户名标签和输入框
        JLabel usernameLabel = UIUtil.createLabel("用户名：");
        usernameField = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(usernameField, gbc);

        // 密码标签和输入框
        JLabel passwordLabel = UIUtil.createLabel("密码：");
        passwordField = UIUtil.createPasswordField();


        usernameField.setText("doctor1");
        passwordField.setText("password");


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(passwordField, gbc);

        // 角色标签和选择框
        JLabel roleLabel = UIUtil.createLabel("身份：");
        String[] roles = {"医生", "患者"};
        roleComboBox = UIUtil.createComboBox(roles);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(roleComboBox, gbc);

        // 登录按钮和注册按钮
        loginButton = UIUtil.createButton("登录");
        registerButton = UIUtil.createButton("注册");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        formPanel.add(buttonPanel, gbc);

        // 添加到主窗口
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    /**
     * 添加事件监听器
     */
    private void addListeners() {
        // 登录按钮点击事件
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // 注册按钮点击事件
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterFrame();
            }
        });
    }

    /**
     * 登录验证
     */
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String roleText = (String) roleComboBox.getSelectedItem();
        String role = "医生".equals(roleText) ? "doctor" : "patient";

        // 验证输入
        if (username.isEmpty() || password.isEmpty()) {
            UIUtil.showError(this, "用户名和密码不能为空！", "错误");
            return;
        }

        // 验证用户
        User user = userDao.login(username, password);
        if (user == null) {
            UIUtil.showError(this, "用户名或密码错误！", "登录失败");
            return;
        }

        // 验证角色
        if (!user.getRole().equals(role)) {
            UIUtil.showError(this, "所选身份与账号不符！", "登录失败");
            return;
        }

        // 根据角色跳转不同界面
        if ("doctor".equals(role)) {
            Doctor doctor = doctorDao.findByUserId(user.getId());
            if (doctor != null) {
                LoginService.login(doctor);
                // 打开医生界面
                new DoctorFrame(doctor);
                dispose(); // 关闭登录窗口
            } else {
                UIUtil.showError(this, "医生信息不存在，请联系管理员！", "登录失败");
            }
        } else {
            Patient patient = patientDao.findByUserId(user.getId());
            if (patient != null) {
                // 打开患者界面
                new PatientFrame(patient);
                dispose(); // 关闭登录窗口
            } else {
                // 患者未完成建档，打开患者注册界面
                UIUtil.showInfo(this, "您尚未完成建档，请先完善个人信息！", "提示");
                new PatientRegisterFrame(user.getId());
                dispose(); // 关闭登录窗口
            }
        }
    }

    /**
     * 打开注册界面
     */
    private void openRegisterFrame() {
        new RegisterFrame();
        dispose(); // 关闭登录窗口
    }

    /**
     * 主方法，程序入口
     */
    public static void main(String[] args) {
        // 使用事件调度线程来确保UI操作的线程安全
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // 设置本地外观和感觉
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new LoginFrame();
            }
        });
    }
}