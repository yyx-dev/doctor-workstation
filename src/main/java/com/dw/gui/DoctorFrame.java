package com.dw.gui;

import com.dw.model.Doctor;
import com.dw.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 医生主界面
 */
public class DoctorFrame extends JFrame {

    private Doctor doctor; // 当前登录的医生信息

    // 左侧菜单按钮
    private JButton doctorInfoButton;
    private JButton appointmentButton;
    private JButton diagnosisButton;
    private JButton logoutButton;
    private JButton admissionFormButton;

    // 右侧内容面板
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // 子面板
    private DoctorInfoPanel doctorInfoPanel;
    private AppointmentPanel appointmentPanel;
    private DiagnosisPanel diagnosisPanel;
    private AdmissionFormPanel admissionFormPanel;

    /**
     * 构造函数
     * @param doctor 医生对象
     */
    public DoctorFrame(Doctor doctor) {
        super("门诊医生工作站系统 - 医生界面");
        this.doctor = doctor;

        // 初始化界面
        initUI();

        // 添加事件监听器
        addListeners();

        // 设置窗口属性
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // 居中显示
        setResizable(true);
        setVisible(true);
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        // 设置布局
        setLayout(new BorderLayout());

        // 创建标题面板
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIUtil.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = UIUtil.createTitleLabel("门诊医生工作站");
        titleLabel.setForeground(Color.WHITE);

        JLabel welcomeLabel = UIUtil.createLabel("欢迎您，" + doctor.getName() + "医生 (" + doctor.getDepartment() + ")");
        welcomeLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(welcomeLabel, BorderLayout.EAST);

        // 创建左侧菜单面板
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(UIUtil.SECONDARY_COLOR);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // 创建左侧菜单按钮
        doctorInfoButton = createMenuButton("个人信息");
        appointmentButton = createMenuButton("挂号处理");
        diagnosisButton = createMenuButton("患者诊断");
        logoutButton = createMenuButton("退出登录");
        admissionFormButton = createMenuButton("入院单");

        menuPanel.add(doctorInfoButton);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(appointmentButton);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(diagnosisButton);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(admissionFormButton);
        menuPanel.add(Box.createVerticalGlue()); // 弹性空间

        menuPanel.add(logoutButton);

        // 创建右侧内容面板
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // 初始化子面板
        doctorInfoPanel = new DoctorInfoPanel(doctor);
        appointmentPanel = new AppointmentPanel(doctor);
        diagnosisPanel = new DiagnosisPanel(doctor);
        admissionFormPanel = new AdmissionFormPanel(doctor);

        // 添加子面板到卡片布局
        contentPanel.add(doctorInfoPanel, "doctorInfo");
        contentPanel.add(appointmentPanel, "appointment");
        contentPanel.add(diagnosisPanel, "diagnosis");
        contentPanel.add(admissionFormPanel, "admissionForm");

        // 显示默认面板
        cardLayout.show(contentPanel, "doctorInfo");

        // 将所有面板添加到主窗口
        add(titlePanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // 默认高亮个人信息按钮
        highlightButton(doctorInfoButton);
    }

    /**
     * 创建左侧菜单按钮
     * @param text 按钮文本
     * @return 按钮对象
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIUtil.NORMAL_FONT);
        button.setBackground(UIUtil.SECONDARY_COLOR);
        button.setForeground(UIUtil.TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(200, 40));

        return button;
    }

    /**
     * 添加事件监听器
     */
    private void addListeners() {
        // 个人信息按钮点击事件
        doctorInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "doctorInfo");
                highlightButton(doctorInfoButton);
            }
        });

        // 挂号处理按钮点击事件
        appointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appointmentPanel.refreshData(); // 刷新数据
                cardLayout.show(contentPanel, "appointment");
                highlightButton(appointmentButton);
            }
        });

        // 患者诊断按钮点击事件
        diagnosisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                diagnosisPanel.refreshData(); // 刷新数据
                cardLayout.show(contentPanel, "diagnosis");
                highlightButton(diagnosisButton);
            }
        });

        // 入院单按钮点击事件
        admissionFormButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                diagnosisPanel.refreshData(); // 刷新数据
                cardLayout.show(contentPanel, "admissionForm");
                highlightButton(admissionFormButton);
            }
        });


        // 退出登录按钮点击事件
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        // 默认高亮个人信息按钮
        highlightButton(doctorInfoButton);
    }

    /**
     * 高亮选中的按钮
     * @param selectedButton 选中的按钮
     */
    private void highlightButton(JButton selectedButton) {
        // 重置所有按钮样式
        doctorInfoButton.setBackground(UIUtil.SECONDARY_COLOR);
        doctorInfoButton.setForeground(UIUtil.TEXT_COLOR);
        appointmentButton.setBackground(UIUtil.SECONDARY_COLOR);
        appointmentButton.setForeground(UIUtil.TEXT_COLOR);
        diagnosisButton.setBackground(UIUtil.SECONDARY_COLOR);
        diagnosisButton.setForeground(UIUtil.TEXT_COLOR);

        // 高亮选中的按钮
        selectedButton.setBackground(UIUtil.PRIMARY_COLOR);
        selectedButton.setForeground(Color.WHITE);
    }

    /**
     * 退出登录
     */
    private void logout() {
        if (UIUtil.showConfirm(this, "确定要退出登录吗？", "确认")) {
            new LoginFrame();
            dispose(); // 关闭当前窗口
        }
    }
}