package com.dw.gui.panel.other;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.dw.model.other.Appointment;
import com.dw.dao.other.AppointmentDao;
import com.dw.util.UIUtil;

/**
 * 主诉面板
 */
public class ChiefComplaintPanel extends JPanel {
    private JTextArea complaintTextArea;
    private JButton submitButton;
    private AppointmentDao appointmentDao = new AppointmentDao();
    private int selectedAppointmentId; // 当前选中的挂号ID

    public ChiefComplaintPanel(int selectedAppointmentId) {
        this.selectedAppointmentId = selectedAppointmentId;
        setLayout(new BorderLayout());
        complaintTextArea = new JTextArea();
        submitButton = new JButton("提交主诉");

        add(new JScrollPane(complaintTextArea), BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        // 添加事件监听器
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitComplaint();
            }
        });
    }

    private void submitComplaint() {
        String complaint = complaintTextArea.getText();
        if (complaint.isEmpty()) {
            UIUtil.showError(this, "主诉不能为空！", "错误");
            return;
        }

        // 更新挂号记录中的主诉
        Appointment appointment = appointmentDao.findById(selectedAppointmentId);
        if (appointment != null) {
            appointment.setChiefComplaint(complaint);
            boolean updated = appointmentDao.update(appointment);
            if (updated) {
                UIUtil.showInfo(this, "主诉提交成功！", "成功");
            } else {
                UIUtil.showError(this, "主诉提交失败，请重试！", "错误");
            }
        } else {
            UIUtil.showError(this, "未找到挂号记录！", "错误");
        }
    }

    public void refreshData() {
        // 刷新主诉数据的逻辑
    }
}