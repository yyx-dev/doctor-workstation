package com.dw.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UI工具类，提供UI相关的通用方法
 */
public class UIUtil {
    // 颜色常量
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180); // 蓝色主色调
    public static final Color SECONDARY_COLOR = new Color(245, 245, 250); // 浅灰蓝色次要色调
    public static final Color TEXT_COLOR = new Color(50, 50, 50); // 深灰色文本
    public static final Color ERROR_COLOR = new Color(220, 0, 0); // 错误提示色
    public static final Color SUCCESS_COLOR = new Color(0, 128, 0); // 成功提示色

    // 字体常量
    public static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 18);
    public static final Font SUBTITLE_FONT = new Font("微软雅黑", Font.BOLD, 16);
    public static final Font NORMAL_FONT = new Font("微软雅黑", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("微软雅黑", Font.PLAIN, 12);

    // 边框常量
    public static final Border PANEL_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    );

    /**
     * 设置组件的标准样式
     * @param component 要设置样式的组件
     */
    public static void setDefaultStyle(JComponent component) {
        component.setFont(NORMAL_FONT);
        component.setForeground(Color.BLACK);

        if (component instanceof JButton) {
            JButton button = (JButton) component;
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setBorderPainted(true);
            button.setOpaque(true);
        } else if (component instanceof JTextField || component instanceof JPasswordField || component instanceof JTextArea) {
            component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        } else if (component instanceof JPanel) {
            component.setBorder(PANEL_BORDER);
            component.setBackground(Color.WHITE);
        } else if (component instanceof JLabel) {
            component.setForeground(Color.BLACK);
        } else if (component instanceof JComboBox) {
            component.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        }
    }

    /**
     * 获取图标资源
     * @param path 图标资源路径
     * @return 图标对象
     */
    public static ImageIcon getIcon(String path) {
        URL url = UIUtil.class.getResource(path);
        if (url != null) {
            return new ImageIcon(url);
        }
        return null;
    }

    /**
     * 创建标准按钮
     * @param text 按钮文本
     * @return 标准样式的按钮
     */
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        setDefaultStyle(button);
        button.setForeground(Color.BLACK);
        return button;
    }

    /**
     * 创建标准标签
     * @param text 标签文本
     * @return 标准样式的标签
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        setDefaultStyle(label);
        return label;
    }

    /**
     * 创建标准标题标签
     * @param text 标题文本
     * @return 标准样式的标题标签
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    /**
     * 创建标准文本框
     * @return 标准样式的文本框
     */
    public static JTextField createTextField() {
        JTextField textField = new JTextField();
        setDefaultStyle(textField);
        return textField;
    }

    /**
     * 创建标准密码框
     * @return 标准样式的密码框
     */
    public static JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        setDefaultStyle(passwordField);
        return passwordField;
    }

    /**
     * 创建标准组合框
     * @param items 选项数组
     * @return 标准样式的组合框
     */
    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        setDefaultStyle(comboBox);
        return comboBox;
    }

    /**
     * 创建标准面板
     * @return 标准样式的面板
     */
    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        setDefaultStyle(panel);
        return panel;
    }

    /**
     * 显示信息对话框
     * @param parent 父组件
     * @param message 信息内容
     * @param title 对话框标题
     */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示错误对话框
     * @param parent 父组件
     * @param message 错误信息
     * @param title 对话框标题
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 显示确认对话框
     * @param parent 父组件
     * @param message 确认信息
     * @param title 对话框标题
     * @return 用户选择结果（是/否）
     */
    public static boolean showConfirm(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * 解析日期字符串
     * @param dateString 日期字符串
     * @return 解析后的日期对象
     */
    public static Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            showError(null, "无效的日期格式，请使用 yyyy-MM-dd 格式", "错误");
            return null;
        }
    }
}