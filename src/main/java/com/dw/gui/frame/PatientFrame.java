package com.dw.gui.frame;

import com.dw.gui.panel.other.*;
import com.dw.model.user.Patient;
import com.dw.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 患者主界面
 */
public class PatientFrame extends JFrame {

    private Patient patient; // 当前登录的患者信息
    private int selectedAppointmentId = -1; // 当前选中的挂号ID

    // 左侧菜单按钮
    private JButton patientInfoButton;
    private JButton appointmentButton;
    private JButton chiefComplaintButton;
    private JButton medicalRecordButton;
    private JButton logoutButton;

    // 右侧内容面板
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // 子面板
    private PatientInfoPanel patientInfoPanel;
    private PatientAppointmentPanel appointmentPanel;
    private RegistrationPanel registrationPanel;
    private PatientMedicalRecordPanel medicalRecordPanel;
    private ChiefComplaintPanel chiefComplaintPanel;

    /**
     * 构造函数
     * @param patient 患者对象
     */
    public PatientFrame(Patient patient) {
        super("门诊医生工作站系统 - 患者界面");
        this.patient = patient;

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

        JLabel welcomeLabel = UIUtil.createLabel("欢迎您，" + patient.getName());
        welcomeLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(welcomeLabel, BorderLayout.EAST);

        // 创建左侧菜单面板
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(UIUtil.SECONDARY_COLOR);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // 创建左侧菜单按钮
        patientInfoButton = createMenuButton("个人信息");
        appointmentButton = createMenuButton("挂号管理");
        chiefComplaintButton = createMenuButton("填写主诉");
        medicalRecordButton = createMenuButton("病历查询");
        logoutButton = createMenuButton("退出登录");

        menuPanel.add(patientInfoButton);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(appointmentButton);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(chiefComplaintButton);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(medicalRecordButton);
        menuPanel.add(Box.createVerticalGlue()); // 弹性空间
        menuPanel.add(logoutButton);

        // 创建右侧内容面板
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // 初始化子面板
        patientInfoPanel = new PatientInfoPanel(patient);
        appointmentPanel = new PatientAppointmentPanel(patient);
        registrationPanel = new RegistrationPanel(patient);
        medicalRecordPanel = new PatientMedicalRecordPanel(patient.getId());
        chiefComplaintPanel = new ChiefComplaintPanel(selectedAppointmentId);

        // 添加子面板到卡片布局
        contentPanel.add(patientInfoPanel, "patientInfo");
        contentPanel.add(appointmentPanel, "appointment");
        contentPanel.add(registrationPanel, "registration");
        contentPanel.add(medicalRecordPanel, "medicalRecord");
        contentPanel.add(chiefComplaintPanel, "chiefComplaint");

        // 显示默认面板
        cardLayout.show(contentPanel, "patientInfo");

        // 将所有面板添加到主窗口
        add(titlePanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
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
        patientInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "patientInfo");
                highlightButton(patientInfoButton);
            }
        });

        // 挂号管理按钮点击事件
        appointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "registration");
                highlightButton(appointmentButton);
            }
        });

        // 填写主诉按钮点击事件
        chiefComplaintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chiefComplaintPanel.refreshData(); // 刷新数据
                cardLayout.show(contentPanel, "chiefComplaint");
                highlightButton(chiefComplaintButton);
            }
        });

        // 病历查询按钮点击事件
        medicalRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                medicalRecordPanel.refreshData(); // 刷新数据
                cardLayout.show(contentPanel, "medicalRecord");
                highlightButton(medicalRecordButton);
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
        highlightButton(patientInfoButton);
    }

    /**
     * 高亮选中的按钮
     * @param selectedButton 选中的按钮
     */
    private void highlightButton(JButton selectedButton) {
        // 重置所有按钮样式
        patientInfoButton.setBackground(UIUtil.SECONDARY_COLOR);
        patientInfoButton.setForeground(UIUtil.TEXT_COLOR);
        appointmentButton.setBackground(UIUtil.SECONDARY_COLOR);
        appointmentButton.setForeground(UIUtil.TEXT_COLOR);
        chiefComplaintButton.setBackground(UIUtil.SECONDARY_COLOR);
        chiefComplaintButton.setForeground(UIUtil.TEXT_COLOR);
        medicalRecordButton.setBackground(UIUtil.SECONDARY_COLOR);
        medicalRecordButton.setForeground(UIUtil.TEXT_COLOR);

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