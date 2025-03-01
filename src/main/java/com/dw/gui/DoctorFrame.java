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

    // 右侧内容面板
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // 左侧菜单按钮
    private JButton doctorInfoButton;
    private JButton appointmentButton;
    private JButton diagnosisButton;
    private JButton logoutButton;

    private JButton formMenuButton; // 单据
    private JPanel formButtonMenuPanel; // 单据二级菜单面板
    private JButton admissionFormButton; // 入院单
    private JButton medicalCertificateButton; // 病假单
    private JButton sickLeaveFormButton; // 疾病证明单

    private JButton scheduleMenuButton; // 排班管理
    private JPanel scheduleButtonMenuPanel; // 排班管理二级菜单面板
    private JButton schedulePlanButton; // 排班计划
    private JButton numberSourceButton; // 号源生成
    private JButton scheduleResourceButton; // 排班资源维护

    private JButton medicalRecordMenuButton; // 病案管理
    private JPanel medicalRecordButtonMenuPanel; // 病案管理二级菜单面板
    private JButton createRecordButton; // 病案建立
    private JButton sortRecordButton; // 病案整序
    private JButton trackRecordButton; // 病案示踪
    private JButton supplyRecordButton; // 病案供应

    private JButton clinicMenuButton; // 诊台管理
    private JPanel clinicButtonMenuPanel; // 诊台管理二级菜单面板
    private JButton receptionButton; // 接诊
    private JButton writeRecordButton; // 病历书写
    private JButton diagnosisDocumentButton; // 下诊断书
    private JButton diagnosisCancelButton; // 诊间退好

    // 子面板
    private DoctorInfoPanel doctorInfoPanel;
    private AppointmentPanel appointmentPanel;
    private DiagnosisPanel diagnosisPanel;

    private AdmissionFormPanel admissionFormPanel;
    private MedicalCertificatePanel medicalCertificatePanel;
    private SickLeaveFormPanel sickLeaveFormPanel;
    private SchedulePlanPanel schedulePlanPanel;
    private NumberSourcePanel numberSourcePanel;
    private ScheduleResourcePanel scheduleResourcePanel;
    private CreateRecordPanel createRecordPanel;
    private SortRecordPanel sortRecordPanel;
    private TrackRecordPanel trackRecordPanel;
    private SupplyRecordPanel supplyRecordPanel;
    private ReceptionPanel receptionPanel;
    private WriteRecordPanel writeRecordPanel;
    private DiagnosisDocumentPanel diagnosisDocumentPanel;
    private DiagnosisCancelPanel diagnosisCancelPanel;

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

    private void initUI() {
        // 设置布局
        setLayout(new BorderLayout());

        // 创建标题面板
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIUtil.PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = UIUtil.createTitleLabel("门诊医生工作站");
        titleLabel.setForeground(Color.WHITE);

        JLabel welcomeLabel = UIUtil.createLabel("欢迎您，" + doctor.getName()
                + "医生 (" + doctor.getDepartment() + ")");
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

        // 创建单据管理二级菜单
        formMenuButton = createMenuButton("单据开具");
        admissionFormButton = createMenuButton("  入院单");
        medicalCertificateButton = createMenuButton("  疾病证明单");
        sickLeaveFormButton = createMenuButton("  病假单");
        formButtonMenuPanel = createSubMenuPanel(admissionFormButton, medicalCertificateButton, sickLeaveFormButton);

        // 创建排班管理二级菜单
        scheduleMenuButton = createMenuButton("排班管理");
        schedulePlanButton = createMenuButton("  排班计划");
        numberSourceButton = createMenuButton("  号源生成");
        scheduleResourceButton = createMenuButton("  排班资源维护");
        scheduleButtonMenuPanel = createSubMenuPanel(schedulePlanButton, numberSourceButton, scheduleResourceButton);

        // 创建病案管理二级菜单
        medicalRecordMenuButton = createMenuButton("病案管理");
        createRecordButton = createMenuButton("  病案建立");
        sortRecordButton = createMenuButton("  病案整序");
        trackRecordButton = createMenuButton("  病案示踪");
        supplyRecordButton = createMenuButton("  病案供应");
        medicalRecordButtonMenuPanel = createSubMenuPanel(createRecordButton, sortRecordButton,
                trackRecordButton, supplyRecordButton);

        // 创建诊台管理二级菜单
        clinicMenuButton = createMenuButton("诊台管理");
        receptionButton = createMenuButton("  接诊");
        writeRecordButton = createMenuButton("  病历书写");
        diagnosisDocumentButton = createMenuButton("  下诊断书");
        diagnosisCancelButton = createMenuButton("  诊间退号");
        clinicButtonMenuPanel = createSubMenuPanel(receptionButton, writeRecordButton, diagnosisDocumentButton,
                diagnosisCancelButton);

//        menuPanel.add(doctorInfoButton);
//        menuPanel.add(Box.createVerticalStrut(5));
//        menuPanel.add(appointmentButton);
//        menuPanel.add(Box.createVerticalStrut(5));
//        menuPanel.add(diagnosisButton);
//        menuPanel.add(Box.createVerticalStrut(5));

        // 添加二级菜单按钮和面板
        menuPanel.add(formMenuButton);
        menuPanel.add(formButtonMenuPanel);
        menuPanel.add(Box.createVerticalStrut(5));

        menuPanel.add(scheduleMenuButton);
        menuPanel.add(scheduleButtonMenuPanel);
        menuPanel.add(Box.createVerticalStrut(5));

        menuPanel.add(medicalRecordMenuButton);
        menuPanel.add(medicalRecordButtonMenuPanel);
        menuPanel.add(Box.createVerticalStrut(5));

        menuPanel.add(clinicMenuButton);
        menuPanel.add(clinicButtonMenuPanel);

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
        medicalCertificatePanel = new MedicalCertificatePanel(doctor);
        sickLeaveFormPanel = new SickLeaveFormPanel(doctor);
        schedulePlanPanel = new SchedulePlanPanel(doctor);
        numberSourcePanel = new NumberSourcePanel(doctor);
        scheduleResourcePanel = new ScheduleResourcePanel(doctor);
        createRecordPanel = new CreateRecordPanel(doctor);
        sortRecordPanel = new SortRecordPanel(doctor);
        trackRecordPanel = new TrackRecordPanel(doctor);
        supplyRecordPanel = new SupplyRecordPanel(doctor);
        receptionPanel = new ReceptionPanel(doctor);
        writeRecordPanel = new WriteRecordPanel(doctor);
        diagnosisDocumentPanel = new DiagnosisDocumentPanel(doctor);
        diagnosisCancelPanel = new DiagnosisCancelPanel(doctor);

        // 添加子面板到卡片布局
        contentPanel.add(doctorInfoPanel, "doctorInfo");
        contentPanel.add(appointmentPanel, "appointment");
        contentPanel.add(diagnosisPanel, "diagnosis");
        contentPanel.add(admissionFormPanel, "admissionForm");
        contentPanel.add(medicalCertificatePanel, "medicalCertificate");
        contentPanel.add(sickLeaveFormPanel, "sickLeaveForm");
        contentPanel.add(schedulePlanPanel, "schedulePlan");
        contentPanel.add(numberSourcePanel, "numberSource");
        contentPanel.add(scheduleResourcePanel, "scheduleResource");
        contentPanel.add(createRecordPanel, "createRecord");
        contentPanel.add(sortRecordPanel, "sortRecord");
        contentPanel.add(trackRecordPanel, "trackRecord");
        contentPanel.add(supplyRecordPanel, "supplyRecord");
        contentPanel.add(receptionPanel, "reception");
        contentPanel.add(writeRecordPanel, "writeRecord");
        contentPanel.add(diagnosisDocumentPanel, "diagnosisDocument");
        contentPanel.add(diagnosisCancelPanel, "diagnosisCancel");

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
     *
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

    private JPanel createSubMenuPanel(JButton... buttons) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIUtil.SECONDARY_COLOR);
        for (JButton btn : buttons) {
            panel.add(btn);
            panel.add(Box.createVerticalStrut(5));
        }
        panel.setVisible(false);
        return panel;
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

        // 退出登录按钮点击事件
        logoutButton.addActionListener(e -> logout());

        // 入院单按钮点击事件
        admissionFormButton.addActionListener(e -> {
            admissionFormPanel.refreshData(); // 刷新数据
            cardLayout.show(contentPanel, "admissionForm");
            highlightButton(admissionFormButton);
        });
        // 病假单按钮点击事件
        medicalCertificateButton.addActionListener(e -> {
            medicalCertificatePanel.refreshData(); // 刷新数据
            cardLayout.show(contentPanel, "medicalCertificate");
            highlightButton(medicalCertificateButton);
        });

        // 病证明单按钮点击事件
        sickLeaveFormButton.addActionListener(e -> {
            sickLeaveFormPanel.refreshData(); // 刷新数据
            cardLayout.show(contentPanel, "sickLeaveForm");
            highlightButton(sickLeaveFormButton);
        });

        // 单据菜单按钮点击事件
        formMenuButton.addActionListener(e -> {
            toggleSubMenu(formButtonMenuPanel);
            highlightButton(formMenuButton);
        });

        // 排班管理菜单事件
        scheduleMenuButton.addActionListener(e -> {
            toggleSubMenu(scheduleButtonMenuPanel);
            highlightButton(scheduleMenuButton);
        });

        // 病案管理菜单事件
        medicalRecordMenuButton.addActionListener(e -> {
            toggleSubMenu(medicalRecordButtonMenuPanel);
            highlightButton(medicalRecordMenuButton);
        });

        // 诊台管理菜单事件
        clinicMenuButton.addActionListener(e -> {
            toggleSubMenu(clinicButtonMenuPanel);
            highlightButton(clinicMenuButton);
        });

        // 排班子菜单项事件
        schedulePlanButton.addActionListener(e -> {
            // 切换到排班计划面板，需要先创建并添加到contentPanel
            cardLayout.show(contentPanel, "schedulePlan");
            highlightButton(schedulePlanButton);
        });

        numberSourceButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "numberSource");
            highlightButton(numberSourceButton);
        });

        scheduleResourceButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "scheduleResource");
            highlightButton(scheduleResourceButton);
        });

        // 病案管理子菜单项事件
        createRecordButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "createRecord");
            highlightButton(createRecordButton);
        });

        sortRecordButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "sortRecord");
            highlightButton(sortRecordButton);
        });

        trackRecordButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "trackRecord");
            highlightButton(trackRecordButton);
        });

        supplyRecordButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "supplyRecord");
            highlightButton(supplyRecordButton);
        });

        // 诊台管理子菜单项事件
        receptionButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "reception");
            highlightButton(receptionButton);
        });

        writeRecordButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "writeRecord");
            highlightButton(writeRecordButton);
        });

        diagnosisDocumentButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "diagnosisDocument");
            highlightButton(diagnosisDocumentButton);
        });

        diagnosisCancelButton.addActionListener(e -> {
            cardLayout.show(contentPanel, "diagnosisCancel");
            highlightButton(diagnosisCancelButton);
        });

        // 默认高亮个人信息按钮
        highlightButton(doctorInfoButton);
    }

    private void toggleSubMenu(JPanel subMenu) {
        boolean isVisible = subMenu.isVisible();
        // 隐藏所有二级菜单
        formButtonMenuPanel.setVisible(false);
        scheduleButtonMenuPanel.setVisible(false);
        medicalRecordButtonMenuPanel.setVisible(false);
        clinicButtonMenuPanel.setVisible(false);
        // 切换当前菜单
        subMenu.setVisible(!isVisible);
    }


    /**
     * 高亮选中的按钮
     *
     * @param selectedButton 选中的按钮
     */
    private void highlightButton(JButton selectedButton) {
        // 重置所有按钮样式
        resetButtonStyle(doctorInfoButton);
        resetButtonStyle(appointmentButton);
        resetButtonStyle(diagnosisButton);
        resetButtonStyle(formMenuButton);
        resetButtonStyle(scheduleMenuButton);
        resetButtonStyle(medicalRecordMenuButton);
        resetButtonStyle(clinicMenuButton);
        resetButtonStyle(admissionFormButton);
        resetButtonStyle(medicalCertificateButton);
        resetButtonStyle(sickLeaveFormButton);
        resetButtonStyle(schedulePlanButton);
        resetButtonStyle(numberSourceButton);
        resetButtonStyle(scheduleResourceButton);
        resetButtonStyle(createRecordButton);
        resetButtonStyle(sortRecordButton);
        resetButtonStyle(trackRecordButton);
        resetButtonStyle(supplyRecordButton);
        resetButtonStyle(receptionButton);
        resetButtonStyle(writeRecordButton);
        resetButtonStyle(diagnosisDocumentButton);
        resetButtonStyle(diagnosisCancelButton);

        // 高亮选中的按钮
        selectedButton.setBackground(UIUtil.PRIMARY_COLOR);
        selectedButton.setForeground(Color.WHITE);
    }

    private void resetButtonStyle(JButton button) {
        button.setBackground(UIUtil.SECONDARY_COLOR);
        button.setForeground(UIUtil.TEXT_COLOR);
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