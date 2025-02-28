package com.dw.gui;

import com.dw.dao.PatientDao;
import com.dw.dao.UserDao;
import com.dw.model.Patient;
import com.dw.model.User;
import com.dw.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 患者建档界面
 */
public class PatientRegisterFrame extends JFrame {

    private int userId; // 用户ID

    private JTextField nameField;
    private JComboBox<String> genderComboBox;
    private JTextField ageField;
    private JTextField phoneField;
    private JTextField addressField;
    private JButton submitButton;
    private JButton cancelButton;

    // 数据访问对象
    private UserDao userDao = new UserDao();
    private PatientDao patientDao = new PatientDao();

    /**
     * 构造函数
     * @param userId 用户ID
     */
    public PatientRegisterFrame(int userId) {
        super("门诊医生工作站系统 - 患者建档");
        this.userId = userId;

        // 初始化界面
        initUI();

        // 添加事件监听器
        addListeners();

        // 设置窗口属性
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
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
        JLabel titleLabel = UIUtil.createTitleLabel("患者个人信息建档");
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // 姓名标签和输入框
        JLabel nameLabel = UIUtil.createLabel("姓名：");
        nameField = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(nameField, gbc);

        // 性别标签和选择框
        JLabel genderLabel = UIUtil.createLabel("性别：");
        String[] genders = {"男", "女"};
        genderComboBox = UIUtil.createComboBox(genders);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(genderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(genderComboBox, gbc);

        // 年龄标签和输入框
        JLabel ageLabel = UIUtil.createLabel("年龄：");
        ageField = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(ageLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(ageField, gbc);

        // 电话标签和输入框
        JLabel phoneLabel = UIUtil.createLabel("联系电话：");
        phoneField = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(phoneField, gbc);

        // 地址标签和输入框
        JLabel addressLabel = UIUtil.createLabel("住址：");
        addressField = UIUtil.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(addressField, gbc);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        submitButton = UIUtil.createButton("提交");
        cancelButton = UIUtil.createButton("取消");

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        // 添加到主窗口
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 添加事件监听器
     */
    private void addListeners() {
        // 提交按钮点击事件
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitPatientInfo();
            }
        });

        // 取消按钮点击事件
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
    }

    /**
     * 提交患者信息
     */
    private void submitPatientInfo() {
        // 获取表单信息
        String name = nameField.getText().trim();
        String gender = (String) genderComboBox.getSelectedItem();
        String ageStr = ageField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        // 验证必填字段
        if (name.isEmpty() || ageStr.isEmpty() || phone.isEmpty()) {
            UIUtil.showError(this, "请填写所有必填字段！", "错误");
            return;
        }

        // 验证年龄格式
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 150) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            UIUtil.showError(this, "请输入有效的年龄！", "错误");
            return;
        }

        // 创建患者对象并保存
        Patient patient = new Patient(userId, name, gender, age, phone, address);
        int patientId = patientDao.add(patient);

        if (patientId > 0) {
            UIUtil.showInfo(this, "个人信息提交成功！", "建档成功");

            // 查询完整的患者信息
            patient = patientDao.findById(patientId);

            // 打开患者主界面
            new PatientFrame(patient);
            dispose(); // 关闭当前窗口
        } else {
            UIUtil.showError(this, "个人信息保存失败！", "建档失败");
        }
    }

    /**
     * 取消并返回登录界面
     */
    private void cancel() {
        // 提示用户
        if (UIUtil.showConfirm(this, "您尚未完成建档，是否确定返回登录界面？", "确认")) {
            // 返回登录界面
            new LoginFrame();
            dispose(); // 关闭当前窗口
        }
    }
}