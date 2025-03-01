package com.dw.gui.panel.other;

import com.dw.dao.user.DoctorDao;
import com.dw.model.user.Doctor;
import com.dw.model.record.MedicalRecord;
import com.dw.util.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 病历查看对话框
 */
public class MedicalRecordDialog extends JDialog {

    private List<MedicalRecord> records; // 病历记录

    // 表格组件
    private JTable recordTable;
    private DefaultTableModel tableModel;

    // 病历详情字段
    private JTextArea diagnosisArea;
    private JTextArea prescriptionArea;

    // 按钮
    private JButton closeButton;

    /**
     * 构造函数
     * @param parent 父窗口
     * @param records 病历记录列表
     */
    public MedicalRecordDialog(Window parent, List<MedicalRecord> records) {
        super(parent, "病历记录", ModalityType.APPLICATION_MODAL);
        this.records = records;

        // 初始化界面
        initUI();

        // 添加事件监听器
        addListeners();

        // 设置窗口属性
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setResizable(true);
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        // 设置布局
        setLayout(new BorderLayout());

        // 创建内容面板
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("历史记录"));

        // 创建表格模型
        String[] columnNames = {"序号", "诊断日期", "科室", "医生"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 设置单元格不可编辑
            }
        };

        // 创建表格
        recordTable = new JTable(tableModel);
        recordTable.setFont(UIUtil.NORMAL_FONT);
        recordTable.setRowHeight(25);
        recordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 设置表格列宽
        recordTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        recordTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        recordTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        recordTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        // 创建滚动面板
        JScrollPane tableScrollPane = new JScrollPane(recordTable);
        tableScrollPane.setBackground(Color.WHITE);

        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // 创建详情面板
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(BorderFactory.createTitledBorder("病历详情"));

        // 创建选项卡
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtil.NORMAL_FONT);

        // 创建诊断选项卡
        JPanel diagnosisPanel = new JPanel(new BorderLayout());
        diagnosisPanel.setBackground(Color.WHITE);

        diagnosisArea = new JTextArea();
        diagnosisArea.setRows(10);
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        diagnosisArea.setEditable(false);
        diagnosisArea.setFont(UIUtil.NORMAL_FONT);

        JScrollPane diagnosisScrollPane = new JScrollPane(diagnosisArea);
        diagnosisScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        diagnosisPanel.add(diagnosisScrollPane, BorderLayout.CENTER);

        // 创建药方选项卡
        JPanel prescriptionPanel = new JPanel(new BorderLayout());
        prescriptionPanel.setBackground(Color.WHITE);

        prescriptionArea = new JTextArea();
        prescriptionArea.setRows(10);
        prescriptionArea.setLineWrap(true);
        prescriptionArea.setWrapStyleWord(true);
        prescriptionArea.setEditable(false);
        prescriptionArea.setFont(UIUtil.NORMAL_FONT);

        JScrollPane prescriptionScrollPane = new JScrollPane(prescriptionArea);
        prescriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        prescriptionPanel.add(prescriptionScrollPane, BorderLayout.CENTER);

        // 添加选项卡
        tabbedPane.addTab("诊断结果", diagnosisPanel);
        tabbedPane.addTab("药方", prescriptionPanel);

        detailPanel.add(tabbedPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        closeButton = UIUtil.createButton("关闭");

        buttonPanel.add(closeButton);

        // 组装所有面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePanel, detailPanel);
        splitPane.setDividerLocation(200);
        splitPane.setBackground(Color.WHITE);

        contentPanel.add(splitPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 添加到对话框
        add(contentPanel);

        // 填充表格数据
        fillTableData();

        // 如果有记录，默认选中第一条
        if (records.size() > 0) {
            recordTable.setRowSelectionInterval(0, 0);
            showRecordDetail(0);
        }
    }

    /**
     * 填充表格数据
     */
    private void fillTableData() {
        // 清空表格数据
        tableModel.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 填充表格数据
        for (int i = 0; i < records.size(); i++) {
            MedicalRecord record = records.get(i);
            Doctor doctor = new DoctorDao().findById(record.getDoctorId());

            Object[] rowData = {
                    i + 1,
                    dateFormat.format(record.getCreatedAt()),
                    doctor != null ? doctor.getDepartment() : "",
                    doctor != null ? doctor.getName() : ""
            };
            tableModel.addRow(rowData);
        }
    }

    /**
     * 显示病历详情
     * @param index 记录索引
     */
    private void showRecordDetail(int index) {
        if (index >= 0 && index < records.size()) {
            MedicalRecord record = records.get(index);
            diagnosisArea.setText(record.getDiagnosis());
            prescriptionArea.setText(record.getPrescription());
        } else {
            diagnosisArea.setText("");
            prescriptionArea.setText("");
        }
    }

    /**
     * 添加事件监听器
     */
    private void addListeners() {
        // 表格行选择事件
        recordTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = recordTable.getSelectedRow();
                if (row != -1) {
                    showRecordDetail(row);
                }
            }
        });

        // 关闭按钮点击事件
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭对话框
            }
        });
    }
}