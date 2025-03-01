// UserManagementPanel.java
package com.dw.gui.panel.user;

import com.dw.dao.user.UserDao;
import com.dw.gui.panel.other.AppointmentPanel;
import com.dw.model.user.Doctor;
import com.dw.model.user.User;
import com.dw.util.UIUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class UserManagementPanel extends JPanel {
    private JTable userTable;
    private JTextField tfSearch;

    private Doctor doctor;
    private User user;

    public UserManagementPanel(Object object) {

        if (object instanceof Doctor) {
            doctor = (Doctor) object;
        } else if (object instanceof User) {
            user = (User) object;
        }

        initUI();
        loadUserData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题和搜索栏
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(UIUtil.createTitleLabel("用户账户管理"), BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tfSearch = UIUtil.createTextField();
        JButton btnSearch = UIUtil.createButton("搜索");
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        // 用户表格
        userTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(userTable);

        // 操作按钮
        JButton btnAdd = UIUtil.createButton("新建用户");
        JButton btnEdit = UIUtil.createButton("编辑权限");
        JButton btnReset = UIUtil.createButton("重置密码");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnReset);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUserData() {
        // 实现用户数据加载逻辑
    }
}