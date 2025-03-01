package com.dw.gui;

import com.dw.model.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;

public class ScheduleResourcePanel extends JPanel {
    private JTextField tfRoom;
    private JTextField tfEquipment;
    private final Doctor doctor;
    public ScheduleResourcePanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 创建面板标题
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = UIUtil.createTitleLabel("排班资源维护");
        titlePanel.add(titleLabel);

        // 创建信息面板
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));

        // 诊室资源
        JLabel lblRoom = UIUtil.createLabel("诊室分配：");
        tfRoom = UIUtil.createTextField();

        // 设备资源
        JLabel lblEquipment = UIUtil.createLabel("医疗设备：");
        tfEquipment = UIUtil.createTextField();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        infoPanel.add(lblRoom, gbc);
        gbc.gridx = 1;
        infoPanel.add(tfRoom, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        infoPanel.add(lblEquipment, gbc);
        gbc.gridx = 1;
        infoPanel.add(tfEquipment, gbc);

        // 将所有面板添加到主面板
        add(titlePanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
    }
}