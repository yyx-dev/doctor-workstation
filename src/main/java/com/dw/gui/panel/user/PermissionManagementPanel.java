// PermissionManagementPanel.java
package com.dw.gui.panel.user;

import com.dw.dao.user.PermissionDao;
import com.dw.dao.user.UserDao;
import com.dw.model.user.Doctor;
import com.dw.model.user.Permission;
import com.dw.model.user.User;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;


import java.sql.SQLException;
import java.util.List;

public class PermissionManagementPanel extends JPanel {
    private JComboBox<User> cbUsers;
    private JList<Permission> permissionList;
    private DefaultListModel<Permission> listModel;

    private Doctor doctor;
    private User user;

    public PermissionManagementPanel(Object object) {
        if (object instanceof Doctor) {
            doctor = (Doctor) object;
        } else if (object instanceof User) {
            user = (User) object;
        }

        initUI();
        loadUserData();
        loadPermissions();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 用户选择
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(new JLabel("选择用户："));
        cbUsers = new JComboBox<>();
        userPanel.add(cbUsers);

        // 权限列表
        listModel = new DefaultListModel<>();
        permissionList = new JList<>(listModel);
        permissionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(permissionList);

        // 操作按钮
        JButton btnSave = UIUtil.createButton("保存权限");
        btnSave.addActionListener(e -> savePermissions());

        add(userPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnSave, BorderLayout.SOUTH);
    }

    private void loadUserData() {
        // 实现用户数据加载
    }

    private void loadPermissions() {
        try {
            List<Permission> permissions = new PermissionDao().getAllPermissions();
            listModel.clear();
            permissions.forEach(listModel::addElement);
        } catch (SQLException e) {
            UIUtil.showError(this, "加载权限数据失败", "数据库错误");
        }
    }

    private void savePermissions() {
        // 实现权限保存逻辑
    }
}