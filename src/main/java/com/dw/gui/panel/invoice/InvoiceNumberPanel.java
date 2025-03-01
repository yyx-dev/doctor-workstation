// InvoiceNumberPanel.java 发票号管理界面
package com.dw.gui.panel.invoice;

import com.dw.dao.invoice.InvoiceNumberDao;
import com.dw.model.user.Doctor;
import com.dw.util.UIUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class InvoiceNumberPanel extends JPanel {
    private JTextField tfPrefix;
    private JTextField tfStartNum;
    private JTextField tfEndNum;
    private JTextArea taResult;

    public InvoiceNumberPanel(Doctor doctor) {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(UIUtil.createTitleLabel("发票号管理"));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 主表单面板
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("号段分配"));

        formPanel.add(UIUtil.createLabel("发票前缀："));
        tfPrefix = UIUtil.createTextField();
        formPanel.add(tfPrefix);

        formPanel.add(UIUtil.createLabel("起始号码："));
        tfStartNum = UIUtil.createTextField();
        formPanel.add(tfStartNum);

        formPanel.add(UIUtil.createLabel("结束号码："));
        tfEndNum = UIUtil.createTextField();
        formPanel.add(tfEndNum);

        // 结果展示
        formPanel.add(UIUtil.createLabel("生成结果："));
        taResult = UIUtil.createTextArea(5, 30);
        formPanel.add(new JScrollPane(taResult));

        // 操作按钮
        JButton btnGenerate = UIUtil.createButton("生成号段");
        btnGenerate.addActionListener(this::generateNumbers);
        JButton btnCheck = UIUtil.createButton("检查可用性");
        btnCheck.addActionListener(this::checkAvailability);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnGenerate);
        buttonPanel.add(btnCheck);

        // 组装界面
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void generateNumbers(ActionEvent e) {
        try {
            String prefix = tfPrefix.getText().trim();
            int start = Integer.parseInt(tfStartNum.getText());
            int end = Integer.parseInt(tfEndNum.getText());

            int count = new InvoiceNumberDao().allocateNumbers(prefix, start, end);
            taResult.setText(String.format("成功生成 %d 个发票号码\n格式示例：%s%08d",
                    count, prefix, start));

        } catch (NumberFormatException ex) {
            UIUtil.showError(this, "号码必须为数字", "输入错误");
        } catch (SQLException ex) {
            UIUtil.showError(this, "生成失败：" + ex.getMessage(), "数据库错误");
        }
    }

    private void checkAvailability(ActionEvent e) {
        try {
            String result = new InvoiceNumberDao().checkAvailability();
            taResult.setText("可用发票号统计：\n" + result);
        } catch (SQLException ex) {
            UIUtil.showError(this, "查询失败：" + ex.getMessage(), "数据库错误");
        }
    }
}