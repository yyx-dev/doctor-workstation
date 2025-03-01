package com.dw.gui.panel.schedule;

import com.dw.dao.schedule.NumberSourceDao;
import com.dw.model.user.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NumberSourcePanel extends JPanel {
    private JSpinner spStartTime;
    private JSpinner spEndTime;
    private JSpinner spInterval;
    private JButton btnGenerate;
    private final Doctor doctor;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public NumberSourcePanel(Doctor doctor) {
        this.doctor = doctor;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(UIUtil.createTitleLabel("号源生成"));

        // 主内容面板
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createTitledBorder("基本信息"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 时间选择组件配置
        spStartTime = createTimeSpinner();
        spEndTime = createTimeSpinner();
        spInterval = new JSpinner(new SpinnerNumberModel(15, 5, 60, 5));

        // 第一行：开始时间
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(UIUtil.createLabel("开始时间："), gbc);

        gbc.gridx = 1;
        contentPanel.add(spStartTime, gbc);

        // 第二行：结束时间
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(UIUtil.createLabel("结束时间："), gbc);

        gbc.gridx = 1;
        contentPanel.add(spEndTime, gbc);

        // 第三行：间隔时间
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(UIUtil.createLabel("间隔（分钟）："), gbc);

        gbc.gridx = 1;
        contentPanel.add(spInterval, gbc);

        // 第四行：生成按钮
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnGenerate = UIUtil.createButton("生成号源");
        btnGenerate.addActionListener(e -> generateNumbers());
        contentPanel.add(btnGenerate, gbc);

        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JSpinner createTimeSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        return spinner;
    }

    private void generateNumbers() {
        try {
            Date startTime = (Date) spStartTime.getValue();
            Date endTime = (Date) spEndTime.getValue();
            int interval = (int) spInterval.getValue();

            if (startTime.after(endTime)) {
                UIUtil.showError(this, "开始时间不能晚于结束时间", "参数错误");
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);

            // 生成时间列表
            while (calendar.getTime().before(endTime)) {
                String timeSlot = timeFormat.format(calendar.getTime());

                // 插入数据库
                new NumberSourceDao().insertTimeSlot(
                        doctor.getId(),
                        timeSlot,
                        new Date() // 当前日期
                );

                calendar.add(Calendar.MINUTE, interval);
            }

            UIUtil.showInfo(this, "成功生成号源", "操作成功");
        } catch (SQLException ex) {
            UIUtil.showError(this, "数据库错误：" + ex.getMessage(), "系统错误");
        } catch (Exception ex) {
            UIUtil.showError(this, "参数错误：" + ex.getMessage(), "输入错误");
        }
    }
}